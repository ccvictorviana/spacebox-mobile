package br.com.download_manager.model;

public interface Subject {

    void register(Observer obj);

    void unregister(Observer obj);

    void notifyObservers(DownloadManagerMessage message);
}
