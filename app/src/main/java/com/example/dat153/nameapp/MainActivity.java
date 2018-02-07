package com.example.dat153.nameapp;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.User;

import static com.example.dat153.nameapp.validators.StringValidator.validName;

/**
 * MAIN CLASS
 *
 * @author internal_cecilie
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Database - instance
     */
    private AppDatabase mDb;
    //private final String PACKAGE_NAME = getApplicationContext().getPackageName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());

        if (!isAppInitialized()) {
            this.initializeApp(mDb);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.menu_settings:
                try {
                    Intent intent = new Intent(this, RegisterAppOwnerActivity.class);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                Log.i(getResources().getString(R.string.menu_item_selected), getResources().getString(R.string.settings));
                return true;


            case R.id.menu_help:
                try {
                    Intent intent = new Intent(this, HelpActivity.class);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                Log.i(getResources().getString(R.string.menu_item_selected), getResources().getString(R.string.quiz));
                return true;
            default:
                return false;
        }
    }


    /**
     * On click displays the list of students.
     *
     * @param view
     */
    public void listStudents(View view) {
        try {
            Intent intent = new Intent(this, ListStudentsActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * On click displays the learning mode game.
     *
     * @param view
     */
    public void learningMode(View view) {
        try {
            Intent intent = new Intent(this, LearningModeActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On click displays the gallery.
     *
     * @param view
     */
    public void viewGallery(View view) {
        try {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Returns true if the owner has registrered with a name.
     *
     * @return true if the owner is registrered.
     */

    public boolean isAppInitialized() {
        boolean isAppInit;
        SharedPreferences sharedPreferences = getSharedPreferences(
                "com.example.dat153.nameapp", Context.MODE_PRIVATE);
        String appOwnerName = sharedPreferences.getString(
                String.valueOf(R.string.key_app_owner_name), null);

        if (appOwnerName == null) {
            isAppInit = false;
        } else {
            boolean isTooShort = validName(appOwnerName);
            boolean isDefault = (appOwnerName.equals(String.valueOf(R.string.default_value_app_owner_name)));
            isAppInit = isTooShort || isDefault;
        }

        return isAppInit;
    }


    /**
     * Sends the user to the registrer owner activity.
     */
    public void initializeApp(AppDatabase db) {
        try {
            new PopulateDbAsyncTask(mDb).execute();
            Intent intent = new Intent(this, RegisterAppOwnerActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;


        PopulateDbAsyncTask(AppDatabase db) {
            this.mDb = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mDb.userDao().insertUser(new User("David Toska", "android.resource://com.example.dat153.nameapp/drawable/internal_david"));
            mDb.userDao().insertUser(new User("Thomas", "android.resource://com.example.dat153.nameapp/drawable/internal_thomas"));
            mDb.userDao().insertUser(new User("Cecilie", "android.resource://com.example.dat153.nameapp/drawable/internal_cecilie"));

            return null;
        }
    }

}
