package br.com.download_manager.observers;

import android.content.Context;

import br.com.download_manager.model.DownloadManagerMessage;
import br.com.download_manager.utils.ResourceMessages;

public class NotificationCreateObserver extends NotificationObserver {

    public NotificationCreateObserver(Context context) {
        super(context);
    }

    @Override
    public void update(DownloadManagerMessage message) {
        notificationManager.notify(message.getNotificationId(), (builder
                .setProgress(100, message.getProgress(), false)
                .setContentText(ResourceMessages.NOTIFICATION_WAIT_DOWNLOAD_COMPLETE)
                .setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADING_TITLE, message.getFileName()))
        ).build());
    }
}