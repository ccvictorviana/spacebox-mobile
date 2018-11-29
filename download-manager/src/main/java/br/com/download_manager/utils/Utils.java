package br.com.download_manager.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.net.URLConnection;

import br.com.download_manager.interfaces.IDownloadManagerAction;
import br.com.download_manager.model.DownloadManagerMessage;

public class Utils {
    public static String getMimeType(String fileName) {
        return URLConnection.guessContentTypeFromName(fileName);
    }

    public static String getMimeTypeNormalizedToPath(String fileName) {
        String mimeType = (fileName != null) ? getMimeType(fileName) : "";
        return (mimeType != null) ? mimeType.replaceAll("/", "_") : "";
    }

    public static void executeAction(DownloadManagerMessage message, IDownloadManagerAction action) {
        if (action != null) {
            new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    action.execute(message);
                }
            }.sendMessage(Message.obtain());
        }
    }
}

