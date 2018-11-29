package br.com.download_manager.observers;

import android.content.Context;

import br.com.download_manager.model.DownloadManagerMessage;
import br.com.download_manager.utils.ResourceMessages;

public class NotificationProgressObserver extends NotificationObserver {

    public NotificationProgressObserver(Context context) {
        super(context);
    }

    @Override
    public void update(DownloadManagerMessage message) {
        builder.setProgress(100, message.getProgress(), false)
                .setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADING_TITLE, message.getFileName()));

        notificationManager.notify(message.getNotificationId(), builder.build());
    }
}