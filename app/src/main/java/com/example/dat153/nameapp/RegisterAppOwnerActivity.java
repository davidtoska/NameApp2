package com.example.dat153.nameapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.dat153.nameapp.validators.StringValidator.validName;


public class RegisterAppOwnerActivity extends AppCompatActivity {

//    private final String TAG = getResources().getString(R.string.register_name);
//    private final String PACKAGE_NAME = getApplicationContext().getPackageName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_app_owner);
        init();
        Toast.makeText(this, getResources().getString(R.string.enter_your_name), Toast.LENGTH_LONG).show();
    }


    public void init(){
        Button addNameButton = findViewById(R.id.AddAppOwnerButton);
        addNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeName(v);
                finish();
            }
        });

        String name = getOwnersName();
        if(name != null){
            EditText editText = findViewById(R.id.ownerNameEditTextField);
            if(editText != null && validName(editText.getText().toString())){
                editText.setText(name);
            }
        }
    }

    public void storeName(View view){
        EditText ownerNameEditTextField = findViewById(R.id.ownerNameEditTextField);

        String ownersName = ownerNameEditTextField.getText().toString();
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.example.dat153.nameapp", Context.MODE_PRIVATE);
        Log.d("RegisterAppOw: ", " : " + ownersName);
        sharedPreferences.edit().putString(String.valueOf(R.string.key_app_owner_name), ownersName).apply();
        Toast.makeText(this, getResources().getString(R.string.name_saved), Toast.LENGTH_LONG).show();
    }

    public String getOwnersName(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.example.dat153.nameapp", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString(
                String.valueOf(R.string.key_app_owner_name), null);
        return name;
    }

}
