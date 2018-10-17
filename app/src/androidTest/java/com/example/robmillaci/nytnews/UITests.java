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
import com.example.robmillaci.nytnews.Activities.SettingsActivity;
import com.example.robmillaci.nytnews.Activities.WebActivity;
import junit.framework.AssertionFailedError;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class UITests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void AuseAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.robmillaci.nytnews", appContext.getPackageName());
    }

    @Test
    public void ACheckTabLayout() throws InterruptedException {
        ViewInteraction tabLayout = onView(ViewMatchers.withId(R.id.tabLayout));
        tabLayout.check(matches(isEnabled()));

        //wait up to 3 seconds for the data to be downloaded and for the recycler view to be populated
        //3 seconds is used because any longer than this wont be a good user experience
        Thread.sleep(3000);
        //check recycler view contains at least 1 value
        onView(withId(R.id.recyclerview)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void BCheckMostPopularTab() throws InterruptedException {
        //click to the 'most popular tab' and check that 1) the sub tab menu is displayed and then the recycler view contains values
        onView(withText("Most Popular")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.popularTabs)).check(matches(isDisplayed()));

        onView(withId(R.id.recyclerview)).check(new RecyclerViewItemCountAssertion(1));

    }

    @Test
    public void CCheckBusinessTab() throws InterruptedException {
        //click to the 'business tab' and check that 1) the sub tab menu is displayed and then the recycler view contains values
        onView(withText("Business")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.popularTabs)).check(matches(not(isDisplayed())));

        onView(withId(R.id.recyclerview)).check(new RecyclerViewItemCountAssertion(1));
    }


    @Test
    public void DCheckSearchActivity() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(1000);

        intended(hasComponent(SearchActivity.class.getName()));
        Intents.release();
    }


    @Test
    public void ECheckSettingsActivity() throws InterruptedException {
        Intents.init();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(1000);
        onView(withText("Settings")).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void FCheckNotificationsSet() throws InterruptedException {
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
        }
    }

    @Test
    public void GCheckWebView() {
        Intents.init();
        onView(withId(R.id.recyclerview)).perform(click());
        intended(hasComponent(WebActivity.class.getName()));
        Intents.release();
    }

}
