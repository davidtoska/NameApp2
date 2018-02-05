package com.example.dat153.nameapp.Database;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads all user from the database.
 */
public class LoadAllUsersTask extends AsyncTask<Void, Void, List<User>> {
    private final AppDatabase mDb;

    public LoadAllUsersTask(AppDatabase db) {
        mDb = db;
    }

    @Override
    protected List<User> doInBackground(Void... voids) {
        List<User> users = mDb.userDao().getAll();
        if(users == null){
            users = new ArrayList<>();
        }
        Log.d("doinbackground: ", "users: " + users.size());
        return users;
    }
}

