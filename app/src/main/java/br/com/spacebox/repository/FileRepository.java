package br.com.spacebox.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.spacebox.entity.File;

@Dao
public interface FileRepository {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(File file);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<File> files);

    @Update
    void update(File file);

    @Query("DELETE FROM tb_file WHERE id = :fileId")
    void deleteById(Long fileId);

    @Query("SELECT * FROM tb_file WHERE userId = :userId AND (fileParentId = :fileParentId OR fileParentId IS NULL)")
    List<File> list(Long userId, Long fileParentId);
}