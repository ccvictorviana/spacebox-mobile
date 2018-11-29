package br.com.download_manager;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import br.com.download_manager.interfaces.IDownloadManager;
import br.com.download_manager.interfaces.IDownloadTask;
import br.com.download_manager.model.DownloadManagerRequest;

public class DownloadManager implements IDownloadManager {
    private static final int DEFAULT_POLL_SIZE = 2;
    private static DownloadManager instance = null;
    public static String AUTHORITIES = "download.manager";

    private int qtdThreadRunning;
    private int qtdMaxThreadRunning;
    private final BlockingQueue<IDownloadTask> downloadWorkQueue;

    private DownloadManager() {
        downloadWorkQueue = new LinkedBlockingQueue<>();
    }

    public static DownloadManager getInstance() {
        return getInstance(DEFAULT_POLL_SIZE);
    }

    public static DownloadManager getInstance(int qtdMaxThreadRunning) {
        DownloadManager currentInstance = (instance == null) ? instance = new DownloadManager() : instance;
        currentInstance.qtdMaxThreadRunning = qtdMaxThreadRunning;
        return currentInstance;
    }

    public boolean isCached(Context context, String fileName) {
        return isCached(context, fileName, null);
    }

    public boolean isCached(Context context, String fileName, Date updateDate) {
        return getFileCached(context, fileName, updateDate) != null;
    }

    public void clearCache(Context context, String fileName) {
        File file = getFileCached(context, fileName, null);
        file.delete();
    }

    public Uri getFileUriCached(DownloadManagerRequest request) {
        Uri fileUri = null;
        File file = getFileCached(request.getContext(), request.getFileName(), request.getUpdateDate());

        if (file != null)
            fileUri = FileProvider.getUriForFile(request.getContext(), DownloadManager.AUTHORITIES, file);

        return fileUri;
    }

    public File getFileCached(DownloadManagerRequest request) {
        return getFileCached(request.getContext(), request.getFileName(), request.getUpdateDate());
    }

    public File getFileCached(Context context, String fileName, Date updateDate) {
        File result = null;
        String path = String.format("%s/%s", context.getFilesDir(), DownloadManagerRequest.getMimeTypeNormalized(fileName));
        File file = new File(path, fileName);

        if (file.exists()) {
            Date date = new Date(file.lastModified());
            if (updateDate == null || updateDate.compareTo(date) <= 0)
                result = file;
        }

        return result;
    }

    public void enqueue(DownloadManagerRequest request) {
        DownloadTask task = new DownloadTask(request);
        if (!isStartTask()) {
            qtdThreadRunning++;
            new Thread(task).start();
        } else {
            downloadWorkQueue.add(task);
        }
    }

    synchronized protected void endedThread() {
        qtdThreadRunning--;
        if (!isStartTask() && downloadWorkQueue.size() > 0) {
            try {
                qtdThreadRunning++;
                new Thread(downloadWorkQueue.take()).start();
            } catch (InterruptedException ignore) {
            }
        }
    }

    private boolean isStartTask() {
        return qtdThreadRunning == qtdMaxThreadRunning;
    }
}
