package com.example.dat153.nameapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.LoadAllNamesTask;
import com.example.dat153.nameapp.Database.LoadAllUsersTask;
import com.example.dat153.nameapp.Database.User;
import com.example.dat153.nameapp.util.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static android.transition.Fade.IN;

/**
 * Class that runs the learning game.
 * @author cecilie
 */

public class LearningModeActivity extends AppCompatActivity {

    private final Random mRandom = new Random();
    private List<User> allStudents;
    private List<String> allStudentNames;
    private User randomStudent;
    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    private String selectedName;
    private int removeIndex;
    private int gameScore;
    private Fade mFade = new Fade(IN);
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_mode);

        spinner = findViewById(R.id.spinner);
        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());
        allStudents = new ArrayList<>();
        allStudentNames = new ArrayList<>();

        try {
            allStudents = new LearningModeActivity.GetStudentsTask(mDb).execute().get();
            Toast.makeText(this, "Student loaded", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        allStudentNames = ApplicationUtils.getStudentNames(allStudents);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allStudentNames);
        gameScore = 0;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedName = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(LearningModeActivity.this, R.string.name_required, Toast.LENGTH_SHORT).show();
            }
        });
        spinner.setAdapter(adapter);
    }

    /**
     *  Ayncs anonoymous class for fetching a
     */
    private class GetStudentsTask extends AsyncTask<Void, Void, List<User>> {
        private final AppDatabase mDb;

        public GetStudentsTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            return mDb.userDao().getAll();
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        runGame(null);
    }

    /**
     * Method that runs the learning mode game. The user gets a picture and have to guess the name of the student in the picture.
     * If there are remaining students to guess the name of the method fetches this, if not the game is ended.
     */
    public void runGame(View view){
        if(!allStudents.isEmpty()){
            ImageView imageView = findViewById(R.id.imageView2);
         
            fadeOut(imageView);
            enableSpinner();
            spinner.setBackgroundColor(getResources().getColor(R.color.primary_material_light_1));
            randomStudent = getRandomStudent();
            imageView.setImageBitmap(decodeImage(randomStudent.getImgName()));
            fadeIn(imageView);

        } else {
            quit();
        }
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);
    }

    public Bitmap decodeImage(String ImgName){
        Bitmap bitmap;
        String pattern = "\\d*";

        if(ImgName.matches(pattern)){
            // Image is in resources
            Log.d("SE HER SE HER", "ImgName: " + ImgName);
            int res = Integer.parseInt(ImgName);
            Log.d("SE HER SE HER", "int: " + res);
            BitmapDrawable temp = (BitmapDrawable) this.getApplicationContext().getResources().getDrawable(res);
            bitmap = temp.getBitmap();
        }
        else{
            // Image is store in internal storage
            bitmap = BitmapFactory.decodeFile(ImgName);
        }
        return bitmap;
    }

    /**
     * Method that creates a fade in feature to the ImageView.
     * @param iv
     */
    private void fadeIn(ImageView iv){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(2000);
        iv.startAnimation(fadeIn);
    }

    /**
     * Method that creates a fade out feature to the ImageView.
     * @param iv
     */
    private void fadeOut(ImageView iv){
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(2000);
        iv.startAnimation(fadeIn);
    }

    /**
     * Method that checks if the user has guessed the right name of the student. Removes the students from the list that the user
     * has guessed correctly.
     */
    public void checkGuess(View view){
        if(allStudentNames.contains(selectedName)){
            if(randomStudent.getName().equals(selectedName)){
                Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show();
                spinner.setBackgroundColor(getResources().getColor(R.color.green));
                allStudents.remove(removeIndex);
                allStudentNames.remove(removeIndex);
                addScore();
                disableSpinner();
            } else {
                Toast.makeText(this, R.string.try_again, Toast.LENGTH_SHORT).show();
                spinner.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }
    }

    /**
     * Triggered if the user pushes the "Quit" button in the game and the game is ended.
     * @param view the view
     */
    public void quitLearningMode(View view){
        quit();
    }

    /**
     * Ends game and the user is sent to a new activity that shows the score of the game.
     */
    private void quit(){
        try {
            Intent intent = new Intent(this, ScoreActivity.class);
            intent.putExtra(getResources().getString(R.string.score), gameScore);
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets a random student from the list
     * @return random student
     */
    public User getRandomStudent(){
        int randomIndex = mRandom.nextInt(allStudents.size());
        User rs = allStudents.get(randomIndex);
        // index that stores the later removal of the index if user guesses correctly
        removeIndex = randomIndex;
        return rs;
    }

    /**
     * Increases the game score
     * @return game score
     */
    private int addScore(){
        return gameScore++;
    }

    /**
     * Enables spinner
     */
    private void enableSpinner(){
        spinner.setEnabled(true);
        spinner.setClickable(true);
    }

    /**
     * Disables spinner
     */
    private void disableSpinner(){
        spinner.setEnabled(false);
        spinner.setClickable(false);
    }

    public List<String> getAllStudentNames(){
        return allStudentNames;
    }

    public int getGameScore(){
        return gameScore;
    }
}