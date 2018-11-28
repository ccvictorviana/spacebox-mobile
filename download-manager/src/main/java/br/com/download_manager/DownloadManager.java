package br.com.download_manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.Serializable;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadManager {
    private static final int DEFAULT_POLL_SIZE = 2;

    private int notificationId;
    private int poolSize;
    private int qtdThreadRunning;
    private static DownloadManager instance = null;
    private final BlockingQueue<Runnable> downloadWorkQueue;
    public static String AUTHORITIES = "download.manager";

    private DownloadManager(int poolSize) {
        this.poolSize = poolSize;
        downloadWorkQueue = new LinkedBlockingQueue<>();
    }

    public static DownloadManager getInstance() {
        return (instance == null) ? instance = new DownloadManager(DEFAULT_POLL_SIZE) : instance;
    }

    public void clearCache(Context context, String fileName){
        File file = getFileCached(context, fileName, null);
        file.delete();
    }

    public boolean isCached(Context context, String fileName) {
        return isCached(context, fileName, null);
    }

    public boolean isCached(Context context, String fileName, Date updateDate) {
        return getFileCached(context, fileName, updateDate) != null;
    }

    public void enqueue(Request request) {
        File file = getFileCached(request.getContext(), request.getFileName(), request.getUpdateDate());

        if (file == null) {
            DownloadTask task = new DownloadTask(request);
            if (!isQtdMaxThreadRunning()) {
                qtdThreadRunning++;
                Thread thread = new Thread(task);
                thread.start();
            } else {
                downloadWorkQueue.add(task);
            }
        } else if (request.getOnCachedDownload() != null) {
            Bundle b = new Bundle();
            b.putSerializable("request", request);

            Message message = Message.obtain();
            message.obj = FileProvider.getUriForFile(request.getContext(), AUTHORITIES, file);
            message.setData(b);

            request.getOnCachedDownload().sendMessage(message);
        }
    }

    private File getFileCached(Context context, String fileName, Date updateDate) {
        File result = null;
        File file = new File(context.getFilesDir() + "/" + Request.getMimeTypeNormalized(fileName), fileName);

        if (file.exists()) {
            if (updateDate == null)
                result = file;
            else {
                Date date = new Date(file.lastModified());
                if (updateDate.compareTo(date) <= 0) {
                    result = file;
                }
            }
        }
        return result;
    }

    synchronized protected void endedThread() {
        qtdThreadRunning--;
        if (!isQtdMaxThreadRunning() && downloadWorkQueue.size() > 0) {
            try {
                qtdThreadRunning++;
                Thread thread = new Thread(downloadWorkQueue.take());
                thread.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isQtdMaxThreadRunning() {
        return qtdThreadRunning == poolSize;
    }

    public static class Request implements Serializable {
        private String id;
        private String uri;
        private String fileName;
        private String authorization;
        private boolean enableNotification;
        private Context context;
        private Date updateDate;
        private Handler onCachedDownload;
        private Handler onBeforeDownload;
        private Handler onProgressDownload;
        private Handler onCompleteDownload;

        public Request(String id, String uri, String fileName, String authorization, Date updateDate,
                       boolean enableNotification, Context context, Handler onCachedDownload, Handler onBeforeDownload,
                       Handler onProgressDownload, Handler onCompleteDownload) {
            this.id = id;
            this.uri = uri;
            this.fileName = fileName;
            this.updateDate = updateDate;
            this.authorization = authorization;
            this.enableNotification = enableNotification;
            this.context = context;
            this.onCachedDownload = onCachedDownload;
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

        protected String getMimeTypeNormalized() {
            return Utils.getMimeTypeNormalizedToPath(fileName);
        }

        public static String getMimeTypeNormalized(String fileName) {
            return Utils.getMimeTypeNormalizedToPath(fileName);
        }

        public Handler getOnCachedDownload() {
            return onCachedDownload;
        }

        public Handler getOnBeforeDownload() {
            return onBeforeDownload;
        }

        public Handler getOnProgressDownload() {
            return onProgressDownload;
        }

        public Handler getOnCompleteDownload() {
            return onCompleteDownload;
        }

        public static class Builder {
            private String id;
            private String uri;
            private String fileName;
            private String authorization;
            private boolean enableNotification;
            private Date updateDate;
            private Context context;
            private Handler onCachedDownload;
            private Handler onBeforeDownload;
            private Handler onProgressDownload;
            private Handler onCompleteDownload;

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

            public Builder withOnBeforeDownload(Handler onBeforeDownload) {
                this.onBeforeDownload = onBeforeDownload;
                return this;
            }

            public Builder withOnProgressDownload(Handler onProgressDownload) {
                this.onProgressDownload = onProgressDownload;
                return this;
            }

            public Builder withOnCachedDownload(Handler onCachedDownload) {
                this.onCachedDownload = onCachedDownload;
                return this;
            }

            public Builder withOnCompleteDownload(Handler onCompleteDownload) {
                this.onCompleteDownload = onCompleteDownload;
                return this;
            }

            public Builder withContext(Context context) {
                this.context = context;
                return this;
            }

            public Request build() {
                return new Request(id, uri, fileName, authorization, updateDate, enableNotification, context,
                        onCachedDownload, onBeforeDownload, onProgressDownload, onCompleteDownload);
            }

            public static Builder create() {
                return new Builder();
            }
        }
    }
}
