package com.radicaldroids.mileage;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;

/**
 * Created by Andrew on 1/7/2017.
 */

@RunWith(AndroidJUnit4.class)
public class CalculateEntriesTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void openFillDataPage() throws Exception{

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.station_location)).perform(typeText("Chevron"));
        Espresso.onView(ViewMatchers.withId(R.id.mileage)).perform(typeText("100000"));
        Espresso.onView(ViewMatchers.withId(R.id.gallons)).perform(typeText("14.59"));
        Espresso.onView(ViewMatchers.withId(R.id.price)).perform(typeText("35.62"));

        Espresso.pressBack();
        Espresso.onView(ViewMatchers.withId(R.id.add_record)).perform(click());
    }
}
