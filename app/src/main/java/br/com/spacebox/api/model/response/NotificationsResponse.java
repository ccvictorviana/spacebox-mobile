package br.com.spacebox.api.model.response;

import br.com.spacebox.api.model.request.NotificationFilterRequest;

public class NotificationsResponse {

    private NotificationFilterRequest filter;

    private NotificationResponse[] notifications;

    public NotificationFilterRequest getFilter() {
        return filter;
    }

    public void setFilter(NotificationFilterRequest filter) {
        this.filter = filter;
    }

    public NotificationResponse[] getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationResponse[] notifications) {
        this.notifications = notifications;
    }

}