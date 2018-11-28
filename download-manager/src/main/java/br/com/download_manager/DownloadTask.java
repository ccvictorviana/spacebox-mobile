package br.com.download_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements Runnable {
    private final static AtomicInteger c = new AtomicInteger(0);
    private Handler handler;
    private int notificationId;
    private NotificationManager notificationManager;
    private DownloadManager.Request mRequest;

    public DownloadTask(DownloadManager.Request request) {
        this.mRequest = request;
        notificationId = c.incrementAndGet();
        notificationManager = (NotificationManager) request.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void run() {
        try {
            if (mRequest.getEnableNotification()){
                createPushNotification();
            }
            downloadFile();

        } finally {
            DownloadManager.getInstance().endedThread();
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

    private void createPushNotification() {
        NotificationCompat.Builder builder = createBuilder();
        builder.setContentTitle("Download " + mRequest.getFileName())
                .setContentText("Aguarde enquanto realizamos o download do seu arquivo, quando terminar você será notificado!");

        notificationManager.notify(notificationId, builder.build());
    }

    private void saveFile(byte[] file) {
    }

    private NotificationChannel createChannel() {
        NotificationChannel result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = new NotificationChannel("notify_001", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(result);
        }
        return result;
    }

    public Uri downloadFile() {
        Uri result = null;
        boolean isDownloaded = false;
        NotificationCompat.Builder builder = createBuilder();

        builder.setContentTitle("Baixando " + mRequest.getFileName());
        Request.Builder requestBuilder = new Request.Builder().url(mRequest.getUri())
                .addHeader("Content-Type", "application/json");

        if (mRequest.getAuthorization() != null) {
            requestBuilder.addHeader("Authorization", mRequest.getAuthorization());
        }

        Request request = requestBuilder.build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if (response.code() == 200 || response.code() == 201) {

                InputStream inputStream = null;
                File mediaFile = null;
                try {
                    inputStream = response.body().byteStream();

                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = response.body().contentLength();
                    mediaFile = new File(mRequest.getContext().getFilesDir() + "/" + mRequest.getMimeTypeNormalized(), mRequest.getFileName());

                    if (!mediaFile.exists()) {
                        mediaFile.getParentFile().mkdirs();
                        try {
                            mediaFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    OutputStream output = new FileOutputStream(mediaFile);

                    if (mRequest.getEnableNotification()) {
                        builder.setProgress(100, 0, false);
                    }

                    if (mRequest.getOnBeforeDownload() != null) {
                        Message message = new Message();
                        Bundle b = new Bundle();
                        b.putInt("progress", 0);
                        message.setData(b);
                        mRequest.getOnBeforeDownload().sendMessage(message);
                    }

                    if (mRequest.getEnableNotification()) {
                        notificationManager.notify(notificationId, builder.build());
                    }
                    while (true) {
                        int readed = inputStream.read(buff);

                        if (readed == -1) {
                            break;
                        }
                        output.write(buff, 0, readed);
                        //write buff
                        downloaded += readed;
                        int percent = (int) ((downloaded * 100) / target);
                        if (mRequest.getEnableNotification()) {
                            builder.setProgress(100, percent, false);
                            notificationManager.notify(notificationId, builder.build());
                        }
                        if (mRequest.getOnProgressDownload() != null) {
                            Message message = new Message();
                            Bundle b = new Bundle();
                            b.putLong("downloaded", downloaded);
                            b.putLong("target", target);
                            b.putInt("progress", percent);
                            message.setData(b);
                            mRequest.getOnProgressDownload().sendMessage(message);
                        }
                    }

                    if (mRequest.getUpdateDate() != null) {
                        mediaFile.setLastModified(mRequest.getUpdateDate().getTime());
                    }

                    output.flush();
                    output.close();

                    isDownloaded = downloaded == target;
                    Thread.sleep(1000);
                } catch (IOException ignore) {
                    ignore.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }

                if (isDownloaded) {
                    result = FileProvider.getUriForFile(mRequest.getContext(), DownloadManager.AUTHORITIES, mediaFile);

                    if (mRequest.getEnableNotification()) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_VIEW);
                        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setDataAndType(result, mRequest.getMimeType());
                        builder.setContentIntent(PendingIntent.getActivity(mRequest.getContext(), 0, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                        builder.setContentTitle("Baixado " + mRequest.getFileName());
                        builder.setProgress(0, 0, false);
                        Log.i("DownloadManager", "NotificationId " + notificationId);
                        notificationManager.notify(notificationId, builder.build());
                    }
                    if (mRequest.getOnCompleteDownload() != null) {
                        Bundle b = new Bundle();
                        b.putSerializable("request", mRequest);

                        Message message = Message.obtain();
                        message.obj = result;
                        message.setData(b);
                        mRequest.getOnCompleteDownload().sendMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}