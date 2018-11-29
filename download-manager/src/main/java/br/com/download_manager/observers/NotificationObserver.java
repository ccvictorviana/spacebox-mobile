package br.com.download_manager.observers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import br.com.download_manager.R;

public abstract class NotificationObserver implements Observer {
    private final static String NOTIFICATION_CHANNEL_ID = "spacebox_channel";
    private final static String NOTIFICATION_CHANNEL_NAME = "Channel Notification of Spacebox.";

    protected Context context;
    protected NotificationCompat.Builder builder;
    protected NotificationManager notificationManager;

    public NotificationObserver(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.builder = createBuilder();
    }

    private NotificationCompat.Builder createBuilder() {
        NotificationChannel channel = createChannel();
        NotificationCompat.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(context, (channel != null) ? channel.getId() : null);
        else
            builder = new NotificationCompat.Builder(context);

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
}
