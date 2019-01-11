package com.thebaileybrew.baileybrewbook;

import android.app.ListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class RecipeSelectionBasicTest {

    private static final String RECIPE_INTRO = "Recipe Introduction";

    @Rule
    public ActivityTestRule<ListActivity> mActivityTestRule
            = new ActivityTestRule<>(ListActivity.class);

    /*
     * Clicks on a list item and checks that the detail view is opened with the correct details
     */
    @Test
    public void clickListViewItem_OpensDetailActivty() {
        //checks to verify that the item at position one has been selected
        onData(anything()).inAdapterView(withId(R.id.recipe_list)).atPosition(1).perform(click());


        onView(withId(R.id.recipe_step_name)).check(matches(withText(RECIPE_INTRO)));


    }

}
