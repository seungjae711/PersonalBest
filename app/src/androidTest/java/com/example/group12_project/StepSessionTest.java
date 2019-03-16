package com.example.group12_project;

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StepSessionTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;
    public StepSessionTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @Test
    public void testConstructor() {
        stepSession session = new stepSession( activity);
        assertNotNull( session);
    }

    @Test
    public void testCalculate() {
        stepSession session = new stepSession( activity);
        session.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        session.end();
        assertTrue(session.calculateSessionSpeed() > 1000);
    }

    @Test
    public void testSteps() {
        stepSession session = new stepSession( activity);
        assertTrue( session.getSessionSteps() >= 0);
    }

}
