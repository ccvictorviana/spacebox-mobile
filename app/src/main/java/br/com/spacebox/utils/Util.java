package br.com.spacebox.utils;


import android.util.Base64;

import br.com.spacebox.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;

    public static String getFileTypeIcon(String type) {
        Integer result = null;

        if (type == null) {
            result = R.drawable.if_folder_blue;
        } else {
            String extension = (type.contains("/") ? type.split("/")[1] : type);
            switch (extension.toUpperCase()) {
                case "AVIB":
                    result = R.drawable.if_avibs;
                    break;
                case "DOC":
                    result = R.drawable.if_docs;
                    break;
                case "FLAC":
                    result = R.drawable.if_flacs;
                    break;
                case "GIF":
                    result = R.drawable.if_gifs;
                    break;
                case "HTML":
                    result = R.drawable.if_htmls;
                    break;
                case "JPG":
                    result = R.drawable.if_jpgs;
                    break;
                case "JS":
                    result = R.drawable.if_jss;
                    break;
                case "MOV":
                    result = R.drawable.if_movs;
                    break;
                case "MP3":
                    result = R.drawable.if_mp3s;
                    break;
                case "MP4":
                    result = R.drawable.if_mp4s;
                    break;
                case "PDF":
                    result = R.drawable.if_pdfs;
                    break;
                case "PNG":
                    result = R.drawable.if_pngs;
                    break;
                case "PSD":
                    result = R.drawable.if_psds;
                    break;
                case "TIFF":
                    result = R.drawable.if_tiffs;
                    break;
                case "TXT":
                    result = R.drawable.if_txts;
                    break;
                case "XLS":
                    result = R.drawable.if_xlss;
                    break;
                default:
                    result = R.drawable.if_file_unknown;
                    break;
            }
        }

        return Integer.toString(result);
    }

    public static String getNotificationTypeIcon(int type) {
        Integer result = null;

        switch (type) {
            case 1:
                result = R.drawable.if_add;
                break;
            case 2:
                result = R.drawable.if_edit;
                break;
            case 3:
                result = R.drawable.if_delete;
                break;
        }

        return Integer.toString(result);
    }

    public static String formatToSize(Long value) {
        String result = "";
        if (value != null) {
            final long[] dividers = new long[]{T, G, M, K, 1};
            final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};

            if (value < 1)
                throw new IllegalArgumentException("Invalid file size: " + value);

            for (int i = 0; i < dividers.length; i++) {
                final long divider = dividers[i];
                if (value >= divider) {
                    result = format(value, divider, units[i]);
                    break;
                }
            }
        }
        return result;
    }

    private static String format(final long value,
                                 final long divider,
                                 final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.1f %s", Double.valueOf(result), unit);
    }

    public static String formatToDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return dateFormat.format(date);
    }

    public static String encodeFileToBase64Binary(String fileName) {
        String result = null;
        try {
            File file = new File(fileName);
            byte[] bytes = loadFile(file);
            result = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception ex) {

        }

        return result;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
}
