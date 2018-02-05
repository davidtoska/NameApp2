package com.example.dat153.nameapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.LoadAllUsersTask;
import com.example.dat153.nameapp.Database.User;
import com.example.dat153.nameapp.adapters.UserAdapter;

public class GalleryActivity extends AppCompatActivity {

    private UserAdapter adapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());

        List<User> users = new ArrayList<>();


        try {
            users = new GetStudentsTask(mDb).execute().get();
            Toast.makeText(this, "Student loaded", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        adapter = new UserAdapter(this, users);
        ListView nameList = this.findViewById(R.id.studentlist);
        nameList.setAdapter(adapter);
    }

    /**
     *  Ayncs anonoymous class for fetching a
     */
    private static class GetStudentsTask extends AsyncTask<Void, Void, List<User>> {
        private final AppDatabase mDb;

        public GetStudentsTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            return mDb.userDao().getAll();
        }
    }


    protected UserAdapter getAdapter(){
        return adapter;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public Bitmap decodeImage(String ImgName){
        Bitmap bitmap;
        String pattern = "\\d*";

        if(ImgName.matches(pattern)){
            // Image is in resources
            int res = Integer.parseInt(ImgName);
            BitmapDrawable temp = (BitmapDrawable) getResources().getDrawable(res);
            bitmap = temp.getBitmap();
        }
        else{
            // Image is store in internal storage
            bitmap = BitmapFactory.decodeFile(ImgName);
        }
        return bitmap;
    }

}
