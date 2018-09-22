package br.com.spacebox.repository.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import br.com.spacebox.entity.File;
import br.com.spacebox.entity.Notification;
import br.com.spacebox.entity.User;
import br.com.spacebox.repository.FileRepository;
import br.com.spacebox.repository.NotificationRepository;
import br.com.spacebox.repository.UserRepository;

@TypeConverters({Converters.class})
@Database(version = 1, entities = {
        User.class,
        File.class,
        Notification.class
})
public abstract class SpaceBoxDatabase extends RoomDatabase {

    public abstract UserRepository userRepository();

    public abstract FileRepository fileRepository();

    public abstract NotificationRepository notificationRepository();

}