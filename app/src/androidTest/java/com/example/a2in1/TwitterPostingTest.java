package com.example.a2in1;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class TwitterPostingTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void TestViewFeed(){
        onView(withId(R.id.drawer_layout)).perform(swipeRight());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        6),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction twitterLoginButton = onView(
                allOf(withId(R.id.login_button), withText("Log in with Twitter"),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        twitterLoginButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        onView(withId(R.id.drawer_layout)).perform(swipeRight());

        ViewInteraction navigationMenuItemView2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        13),
                        isDisplayed()));
        navigationMenuItemView2.perform(click());

        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.checkedText), withText("I, 2in1App Wish to post"),
                        childAtPosition(
                                allOf(withId(R.id.confirmInputs),
                                        childAtPosition(
                                                withId(R.id.nav_TwitterPosting),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatCheckBox.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.msgInput),
                        childAtPosition(
                                allOf(withId(R.id.postImage),
                                        childAtPosition(
                                                withId(R.id.nav_TwitterPosting),
                                                2)),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Post message "), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.getPicBtn), withText("Add a picture to your post"),
                        childAtPosition(
                                allOf(withId(R.id.postImage),
                                        childAtPosition(
                                                withId(R.id.nav_TwitterPosting),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.getPicBtn), withText("Add a picture to your post"),
                        childAtPosition(
                                allOf(withId(R.id.postImage),
                                        childAtPosition(
                                                withId(R.id.nav_TwitterPosting),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.postingSubmitBtn), withText("Post"),
                        childAtPosition(
                                allOf(withId(R.id.postImage),
                                        childAtPosition(
                                                withId(R.id.nav_TwitterPosting),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}