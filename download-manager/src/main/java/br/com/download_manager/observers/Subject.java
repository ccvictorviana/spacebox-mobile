package br.com.download_manager.observers;

import br.com.download_manager.model.DownloadManagerMessage;

public interface Subject {

    void register(Observer obj);

    void unregister(Observer obj);

    void notifyObservers(DownloadManagerMessage message);
}
