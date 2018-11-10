package com.example.robmillaci.nytnews;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.robmillaci.nytnews.Activities.MainActivity;
import com.example.robmillaci.nytnews.Activities.SearchActivity;
import com.example.robmillaci.nytnews.Activities.SearchResultsActivity;
import com.example.robmillaci.nytnews.Activities.SettingsActivity;
import com.example.robmillaci.nytnews.Activities.WebActivity;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UITests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.robmillaci.nytnews", appContext.getPackageName());
    }

    @Test
    public void CheckTabLayout() throws InterruptedException {
        ViewInteraction tabLayout = onView(ViewMatchers.withId(R.id.tabLayout));
        tabLayout.check(matches(isEnabled()));

        //wait up to 3 seconds for the data to be downloaded and for the recycler view to be populated
        //3 seconds is used because any longer than this wont be a good user experience
        Thread.sleep(3000);
        //check recycler view contains at least 1 value
        onView(withId(R.id.recyclerview)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void CheckMostPopularTab() throws InterruptedException {
        //click to the 'most popular tab' and check that 1) the sub tab menu is displayed and then the recycler view contains values
        onView(withText("Most Popular")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.popularTabs)).check(matches(isDisplayed()));

        onView(withId(R.id.recyclerview)).check(new RecyclerViewItemCountAssertion(1));

    }

    @Test
    public void CheckBusinessTab() throws InterruptedException {
        //click to the 'business tab' and check that 1) the sub tab menu is displayed and then the recycler view contains values
        onView(withText("Business")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.popularTabs)).check(matches(not(isDisplayed())));

        onView(withId(R.id.recyclerview)).check(new RecyclerViewItemCountAssertion(1));
    }


    @Test
    public void CheckSearchActivity() throws InterruptedException {
        //click on the search activity and check that the search activity class is launched
        Intents.init();
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(1000);

        intended(hasComponent(SearchActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void CheckSettingsActivity() throws InterruptedException {
        //click on the settings menu option and check that the settingsactivity is launched
        Intents.init();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(1000);
        onView(withText("Settings")).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));
        Intents.release();
    }


    @Test
    public void CheckNotificationsSet() throws InterruptedException {
        //check that the notifications are set when the notification switch is turned on
        Intents.init();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(1000);
        onView(withText("Settings")).perform(click());


        try {
            onView(withId(R.id.notificationSwitch)).check(matches(isChecked()));
            assertTrue(SettingsActivity.mBuilder != null);
            assertTrue(SettingsActivity.alarmManager != null);
        } catch (AssertionFailedError e) {
            onView(withId(R.id.notificationSwitch)).perform(click());
            assertTrue(SettingsActivity.mBuilder != null);
            assertTrue(SettingsActivity.alarmManager != null);
        } finally {
            Intents.release();
        }

    }

    @Test
    public void CheckTurnOffNotification() throws InterruptedException {
        //check notification alarm is removed when the notification switch is turned off
        Intents.init();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(1000);
        onView(withText("Settings")).perform(click());

        try {
            onView(withId(R.id.notificationSwitch)).check(matches(isChecked()));
            onView(withId(R.id.notificationSwitch)).perform(click());
            assertTrue(SettingsActivity.mBuilder == null);
            assertTrue(SettingsActivity.alarmManager == null);
        } catch (AssertionFailedError e) {
            assertTrue(SettingsActivity.mBuilder == null);
            assertTrue(SettingsActivity.alarmManager == null);
        } finally {
            Intents.release();
        }

    }

    @Test
    public void CheckWebView() {
        //check that clicking on a recycler view item launches the web activity (webview)
        try {
            Intents.init();
            onView(withId(R.id.recyclerview)).perform(click());
            intended(hasComponent(WebActivity.class.getName()));
        } finally {
            Intents.release();
        }
    }


    //ToDo check search activity results
}
