package br.com.download_manager.observers;

import android.content.Context;

import br.com.download_manager.model.DownloadManagerMessage;

public class NotificationProgressObserver extends NotificationObserver {

    public NotificationProgressObserver(Context context) {
        super(context);
    }

    @Override
    public void update(DownloadManagerMessage message) {
        builder.setProgress(100, message.getProgress(), false);
        notificationManager.notify(message.getNotificationId(), builder.build());
    }
}