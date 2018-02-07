package com.example.dat153.nameapp.Database;

import android.os.AsyncTask;

import java.util.List;




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



