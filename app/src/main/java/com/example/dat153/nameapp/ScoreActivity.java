package com.example.dat153.nameapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        TextView t = (TextView) findViewById(R.id.textView4);

        Intent intent = getIntent();
        int score = intent.getIntExtra(getResources().getString(R.string.score), 0);
        String theScore = Integer.toString(score);
        t.setText(theScore);
    }
}
