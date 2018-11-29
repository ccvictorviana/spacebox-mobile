package br.com.download_manager.observers;

import br.com.download_manager.model.DownloadManagerMessage;

@FunctionalInterface
public interface Observer {
    void update(DownloadManagerMessage message);
}