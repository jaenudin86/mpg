package com.radicaldroids.mileage;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.core.deps.dagger.internal.DoubleCheckLazy;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public void fillData() {
        List<TestRecord> testRecords = createTestRecord(10, 12015, 12.3, 235, 35.75);
        fillRecordWithData(testRecords);
    }

    /**
     *
     * @param numOfEntries number of entries to create
     * @param startingMileage starting mileage
     * @param gallons gallons used in filling the vehicle
     * @param milesBetweenFills
     * @param fillPrice total price of fill
     * @return
     */
    private List<TestRecord> createTestRecord(int numOfEntries, int startingMileage, Double gallons, int milesBetweenFills, Double fillPrice) {
        List<TestRecord> tsList = new ArrayList<>();

        for (int i = 0; i < numOfEntries; i ++) {
            TestRecord testRecord = new TestRecord();
            Random randomNum = new Random();

            switch (randomNum.nextInt(4)) {
                case 1:
                    testRecord.setStation("Chevron");
                    break;
                case 2:
                    testRecord.setStation("Shell");
                    break;
                case 3:
                    testRecord.setStation("Exxon Mobil");
                    break;
                default:
                    testRecord.setStation("cheap gas on corner");
                    break;
            }

            testRecord.setGallons(String.valueOf(gallons + randomNum.nextDouble()));
            testRecord.setMileage(String.valueOf(milesBetweenFills * (i + 1) + startingMileage + randomNum.nextInt(18)));
            testRecord.setPrice(String.valueOf(fillPrice + randomNum.nextDouble()));
            tsList.add(testRecord);
        }

        return tsList;
    }

    private void fillRecordWithData(List<TestRecord> testRecordList) {

        for (TestRecord testRecord:testRecordList) {

            Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(click());

            Espresso.onView(ViewMatchers.withId(R.id.station_location)).perform(typeText(testRecord.getStation()));
            Espresso.onView(ViewMatchers.withId(R.id.mileage)).perform(typeText(testRecord.getMileage()));
            Espresso.onView(ViewMatchers.withId(R.id.gallons)).perform(typeText(testRecord.getGallons()));
            Espresso.onView(ViewMatchers.withId(R.id.price)).perform(typeText(testRecord.getPrice()));

            Espresso.pressBack();
            Espresso.onView(ViewMatchers.withId(R.id.add_record)).perform(click());
        }
    }

    @Test
    public void fillOnEntry() throws Exception{

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.station_location)).perform(typeText("Chevron"));
        Espresso.onView(ViewMatchers.withId(R.id.mileage)).perform(typeText("100000"));
        Espresso.onView(ViewMatchers.withId(R.id.gallons)).perform(typeText("14.59"));
        Espresso.onView(ViewMatchers.withId(R.id.price)).perform(typeText("35.62"));

        Espresso.pressBack();
        Espresso.onView(ViewMatchers.withId(R.id.add_record)).perform(click());
    }
}
