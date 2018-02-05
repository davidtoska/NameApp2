package com.example.dat153.nameapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by cecilie on 01.02.2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityTest {

    private SharedPreferences mSharedPreferences;
    private String mKeyAppOwnerName;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before(){
        mSharedPreferences =
                mActivityRule.getActivity().getSharedPreferences("com.example.dat153.nameapp", Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(String.valueOf(R.string.key_app_owner_name), null).commit();
        mKeyAppOwnerName = "Ola Nordmann";
    }

    @Test
    public void testViewsVisible_views(){
        onView(withId(R.id.button)).check(matches(isDisplayed()));
        onView(withId(R.id.button2)).check(matches(isDisplayed()));
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
    }
    /**
     * Test that gets you directed to the RegisterAppOwnerActivity if not registered key_app_owner_name in shared preferences.
     */
    @Test
    public void testIsNotRegisteredScenario_sharedPreferences() throws InterruptedException {
        assertEquals(mSharedPreferences.getString(String.valueOf(R.string.key_app_owner_name), null), null);
        onView(withId(R.id.ownerNameEditTextField)).perform(typeText(mKeyAppOwnerName));
        closeSoftKeyboard();
        onView(withId(R.id.AddAppOwnerButton)).perform(click());
        assertEquals(mSharedPreferences.getString(String.valueOf(R.string.key_app_owner_name), null), mKeyAppOwnerName);
        mSharedPreferences.edit().putString(String.valueOf(R.string.key_app_owner_name), null).commit();
    }
}