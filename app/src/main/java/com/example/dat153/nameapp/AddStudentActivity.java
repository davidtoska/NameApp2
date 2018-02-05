package com.example.dat153.nameapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dat153.nameapp.Database.AddUserTask;
import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.User;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.dat153.nameapp.validators.StringValidator.validName;

/**
 * Adds a new student to the app
 *
 * @author mrreitet
 */
public class AddStudentActivity extends AppCompatActivity {

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
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * @param requestCode int representing what type of request
     * @param resultCode int representing if all went okey
     * @param data the data we asked for
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Preview of image
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmapImage = (Bitmap) extras.get("data");
            }
            ImgPreview.setImageBitmap(bitmapImage);
        }
        // If user wants to choose from gallery
        if(requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK){
            ImgURI = data.getData();
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), ImgURI);
                ImgPreview = findViewById(R.id.ImgPreview);
                ImgPreview.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
            }
        }

        // Save bitmap to image
        encodeImage(ImgName, bitmapImage);
    }


    /**
     * Method that creates a unique image name based upon date and time
     *
     * @return unique image name
     */
    @NonNull
    private String GeneretePictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy-hh/mm", Locale.GERMAN);
        return IMG_PATH + sdf.toString() + IMG_PATH_JPG;
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
            addStudentToDB(name);
            finish();

        } else {
            Toast.makeText(this, R.string.name_not_valid, Toast.LENGTH_SHORT);
            firstName.setHint(R.string.first_name);
            lastName.setHint(R.string.last_name);
        }
    }

    protected void addStudentToDB(String name){
        // Create the student and place him/her in the collection.
        User usr = new User(name, ImgName);

        //Adding the new user with an asyncTask
        new AddUserTask(mDb).execute(usr);
    }

    // TODO:  vurder å flytt dette ut

    /**
     * Method that creates the image file in the format jpg and stores it on internal storage
     * @param ImgName name of picture
     */
    public void encodeImage(String ImgName, Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        try {
            FileOutputStream fos = openFileOutput(ImgName, Context.MODE_PRIVATE);
            fos.write(byteArray);
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that fetches image from internal storage and decodes it to a bitmap
     * @param ImgName name of image
     * @return a bitmap
     */
    public Bitmap decodeImage(String ImgName){
        Bitmap bitmap = null;
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

    public void setImgName(String imageName){
        this.ImgName = imageName;
    }

    public void setBitmapImage(Bitmap image){
        this.bitmapImage = image;
    }
}
