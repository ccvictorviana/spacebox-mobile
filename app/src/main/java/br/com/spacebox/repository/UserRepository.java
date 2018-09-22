package br.com.spacebox.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import br.com.spacebox.entity.User;

@Dao
public interface UserRepository {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    int delete(User user);

    @Query("SELECT * FROM tb_user")
    User[] list();

    @Query("SELECT * FROM tb_user WHERE userId = :id LIMIT 1")
    User getById(int id);
}