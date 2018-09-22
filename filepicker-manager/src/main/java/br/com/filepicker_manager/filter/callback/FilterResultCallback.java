package br.com.filepicker_manager.filter.callback;

import java.util.List;

import br.com.filepicker_manager.filter.entity.BaseFile;
import br.com.filepicker_manager.filter.entity.Directory;

public interface FilterResultCallback<T extends BaseFile> {
    void onResult(List<Directory<T>> directories);
}
