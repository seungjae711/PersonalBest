package com.example.group12_project;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TimeKeeperTest {

    @Test
    public void testshort() {
        TimerKeeper timeKeeper = new TimerKeeper();
        long lThresh  = 200000000;
        long hThresh = 600000000;

        timeKeeper.setStart();
        long ellap = timeKeeper.getEllapsedTime();
        assertTrue( lThresh <= timeKeeper.getStart());
        assertTrue(timeKeeper.getEllapsedTime() <= hThresh);
    }

    @Test
    public void testlong() {
        TimerKeeper timeKeeper = new TimerKeeper();
        long lThresh  = 200000000;
        long hThresh = 1500000000;

        timeKeeper.setStart();
        try {
            Thread.sleep(1000);
        } catch (Exception e){
            // go through with no pause
        }
        long ellap = timeKeeper.getEllapsedTime();
        assertTrue( lThresh <= timeKeeper.getStart());
        assertTrue(timeKeeper.getEllapsedTime() <= hThresh);
    }
}
