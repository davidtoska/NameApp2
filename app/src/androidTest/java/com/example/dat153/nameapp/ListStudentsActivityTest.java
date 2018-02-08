package com.example.dat153.nameapp;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

/**
 * Created by cecilie on 02.02.2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ListStudentsActivityTest {

    private String mStudToShow;

    @Rule
    public ActivityTestRule<ListStudentsActivity> mActivityRule =
            new ActivityTestRule<>(ListStudentsActivity.class);

    @Before
    public void before(){
        mStudToShow = "Thomas Reite";
    }

    @Test
    public void testViewsExist_views(){
        onView(withId(R.id.student_name_listview)).check(matches(isDisplayed()));
        onView(withId(R.id.button4)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickedStudentScenario_listView() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is(mStudToShow)))
                .inAdapterView(withId(R.id.student_name_listview))
                .perform(click());

        onView(withText(mStudToShow)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.imageView), withTagValue(is((Object) mStudToShow)))).check(matches(isDisplayed()));
        onView(withId(R.id.textView2)).check(matches(withText(mStudToShow)));
    }
}