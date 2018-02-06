package com.example.dat153.nameapp;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.LoadAllNamesTask;
import com.example.dat153.nameapp.Database.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by internal_cecilie on 31.01.2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class AddStudentActivityTest {

    private UiDevice mDevice;
    private String mFirstName;
    private String mLastName;
    private AppDatabase mDb;

    @Rule
    public ActivityTestRule<AddStudentActivity> mActivityRule =
            new ActivityTestRule<>(AddStudentActivity.class);

    @Before
    public void before() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDb = AppDatabase.getPersistentDatabase(mActivityRule.getActivity().getApplicationContext());
        mFirstName = "Ola";
        mLastName = "Nordmann";
        mActivityRule.getActivity().encodeImage("student.jpg", getDefaultBitmap());
        mActivityRule.getActivity().setImgName("student.jpg");
    }

    @Test
    public void onPermission() throws InterruptedException, UiObjectNotFoundException {
        Intents.init();
        denyPermissionToCamera();
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(createResultCameraScenario());
        onView(withId(R.id.takePictureButton)).perform(click());
        // TODO: adjust "Tillat" to locale
        mDevice.findObject(new UiSelector().text("Tillat")).click();
        Intents.release();
        mActivityRule.getActivity().finish();
    }

    @Test
    public void addStudent() throws UiObjectNotFoundException {
        mActivityRule.getActivity().setImgName("name.jpg");
        mActivityRule.getActivity().setBitmapImage(getDefaultBitmap());

        onView(withId(R.id.inputFirstName)).perform(clearText());
        onView(withId(R.id.inputFirstName)).perform(typeText(mFirstName));
        closeSoftKeyboard();
        onView(withId(R.id.inputLastName)).perform(clearText());
        onView(withId(R.id.inputLastName)).perform(typeText(mLastName));
        closeSoftKeyboard();

        onView(withId(R.id.AddButton)).perform(click());
        // TODO: should add method to check that the correct image is displayed
        mActivityRule.getActivity().finish();
    }

    @Test
    public void testStudentAddedExistsInDB() throws UiObjectNotFoundException, ExecutionException, InterruptedException {
        onView(withId(R.id.inputFirstName)).perform(clearText());
        onView(withId(R.id.inputFirstName)).perform(typeText(mFirstName));
        closeSoftKeyboard();
        onView(withId(R.id.inputLastName)).perform(clearText());
        onView(withId(R.id.inputLastName)).perform(typeText(mLastName));
        closeSoftKeyboard();

        mActivityRule.getActivity().addStudentToDB(getFullName());
        onView(withId(R.id.AddButton)).perform(click());

        /**
         * TODO:
         * add method to find the student in the DB and check whether the name and the image resource is what it's said to be
         * use getFullName() and the default BitMap (should be uri?)
         */
        List<String> userNames = new LoadAllNamesTask(mDb).execute().get();
        assertTrue(userNames.contains(getFullName()));
        mActivityRule.getActivity().finish();
    }

    @Test
    public void testStudentRemovedNotExistsInDB() throws UiObjectNotFoundException {
        /**
         * Method not implemented in the code yet.
         */
    }


    @Test
    public void testCameraScenario() throws InterruptedException {
        Intents.init();
        addPermissionToCamera();
        // Espresso responds with the result
        closeSoftKeyboard();
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(createResultCameraScenario());
        onView(withId(R.id.takePictureButton)).check(matches(isDisplayed()));
        onView(withId(R.id.takePictureButton)).perform(click());
        onView(withId(R.id.ImgPreview)).check(matches(isDisplayed()));
        Intents.release();
        mActivityRule.getActivity().finish();
    }

    /**
     * Grant permission to camera.
     */
    private void addPermissionToCamera(){
        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.CAMERA");
    }

    /**
     * Revokes permission to camera.
     */
    private void denyPermissionToCamera(){
        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm revoke " + getTargetContext().getPackageName()
                        + " android.permission.CAMERA");
    }

    /**
     * Constructs a default bitmap from ic_launcher icon.
     * @return
     */
    private Bitmap getDefaultBitmap(){
        return BitmapFactory.decodeResource(getTargetContext().getResources(), R.mipmap.ic_launcher);
    }

    /**
     * Creates a dummy result for the camera scenario.
     * @return
     */
    private Instrumentation.ActivityResult createResultCameraScenario(){
        Intent intent = new Intent();
        intent.putExtra("data", getDefaultBitmap());
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
        return result;
    }

    /**
     * Returns the concatenated String of firsName and lastName.
     * @return fullname
     */
    private String getFullName(){
        String fullName = mFirstName.concat(" " + mLastName);
        return fullName;
    }


}