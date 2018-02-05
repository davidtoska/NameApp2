package com.example.dat153.nameapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.LoadAllNamesTask;
import com.example.dat153.nameapp.Database.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListStudentsActivity extends AppCompatActivity {

    private final String TAG = "ListStudentAct;";
    private AppDatabase mDb;

    private ArrayAdapter<String> adapter;
    private List<String> userNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        //Getting a list of all student names.
        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());
        userNames = new ArrayList<>();

        //Loading user names from database
        try {
            userNames = new LoadAllNamesTask(mDb).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(adapter == null){
            Log.d(TAG, getResources().getString(R.string.creating_new_arrayadapter));
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, userNames);
        }
     }

    @Override
    protected void onResume() {
        super.onResume();
        //Getting elements from the view.
        Button b = findViewById(R.id.button4);
        ListView listView = findViewById(R.id.student_name_listview);

        //Getting a list of all student names.
        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());
        userNames = new ArrayList<>();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListStudentsActivity.this, ViewStudentActivity.class);
                String studentName = adapter.getItem(position);
                Log.d(TAG, getResources().getString(R.string.student_clicked) + studentName);
                intent.putExtra(getResources().getString(R.string.name), studentName);
                startActivity(intent);
            }
        });

        listView.setAdapter(adapter);
    }

    /**
     * On click displays the learning mode game.
     * @param view
     */
    public void addStudentMode(View view){
        try {
            Intent intent = new Intent(this, AddStudentActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadAllUsersTask extends AsyncTask<Void, Void, List<User>> {
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
}
