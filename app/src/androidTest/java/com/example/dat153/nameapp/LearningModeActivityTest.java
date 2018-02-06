package com.example.dat153.nameapp;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
import static android.support.test.espresso.matcher.ViewMatchers.hasBackground;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by internal_cecilie on 30.01.2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class LearningModeActivityTest {

    private String mSelectionText1;
    private String mSelectionText2;
    private String mSelectionText3;
    private Integer spinnerId;

    @Rule
    public ActivityTestRule<LearningModeActivity> mActivityRule =
            new ActivityTestRule<>(LearningModeActivity.class);

    @Before
    public void before(){
        mSelectionText1 = "Thomas Reite";
        mSelectionText2 = "David Toska";
        mSelectionText3 = "Cecilie Gjørøy";
        spinnerId = R.id.spinner;
    }

    /**
     * Tests that the views in the layout to be tested exists.
     */
    @Test
    public void testViewsExist_view() throws NoMatchingViewException{
        Intents.init();
        onView(withId(R.id.spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.relativeLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.button5)).check(matches(isDisplayed()));
        onView(withId(R.id.button6)).check(matches(isDisplayed()));
        onView(withId(R.id.button7)).check(matches(isDisplayed()));
        onView(withId(R.id.viewSwitcher)).check(matches(isDisplayed()));
        onView(withId(R.id.imageView2)).check(matches(isDisplayed()));
        Intents.release();
    }

    /**
     * Instrumented test on checking if the selected name in the learning game matches the name it is supposed to match.
     */
    @Test
    public void testCorrectNameSelected_spinner() throws NoMatchingViewException{
        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(mActivityRule.getActivity().getRandomStudent().getName()))).perform(click());
        onView(withId(spinnerId)).check(matches(withSpinnerText(containsString(mActivityRule.getActivity().getRandomStudent().getName()))));
    }

    /**
     * Instrumented test on checking if the selected name in the learning game responds correctly to wrong name selected.
     */
    @Test
    public void testWrongNameSelected_spinner() throws NoMatchingViewException{
        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(getWrongNameToGuess()))).perform(click());
        onView(withText(mActivityRule.getActivity().getRandomStudent().getName())).check(matches(not(isDisplayed())));
    }

    /**
     * Method to check that the color of the spinner is green when the user has correctly guessed the name of the student.
     */
    @Test
    public void testColorCorrect_spinner() throws NoMatchingViewException{
        final int corrAnswerBackground = R.color.green;

        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(mActivityRule.getActivity().getRandomStudent().getName()))).perform(click());
        onView(withId(R.id.button5)).perform(click());
        onView(withId(spinnerId)).check(matches(hasBackground(corrAnswerBackground)));
    }

    /**
     * Runs the game on a list of three students. Checks that the score is 3 afterwards.
     */
    @Test
    public void testScore_game() throws NoMatchingViewException{
        assertEquals(mActivityRule.getActivity().getGameScore(), 0);

        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(mActivityRule.getActivity().getRandomStudent().getName()))).perform(click());
        onView(withId(R.id.button5)).perform(click()); // guess
        onView(withId(R.id.button6)).perform(click()); // next

        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(mActivityRule.getActivity().getRandomStudent().getName()))).perform(click());
        onView(withId(R.id.button5)).perform(click());
        onView(withId(R.id.button6)).perform(click());

        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(mActivityRule.getActivity().getRandomStudent().getName()))).perform(click());
        onView(withId(R.id.button5)).perform(click());
        onView(withId(R.id.button6)).perform(click());

        onView(withId(R.id.textView4)).check(matches(withText(Integer.toString(mActivityRule.getActivity().getGameScore()))));
    }

    private String getWrongNameToGuess(){
        List<String> list = new ArrayList<>(mActivityRule.getActivity().getAllStudentNames());
        list.remove(mActivityRule.getActivity().getRandomStudent().getName());
        return list.get(0);
    }
}