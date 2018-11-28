package br.com.download_manager;

import java.net.URLConnection;

public class Utils {
    public static String getMimeType(String fileName) {
        return URLConnection.guessContentTypeFromName(fileName);
    }

    public static String getMimeTypeNormalizedToPath(String fileName) {
        String mimeType = (fileName != null) ? getMimeType(fileName) : "";
        return (mimeType != null) ? mimeType.replaceAll("/", "_") : "";
    }
}

