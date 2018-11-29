package br.com.download_manager.model;

@FunctionalInterface
public interface Observer {
    void update(DownloadManagerMessage message);
}