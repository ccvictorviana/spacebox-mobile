package br.com.download_manager;

import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.download_manager.interfaces.IDownloadTask;
import br.com.download_manager.model.DownloadManagerMessage;
import br.com.download_manager.model.DownloadManagerRequest;
import br.com.download_manager.observers.ENotificationObserverType;
import br.com.download_manager.observers.NotificationObserverFactory;
import br.com.download_manager.observers.NotificationSubject;
import br.com.download_manager.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements IDownloadTask {
    private int notificationId;
    private DownloadManagerRequest mRequest;
    private DownloadManagerMessage message = new DownloadManagerMessage();
    private final static AtomicInteger c = new AtomicInteger(0);
    private NotificationSubject notiSubjectCreate = new NotificationSubject();
    private NotificationSubject notiSubjectProgress = new NotificationSubject();
    private NotificationSubject notiSubjectComplete = new NotificationSubject();

    public DownloadTask(DownloadManagerRequest request) {
        mRequest = request;
        registerNotificationSubjectCreate();
        registerNotificationSubjectProgress();
        registerNotificationSubjectComplete();

        if (request.getEnableNotification()) {
            notificationId = c.incrementAndGet();
            message.setNotificationId(notificationId);
        }
    }

    private void registerNotificationSubjectCreate() {
        notiSubjectCreate.register((message) -> Utils.executeAction(message, mRequest.getOnBeforeDownload()));

        if (mRequest.getEnableNotification()) {
            notiSubjectCreate.register(NotificationObserverFactory
                    .getNotificationObserver(
                            ENotificationObserverType.NotificationCreateObserver, mRequest.getContext()));
        }
    }

    private void registerNotificationSubjectProgress() {
        notiSubjectProgress.register((message) -> Utils.executeAction(message, mRequest.getOnProgressDownload()));

        if (mRequest.getEnableNotification()) {
            notiSubjectProgress.register(NotificationObserverFactory
                    .getNotificationObserver(
                            ENotificationObserverType.NotificationProgressObserver, mRequest.getContext()));
        }
    }

    private void registerNotificationSubjectComplete() {
        notiSubjectComplete.register((message) -> Utils.executeAction(message, mRequest.getOnCompleteDownload()));

        if (mRequest.getEnableNotification()) {
            notiSubjectComplete.register(NotificationObserverFactory
                    .getNotificationObserver(
                            ENotificationObserverType.NotificationCompleteObserver, mRequest.getContext()));
        }
    }

    @Override
    public void run() {
        try {
            Uri fileUri = DownloadManager.getInstance().getFileUriCached(mRequest);

            if (fileUri == null) {
                downloadFile();
            } else if (mRequest.getOnCompleteDownload() != null) {
                message.setFile(fileUri);
                message.setComplete(true);
                message.setFromCache(true);
                message.setMimeType(mRequest.getMimeType());
                notiSubjectComplete.notifyObservers(message);
            }
        } finally {
            DownloadManager.getInstance().endedThread();
        }
    }

    public void downloadFile() {
        Request request = buildRequest();
        OkHttpClient client = new OkHttpClient();

        message.setProgress(0);
        notiSubjectCreate.notifyObservers(message);

        try {
            boolean isDownloaded = false;
            Response response = client.newCall(request).execute();
            boolean isResponseOK = response.code() == 200 || response.code() == 201;

            if (isResponseOK) {
                File mediaFile = createFile();
                InputStream inputStream = null;

                try {
                    inputStream = response.body().byteStream();
                    OutputStream output = new FileOutputStream(mediaFile);
                    isDownloaded = readStream(inputStream, output, response);

                    if (mRequest.getUpdateDate() != null)
                        mediaFile.setLastModified(mRequest.getUpdateDate().getTime());

                    output.flush();
                    output.close();
                    Thread.sleep(1000);
                } catch (IOException ignore) {
                    ignore.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null)
                        inputStream.close();
                }

                if (isDownloaded) {
                    Uri fileUri = FileProvider.getUriForFile(mRequest.getContext(), DownloadManager.AUTHORITIES, mediaFile);
                    message.setFile(fileUri);
                    message.setProgress(100);
                    message.setComplete(true);
                    message.setFromCache(false);
                    message.setMimeType(mRequest.getMimeType());

                    notiSubjectComplete.notifyObservers(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request buildRequest() {
        Request.Builder requestBuilder = new Request.Builder()
                .url(mRequest.getUri())
                .addHeader("Content-Type", "application/json");

        if (mRequest.getAuthorization() != null)
            requestBuilder.addHeader("Authorization", mRequest.getAuthorization());

        return requestBuilder.build();
    }

    private File createFile() throws IOException {
        File mediaFile = new File(mRequest.getContext().getFilesDir() + "/" + mRequest.getMimeTypeNormalized(), mRequest.getFileName());

        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
            mediaFile.createNewFile();
        }

        return mediaFile;
    }

    private boolean readStream(InputStream inputStream, OutputStream output, Response response) throws IOException {
        long downloaded = 0;
        byte[] buff = new byte[1024 * 4];
        long target = response.body().contentLength();

        while (true) {
            int readed = inputStream.read(buff);
            if (readed == -1)
                break;

            output.write(buff, 0, readed);
            downloaded += readed;

            message.setProgress((int) ((downloaded * 100) / target));
            notiSubjectProgress.notifyObservers(message);
        }

        return downloaded == target;
    }
}