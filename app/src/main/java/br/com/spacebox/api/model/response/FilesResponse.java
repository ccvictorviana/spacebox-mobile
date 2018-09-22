package br.com.spacebox.api.model.response;

import br.com.spacebox.api.model.request.FileFilterRequest;

public class FilesResponse {
    private FileFilterRequest filter;
    private FileSummaryResponse[] files;

    public FileFilterRequest getFilter() {
        return filter;
    }

    public void setFilter(FileFilterRequest filter) {
        this.filter = filter;
    }

    public FileSummaryResponse[] getFiles() {
        return files;
    }

    public void setFiles(FileSummaryResponse[] files) {
        this.files = files;
    }
}
