package com.example.mayamiller.startandendbutton;

import android.app.Activity;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class ButtonTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testStartText() {
        Button button_start = mainActivity.getActivity().findViewById(R.id.startTimer);
        String startText = button_start.getText().toString();
        assertThat(startText, equalTo("Start Timer"));
    }


    @Test
    public void testEndText() {
        Button button_end = mainActivity.getActivity().findViewById(R.id.endTimer);
        String endText = button_end.getText().toString();
        assertThat(endText, equalTo("End Timer"));
    }

    @Test
    public void testTimerText() {
        TextView time_text = mainActivity.getActivity().findViewById(R.id.time);
        String timeText = time_text.getText().toString();
        assertThat(timeText, equalTo("Time Your Steps!"));
    }

    @Test
    public void testDefault() {
        Button button_start = mainActivity.getActivity().findViewById(R.id.startTimer);
        Button button_end = mainActivity.getActivity().findViewById(R.id.endTimer);
        assertThat(View.VISIBLE, equalTo(button_start.getVisibility()));
        assertThat(View.INVISIBLE, equalTo(button_end.getVisibility()));
    }

    @UiThreadTest
    public void testToggle1() {
        Button button_start = mainActivity.getActivity().findViewById(R.id.startTimer);
        Button button_end = mainActivity.getActivity().findViewById(R.id.endTimer);
        button_start.performClick();

        assertThat(View.VISIBLE, equalTo(button_end.getVisibility()));
        assertThat(View.INVISIBLE, equalTo(button_start.getVisibility()));
    }

    @UiThreadTest
    public void testToggle2() {
        Button button_start = mainActivity.getActivity().findViewById(R.id.startTimer);
        Button button_end = mainActivity.getActivity().findViewById(R.id.endTimer);

        button_start.performClick();
        assertThat(View.VISIBLE, equalTo(button_end.getVisibility()));
        assertThat(View.INVISIBLE, equalTo(button_start.getVisibility()));

        button_end.performClick();
        assertThat(View.VISIBLE, equalTo(button_end.getVisibility()));
        assertThat(View.VISIBLE, equalTo(button_start.getVisibility()));

    }

}
