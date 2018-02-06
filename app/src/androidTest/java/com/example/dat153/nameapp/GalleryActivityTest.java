package com.example.dat153.nameapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.dat153.nameapp.Database.AddUserTask;
import com.example.dat153.nameapp.Database.AppDatabase;
import com.example.dat153.nameapp.Database.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by internal_cecilie on 02.02.2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class GalleryActivityTest {

    private AppDatabase mDb;
    private String mFirstName;
    private String mLastName;

    @Rule
    public ActivityTestRule<GalleryActivity> mActivityRule =
            new ActivityTestRule<>(GalleryActivity.class);

    @Before
    public void before(){
        mDb = AppDatabase.getPersistentDatabase(mActivityRule.getActivity().getApplicationContext());
    }

    @Test
    public void testViewsExist(){
        onView(withId(R.id.studentlist)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddedStudentScenario() throws NullPointerException, InterruptedException {
        Thread.sleep(2000);
        assertEquals(mActivityRule.getActivity().getAdapter().getCount(), 3);

        mActivityRule.getActivity().finish();
    }

    private String getFullName(){
        String fullName = mFirstName.concat(" " + mLastName);
        return fullName;
    }

    private Bitmap getDefaultBitmap(){
        return BitmapFactory.decodeResource(getTargetContext().getResources(), R.mipmap.ic_launcher);
    }

}