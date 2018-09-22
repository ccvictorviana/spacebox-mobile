package br.com.spacebox.repository.utils;

import android.arch.persistence.room.Room;
import android.content.Context;

public class SpaceBoxDatabaseCreate {
    private static SpaceBoxDatabaseCreate instance;
    private SpaceBoxDatabase dataBase;

    public SpaceBoxDatabaseCreate(Context context) {
        dataBase = Room.databaseBuilder(context, SpaceBoxDatabase.class, "spaceBoxDB").build();
    }

    public static SpaceBoxDatabaseCreate getInstance(Context context) {
        if (instance == null)
            instance = new SpaceBoxDatabaseCreate(context);

        return instance;
    }

    public SpaceBoxDatabase getDataBase() {
        return dataBase;
    }
}
