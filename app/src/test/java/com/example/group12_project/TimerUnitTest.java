package com.example.group12_project;

import org.junit.Test;

import java.sql.Time;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TimerUnitTest {
    @Test
    public void timerIsCorrect() {
        TimerKeeper timer = new TimerKeeper();
        timer.setStart();
        int counter = 0;
        long start = timer.getEllapsedTime();
        long end = 0;
        while (counter < 100) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            end = timer.getEllapsedTime();
            assertTrue(end > start);
            start = end;
            counter++;
        }
    }
}