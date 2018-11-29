package br.com.download_manager.interfaces;

import br.com.download_manager.model.DownloadManagerMessage;

@FunctionalInterface
public interface IDownloadManagerAction {
    void execute(DownloadManagerMessage response);
}
