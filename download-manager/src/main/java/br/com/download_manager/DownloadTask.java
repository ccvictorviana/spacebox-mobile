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
import br.com.download_manager.model.DownloadManagerRequest;
import br.com.download_manager.model.DownloadManagerMessage;
import br.com.download_manager.utils.ResourceMessages;
import br.com.download_manager.utils.Utils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements IDownloadTask {
    private int notificationId;
    private DownloadManagerRequest mRequest;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private final static AtomicInteger c = new AtomicInteger(0);

    public DownloadTask(DownloadManagerRequest request) {
        mRequest = request;
        notificationId = c.incrementAndGet();
        notificationManager = (NotificationManager) request.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void run() {
        try {
            Uri fileUri = DownloadManager.getInstance().getFileUriCached(mRequest);

            if (fileUri == null)
                downloadFile();
            else if (mRequest.getOnCompleteDownload() != null)
                notifyDownloadComplete(fileUri, true);

        } finally {
            DownloadManager.getInstance().endedThread();
        }
    }

    public void downloadFile() {
        showInitialNotification();

        boolean isDownloaded = false;
        Request request = buildRequest();
        OkHttpClient client = new OkHttpClient();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if (response.code() == 200 || response.code() == 201) {

                File mediaFile = createFile();
                InputStream inputStream = null;

                try {
                    inputStream = response.body().byteStream();
                    OutputStream output = new FileOutputStream(mediaFile);

                    showDownloadStartNotification();

                    if (mRequest.getOnBeforeDownload() != null) {
                        DownloadManagerMessage message = new DownloadManagerMessage();
                        message.setProgress(0);
                        Utils.executeAction(message, mRequest.getOnBeforeDownload());
                    }

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
                    showDownloadCompleteNotification(fileUri);
                    notifyDownloadComplete(fileUri, false);
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
            result = new NotificationChannel("spacebox_channel", "Channel Spacebox", NotificationManager.IMPORTANCE_DEFAULT);
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
        long downloaded = 0;
        byte[] buff = new byte[1024 * 4];
        long target = response.body().contentLength();

        while (true) {
            int readed = inputStream.read(buff);
            if (readed == -1)
                break;

            output.write(buff, 0, readed);
            downloaded += readed;

            int percent = (int) ((downloaded * 100) / target);

            showDownloadProgressNotification(percent);
            notifyProgressDownload(percent);
        }

        return downloaded == target;
    }

    private void showInitialNotification() {
        if (mRequest.getEnableNotification()) {
            builder = createBuilder()
                    .setContentTitle(String.format(ResourceMessages.NOTIFICATION_TITLE, mRequest.getFileName()))
                    .setContentText(ResourceMessages.NOTIFICATION_WAIT_DOWNLOAD_COMPLETE);

            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void showDownloadStartNotification() {
        if (mRequest.getEnableNotification()) {
            builder.setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADING_TITLE, mRequest.getFileName()));
            builder.setProgress(100, 0, false);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void showDownloadProgressNotification(int percent) {
        if (mRequest.getEnableNotification()) {
            builder.setProgress(100, percent, false);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void showDownloadCompleteNotification(Uri fileUri) {
        if (mRequest.getEnableNotification()) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_VIEW);
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(fileUri, mRequest.getMimeType());

            builder.setContentIntent(PendingIntent.getActivity(mRequest.getContext(), 0, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT));
            builder.setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADED_TITLE, mRequest.getFileName()));
            builder.setProgress(0, 0, false);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void notifyProgressDownload(int percent) {
        if (mRequest.getOnProgressDownload() != null) {
            DownloadManagerMessage message = new DownloadManagerMessage();
            message.setProgress(percent);
            Utils.executeAction(message, mRequest.getOnProgressDownload());
        }
    }

    private void notifyDownloadComplete(Uri fileUri, boolean isFromCache) {
        if (mRequest.getOnCompleteDownload() != null) {
            DownloadManagerMessage message = new DownloadManagerMessage();
            message.setFile(fileUri);
            message.setComplete(true);
            message.setFromCache(isFromCache);
            message.setMimeType(mRequest.getMimeType());
            Utils.executeAction(message, mRequest.getOnCompleteDownload());
        }
    }
}