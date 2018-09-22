package br.com.spacebox.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import br.com.spacebox.entity.Notification;
import br.com.spacebox.entity.User;

@Dao
public interface NotificationRepository {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Notification notification);

    @Delete
    int delete(User notification);

    @Query("SELECT * FROM tb_notification")
    Notification[] list();

}