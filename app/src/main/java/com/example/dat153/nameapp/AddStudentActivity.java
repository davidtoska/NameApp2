package com.example.dat153.nameapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.dat153.nameapp.validators.StringValidator.validName;

/**
 * Adds a new student to the app
 *
 * @author mrreitet
 */
public class AddStudentActivity extends AppCompatActivity {

    private final String TAG = "AddStudentAct";

    // Unique code for requesting the capture of a image
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // Unique code for choosing a image from gallery
    private static final int CHOOSE_FROM_GALLERY = 2;

    // Unique code for requesting camera permissions on runtime
    private static final int PERMISSION_REQUEST_CAMERA = 3;

    // Holds a reference to the ImgageView used to display image preview
    private ImageView ImgPreview;

    // URI for the image
    private Uri ImgURI;

    // Path to image
    private String imgPath;

    // Bitmap image of the student
    private Bitmap bitmapImage;

    // Name of image
    private String ImgName;

    // Database
    private AppDatabase mDb;

    // imgage path
    private final String IMG_PATH= "NameApp_";

    // image path jpg
    private final String IMG_PATH_JPG = ".jpg";

    private Uri imgURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        ImgPreview = findViewById(R.id.ImgPreview);
        mDb = AppDatabase.getPersistentDatabase(this.getApplicationContext());
    }

    /**
     * Method called when take picture button is pressed.
     *
     * @param v the view
     */
    public void OnClickedTakePictureButton(View v) {
        // Check for camera permission
        CheckCameraPermission();
    }

    /**
     * Method called when select image from gallery is pressed.
     * @param v the view
     */
    public void OnClickedSelectImageButton(View v){
        dispatchSelectImageIntent();
    }

    /**
     * Creates the intent and starts the activity
     */
    private void dispatchSelectImageIntent() {
        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);

        if(selectImageIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(selectImageIntent, CHOOSE_FROM_GALLERY);
        }
    }

    /**
     * Create the takePictureIntent and starts the activity.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File image = createFile();
            if (image != null) {
                imgURI = FileProvider.getUriForFile(this, "com.example.dat153.nameapp.fileprovider", image);
                takePictureIntent.putExtra("return-data", true);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Metod creating the image file
     * @return
     */
    public File createFile(){
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
     * @param requestCode int representing what type of request
     * @param resultCode int representing if all went okey
     * @param data the data we asked for
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
                ImgPreview.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
            }
        }

        // If user wants to choose from gallery
        if(requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK){
            //ImgURI = data.getData();
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), ImgURI);
                ImgPreview.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
            }
            imgPath = getPathFromURI(data.getData());
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    /**
     * Method that creates a unique image name based upon date and time
     *
     * @return unique image name
     */
    @NonNull
    private String GeneretePictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy-hh/mm", Locale.GERMAN);
        return (IMG_PATH + sdf.toString() + IMG_PATH_JPG).toLowerCase();
    }

    /**
     * Method to add a student to the collection.
     *
     * @param v the view
     */
    public void OnClickedAddStudent(View v) {
        // Get the name
        EditText firstName = findViewById(R.id.inputFirstName);
        EditText lastName = findViewById(R.id.inputLastName);

        String name = firstName.getText() + " " + lastName.getText();

        if(validName(name)) {
            Log.d("encodeImg: ", String.valueOf(ImgName));
            addStudentToDB(name);

            // Potensial fix to get the list updated
            try {
                Intent intent = new Intent(this, ListStudentsActivity.class);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
            finish();

        } else {
            Toast.makeText(this, R.string.name_not_valid, Toast.LENGTH_SHORT).show();
            firstName.setHint(R.string.first_name);
            lastName.setHint(R.string.last_name);
        }
    }

    protected void addStudentToDB(String name){
        // Create the student and place him/her in the collection.
        User usr = new User(name, imgPath);
        Log.d(TAG, "imgPath" + imgPath);

        //Adding the new user with an asyncTask
        new AddUserTask(mDb).execute(usr);
    }

    /**
     * Method that check if we have camera permission and if not requests it.
     */
    private void CheckCameraPermission() {
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
     * Requests the {@link android.Manifest.permission#CAMERA} permission.
     */
    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.CAMERA)) {

            // Request the permission
            ActivityCompat.requestPermissions(AddStudentActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

    /**
     * Method that handles the result of the permission request.
     * @param requestCode int representing the what request it is
     * @param permissions the permission
     * @param grantResults the result of the request for permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show();

                // Takes the picture
                dispatchTakePictureIntent();
            } else {
                // Permission request was denied.
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Add student to DB.
     */
    private static class AddUserTask extends AsyncTask<User, Void,Void> {

        private final AppDatabase mDb;

        AddUserTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(User... users) {
            mDb.userDao().insertUser(users[0]);
            return null;
        }
    }

    public void setImgPath(String imageName){
        this.imgPath = imageName;
    }

    public void setBitmapImage(Bitmap image){
        this.bitmapImage = image;
    }



}
