package com.example.group12_project;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Rule;
import org.junit.runner.RunWith;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BarGraphTest extends ActivityInstrumentationTestCase2<MainActivity>{

    private MainActivity activity;
    public BarGraphTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @Test
    public void testConstructor() {

        BarGraph barGraph = new BarGraph(activity);
        assertNotNull( barGraph);
    }


}
