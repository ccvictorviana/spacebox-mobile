package br.com.download_manager.model;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;

import br.com.download_manager.interfaces.IDownloadManagerAction;
import br.com.download_manager.utils.Utils;

public class DownloadManagerRequest implements Serializable {
    private String id;
    private String uri;
    private String fileName;
    private String authorization;
    private boolean addHeaderJSON;
    private boolean enableNotification;
    private Context context;
    private Date updateDate;
    private IDownloadManagerAction onCompleteDownload;
    private IDownloadManagerAction onBeforeDownload;
    private IDownloadManagerAction onProgressDownload;

    public DownloadManagerRequest(String id, String uri, String fileName, String authorization, Date updateDate,
                                  boolean enableNotification, boolean addHeaderJSON, Context context, IDownloadManagerAction onBeforeDownload,
                                  IDownloadManagerAction onProgressDownload, IDownloadManagerAction onCompleteDownload) {
        this.id = id;
        this.uri = uri;
        this.fileName = fileName;
        this.updateDate = updateDate;
        this.authorization = authorization;
        this.addHeaderJSON = addHeaderJSON;
        this.enableNotification = enableNotification;
        this.context = context;
        this.onBeforeDownload = onBeforeDownload;
        this.onProgressDownload = onProgressDownload;
        this.onCompleteDownload = onCompleteDownload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAuthorization() {
        return authorization;
    }

    public boolean getEnableNotification() {
        return enableNotification;
    }

    public Context getContext() {
        return context;
    }

    public String getMimeType() {
        return Utils.getMimeType(fileName);
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getMimeTypeNormalized() {
        return Utils.getMimeTypeNormalizedToPath(fileName);
    }

    public static String getMimeTypeNormalized(String fileName) {
        return Utils.getMimeTypeNormalizedToPath(fileName);
    }

    public IDownloadManagerAction getOnBeforeDownload() {
        return onBeforeDownload;
    }

    public IDownloadManagerAction getOnProgressDownload() {
        return onProgressDownload;
    }

    public IDownloadManagerAction getOnCompleteDownload() {
        return onCompleteDownload;
    }

    public boolean isAddHeaderJSON() {
        return addHeaderJSON;
    }

    public void setAddHeaderJSON(boolean addHeaderJSON) {
        this.addHeaderJSON = addHeaderJSON;
    }

    public static class Builder {
        private String id;
        private String uri;
        private Date updateDate;
        private Context context;
        private String fileName;
        private String authorization;
        private boolean addHeaderJSON;
        private boolean enableNotification;
        private IDownloadManagerAction onBeforeDownload;
        private IDownloadManagerAction onProgressDownload;
        private IDownloadManagerAction onCompleteDownload;

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withEnableNotification(boolean enableNotification) {
            this.enableNotification = enableNotification;
            return this;
        }

        public Builder withUpdateDate(Date updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withAuthorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder withOnBeforeDownload(IDownloadManagerAction onBeforeDownload) {
            this.onBeforeDownload = onBeforeDownload;
            return this;
        }

        public Builder withOnProgressDownload(IDownloadManagerAction onProgressDownload) {
            this.onProgressDownload = onProgressDownload;
            return this;
        }

        public Builder withOnCompleteDownload(IDownloadManagerAction onCompleteDownload) {
            this.onCompleteDownload = onCompleteDownload;
            return this;
        }

        public Builder withContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder withAddHeaderJSON(boolean addHeaderJSON) {
            this.addHeaderJSON = addHeaderJSON;
            return this;
        }

        public DownloadManagerRequest build() {
            return new DownloadManagerRequest(id, uri, fileName, authorization, updateDate, enableNotification, addHeaderJSON, context,
                    onBeforeDownload, onProgressDownload, onCompleteDownload);
        }

        public static Builder create() {
            return new Builder();
        }
    }
}
