package br.com.download_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
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
import br.com.download_manager.model.NotificationSubject;
import br.com.download_manager.utils.ResourceMessages;
import br.com.download_manager.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements IDownloadTask {
    private int notificationId;
    private DownloadManagerRequest mRequest;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private final static AtomicInteger c = new AtomicInteger(0);
    private final static String NOTIFICATION_CHANNEL_ID = "spacebox_channel";
    private final static String NOTIFICATION_CHANNEL_NAME = "Channel Notification of Spacebox.";

    private NotificationSubject notiSubjectBefore = new NotificationSubject();
    private NotificationSubject notiSubjectProgress = new NotificationSubject();
    private NotificationSubject notiSubjectComplete = new NotificationSubject();

    public DownloadTask(DownloadManagerRequest request) {
        mRequest = request;
        notificationId = c.incrementAndGet();
        notificationManager = (NotificationManager) request.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        registerNotificationSubjectBefore();
        registerNotificationSubjectProgress();
        registerNotificationSubjectComplete();
    }

    private void registerNotificationSubjectBefore() {
        notiSubjectBefore.register((message) -> {
            Utils.executeAction(message, mRequest.getOnBeforeDownload());
        });
        notiSubjectBefore.register((message) -> {
            if (mRequest.getEnableNotification()) {
                builder = createBuilder()
                        .setProgress(100, message.getProgress(), false)
                        .setContentText(ResourceMessages.NOTIFICATION_WAIT_DOWNLOAD_COMPLETE)
                        .setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADING_TITLE, mRequest.getFileName()));

                notificationManager.notify(notificationId, builder.build());
            }
        });
    }

    private void registerNotificationSubjectProgress() {
        notiSubjectProgress.register((message) -> {
            Utils.executeAction(message, mRequest.getOnProgressDownload());
        });

        notiSubjectProgress.register((message) -> {
            if (mRequest.getEnableNotification()) {
                builder.setProgress(100, message.getProgress(), false);
                notificationManager.notify(notificationId, builder.build());
            }
        });
    }

    private void registerNotificationSubjectComplete() {
        notiSubjectComplete.register((message) -> {
            Utils.executeAction(message, mRequest.getOnCompleteDownload());
        });
        notiSubjectComplete.register((message) -> {
            if (mRequest.getEnableNotification() && !message.isFromCache()) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_VIEW);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(message.getFile(), mRequest.getMimeType());

                builder.setContentIntent(PendingIntent.getActivity(mRequest.getContext(), 0, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                builder.setContentText(null);
                builder.setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADED_TITLE, mRequest.getFileName()));
                builder.setProgress(0, 0, false);
                notificationManager.notify(notificationId, builder.build());
            }
        });
    }

    @Override
    public void run() {
        try {
            Uri fileUri = DownloadManager.getInstance().getFileUriCached(mRequest);

            if (fileUri == null)
                downloadFile();
            else if (mRequest.getOnCompleteDownload() != null) {
                DownloadManagerMessage message = new DownloadManagerMessage();
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
        DownloadManagerMessage message = new DownloadManagerMessage();
        message.setProgress(0);
        notiSubjectBefore.notifyObservers(message);

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

    private NotificationCompat.Builder createBuilder() {
        NotificationChannel channel = createChannel();
        NotificationCompat.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(mRequest.getContext(), (channel != null) ? channel.getId() : null);
        } else {
            builder = new NotificationCompat.Builder(mRequest.getContext());
        }

        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder;
    }

    private NotificationChannel createChannel() {
        NotificationChannel result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(result);
        }
        return result;
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
        DownloadManagerMessage message = new DownloadManagerMessage();
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