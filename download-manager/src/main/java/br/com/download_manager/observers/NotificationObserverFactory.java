package br.com.download_manager.observers;

import android.content.Context;

public class NotificationObserverFactory {
    public static Observer getNotificationObserver(ENotificationObserverType observerType, Context context) {
        Observer result = null;

        switch (observerType) {
            case NotificationCreateObserver:
                result = new NotificationCreateObserver(context);
                break;
            case NotificationProgressObserver:
                result = new NotificationProgressObserver(context);
                break;
            case NotificationCompleteObserver:
                result = new NotificationCompleteObserver(context);
                break;
        }

        return result;
    }
}
