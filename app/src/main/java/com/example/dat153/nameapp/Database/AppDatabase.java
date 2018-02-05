package com.example.dat153.nameapp.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by mrreitet on 01.02.2018.
 * This Class can deliver an In-memory-database for testing, og
 * a persistent database for the application.
 */

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase IN_MEMORY_INSTANCE;
    private static AppDatabase PERSISTENT_INSTANCE;

    public abstract UserDao userDao();

    public static AppDatabase getInMemoryDatabase(Context context) {
        if (IN_MEMORY_INSTANCE == null) {
            IN_MEMORY_INSTANCE = Room.inMemoryDatabaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class).build();
        }
        return IN_MEMORY_INSTANCE;
    }

    public static AppDatabase getPersistentDatabase(Context context) {
        if (PERSISTENT_INSTANCE == null) {
            PERSISTENT_INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "nameapp_database"
            ).allowMainThreadQueries().build();
        }

        return PERSISTENT_INSTANCE;
    }

}

