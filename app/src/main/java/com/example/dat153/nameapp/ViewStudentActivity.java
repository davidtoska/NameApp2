package com.example.dat153.nameapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.User;
import com.example.dat153.nameapp.util.ApplicationUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewStudentActivity extends AppCompatActivity {
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);
        Intent intent = getIntent();
        String name = intent.getStringExtra(getResources().getString(R.string.name));

        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());

        //Should this be in a Task?
        User thisUser = null;
        try {
            thisUser = new LoadUserByName(mDb).execute(name).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //User thisUser = mDb.userDao().loadUserByName(name);
        ImageView imageView = findViewById(R.id.imageView);

        imageView.setImageBitmap(decodeImage(thisUser.getImgPath(), 256));
        imageView.setTag(thisUser.getName());
        TextView textView = findViewById(R.id.textView2);
        textView.setText(name);
    }

    private Bitmap decodeImage(String imgPath, final int THUMBSIZE) {
        Bitmap thumbImage = null;
        Drawable temp = null;
        if (imgPath.contains("internal")) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(imgPath));
                temp = BitmapDrawable.createFromStream(inputStream, imgPath);
                Bitmap bitmap = ((BitmapDrawable) temp).getBitmap();
                thumbImage = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (imgPath.contains("tmp")) {
            try {
                thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgPath), THUMBSIZE, THUMBSIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return thumbImage;
    }

    /**
     * Loads all user from the database.
     */
    private static class LoadUserByName extends AsyncTask<String, Void, User> {
        private final AppDatabase mDb;

        LoadUserByName(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected User doInBackground(String... strings) {
            User user = new User();
            if (strings.length != 0) {
                user = mDb.userDao().loadUserByName(strings[0]);
            }

            Log.d("doinbackground: ", "users: " + user.getName());
            return user;
        }
    }
}
