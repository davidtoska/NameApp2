package com.example.dat153.nameapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.dat153.nameapp.validators.StringValidator.validName;


public class RegisterAppOwnerActivity extends AppCompatActivity {




    private final String TAG = "RegAppOwner";
    // Unique code for requesting camera permissions on runtime

    // Unique code for requesting the capture of a image
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //Unique code for requesting camera permissions on runtime.
    private static final int PERMISSION_REQUEST_CAMERA = 3;


    // Bitmap image of the student
    private Bitmap bitmapImage;

    // Holds a reference to the ImgageView used to display image preview
    private ImageView ImgPreviewOwner;

    // Path to image
    private String imgPath;

    // imgage path
    private final String IMG_PATH= "NameApp_";

    // image path jpg
    private final String IMG_PATH_JPG = ".jpg";

    private Uri imgURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_app_owner);
        init();
        Toast.makeText(this, getResources().getString(R.string.enter_your_name), Toast.LENGTH_LONG).show();
    }

    public void OnClickedTakePictureButton(View v) {
        // Check for camera permission
        CheckCameraPermission();
    }

    public void init(){
        Button addNameButton = findViewById(R.id.AddAppOwnerButton);
        ImgPreviewOwner = findViewById(R.id.ImgPreviewOwner);
        addNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeOwner(v);
                finish();
            }
        });

        //Get name of owner if excists (null otherwise)
        String name = getOwnersName();
        if(name != null){
            EditText editText = findViewById(R.id.ownerNameEditTextField);
            if(editText != null && validName(name)){
                editText.setText(name);
            }
        }
    }

    public void storeOwner(View view){
        EditText ownerNameEditTextField = findViewById(R.id.ownerNameEditTextField);

        String ownersName = ownerNameEditTextField.getText().toString();
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.example.dat153.nameapp", Context.MODE_PRIVATE);
        Log.d(TAG, " : " + ownersName);
        Log.d(TAG, "AppOwner uri: " + imgPath);
        sharedPreferences.edit().putString(String.valueOf(R.string.key_app_owner_name), ownersName).apply();
        Toast.makeText(this, getResources().getString(R.string.name_saved), Toast.LENGTH_LONG).show();
    }

    /**
     * Retrives the owners name from shared preferences if registered.
     * Null othervise.
     * @return String og null.
     */
    public String getOwnersName(){
        String ownersName = null;
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.example.dat153.nameapp", Context.MODE_PRIVATE);

        if(sharedPreferences != null){
            ownersName = sharedPreferences.getString(
                    String.valueOf(R.string.key_app_owner_name), null);
        }
        return ownersName;
    }

    /**
     * Method that check if we have camera permission and if not requests it.
     */
    private void CheckCameraPermission() {
        Log.d(TAG, "Checking camara permissions");

        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Takes the picture
            dispatchTakePictureIntent();
        }
        else{
            // Permission is missing and must be requested.
            requestCameraPermission();
        }
    }

    /**
     * Create the takePictureIntent and starts the activity.
     */
    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTAkePictureIntent()");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File image = createFile();
            if (image != null) {
                imgURI = FileProvider.getUriForFile(this, "com.example.dat153.nameapp.fileprovider", image);
                takePictureIntent.putExtra("return-data", true);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.d(TAG, "dispatchTakePic.. after start activity for result");
            }
        }
    }


    /**
     * Requests the {@link android.Manifest.permission#CAMERA} permission.
     */
    private void requestCameraPermission() {
        Log.d(TAG, "requestCameraPermission");
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Request the permission
            ActivityCompat.requestPermissions(RegisterAppOwnerActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }


    /**
     * Metod creating the image file
     * @return
     */
    public File createFile(){
        Log.d(TAG, "createFile");
        String fileName = GeneretePictureName();

        File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try{
            image = File.createTempFile(fileName,/* suffix */ null,fileDir/* directory */);
            imgPath = image.getAbsolutePath();
        }
        catch(Exception e){
            Log.d("IOerror", "File not created");
        }
        return image;
    }

    /**
     * Method that creates a unique image name based upon date and time
     *
     * @return unique image name
     */
    @NonNull
    private String GeneretePictureName() {
        Log.d(TAG, "generatePictureName");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy-hh/mm", Locale.GERMAN);
        Log.d(TAG, sdf.toString());
        return (IMG_PATH + sdf.toString() + IMG_PATH_JPG).toLowerCase();
    }

    /**
     * @param requestCode int representing what type of request
     * @param resultCode int representing if all went okey
     * @param data the data we asked for
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
                Log.d(TAG, bitmapImage.toString());
                Log.d(TAG, "imgPreOw: " + ImgPreviewOwner.toString());
                Log.d(TAG, ImgPreviewOwner.toString());
                ImgPreviewOwner.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
            }
        }
    }

}
