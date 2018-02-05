package com.example.dat153.nameapp.Database;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.02.2018.
 */

public class LoadAllNamesTask extends AsyncTask<Void, Void, List<String>> {
    private final AppDatabase mDb;

    public LoadAllNamesTask(AppDatabase mDb) {
        this.mDb = mDb;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> res;
        res = mDb.userDao().loadAllUserNames();
        return res;
    }
}



