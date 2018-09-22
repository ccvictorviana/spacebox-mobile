package br.com.filepicker_manager.filter;

import android.support.v4.app.FragmentActivity;

import br.com.filepicker_manager.filter.callback.FileLoaderCallbacks;
import br.com.filepicker_manager.filter.callback.FilterResultCallback;
import br.com.filepicker_manager.filter.entity.AudioFile;
import br.com.filepicker_manager.filter.entity.ImageFile;
import br.com.filepicker_manager.filter.entity.NormalFile;
import br.com.filepicker_manager.filter.entity.VideoFile;

public class FileFilter {
    public static void getImages(FragmentActivity activity, FilterResultCallback<ImageFile> callback) {
        activity.getSupportLoaderManager().initLoader(0, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_IMAGE));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback) {
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_VIDEO));
    }

    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback) {
        activity.getSupportLoaderManager().initLoader(2, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_AUDIO));
    }

    public static void getFiles(FragmentActivity activity,
                                FilterResultCallback<NormalFile> callback, String[] suffix) {
        activity.getSupportLoaderManager().initLoader(3, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_FILE, suffix));
    }
}
