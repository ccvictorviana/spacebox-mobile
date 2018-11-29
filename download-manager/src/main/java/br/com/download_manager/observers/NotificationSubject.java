package br.com.download_manager.observers;

import java.util.ArrayList;
import java.util.List;

import br.com.download_manager.model.DownloadManagerMessage;

public class NotificationSubject implements Subject {
    private List<Observer> observers;

    public NotificationSubject() {
        this.observers = new ArrayList<>();
    }

    @Override
    synchronized public void register(Observer obj) {
        if (obj == null)
            throw new IllegalArgumentException("Null Observer");

        if (!observers.contains(obj))
            observers.add(obj);
    }

    @Override
    synchronized public void unregister(Observer obj) {
        observers.remove(obj);
    }

    @Override
    synchronized public void notifyObservers(DownloadManagerMessage message) {
        for (Observer obj : observers)
            obj.update(message);
    }
}