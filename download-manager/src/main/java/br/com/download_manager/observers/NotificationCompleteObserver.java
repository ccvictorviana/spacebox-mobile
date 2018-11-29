package br.com.download_manager.observers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import br.com.download_manager.model.DownloadManagerMessage;
import br.com.download_manager.utils.ResourceMessages;

public class NotificationCompleteObserver extends NotificationObserver {

    public NotificationCompleteObserver(Context context) {
        super(context);
    }

    @Override
    public void update(DownloadManagerMessage message) {
        if (!message.isFromCache()) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_VIEW);
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(message.getFile(), message.getMimeType());

            PendingIntent contentIntent = PendingIntent.getActivity(message.getContext(),
                    0, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(contentIntent);
            builder.setContentText(null);
            builder.setContentTitle(String.format(ResourceMessages.NOTIFICATION_DOWNLOADED_TITLE, message.getFileName()));
            builder.setProgress(0, 0, false);
            notificationManager.notify(message.getNotificationId(), builder.build());
        }
    }
}