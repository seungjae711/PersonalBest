package com.example.group12_project;

import com.example.group12_project.friendlist.Friend;
import com.example.group12_project.friendlist.SelfData;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SelfDataTest {
    @Test
    public void testConstructor() {
        SelfData user1 = new SelfData("001", 2000, 750);

        assertNotNull(user1);
    }

    @Test
    public void testId() {
        SelfData user1 = new SelfData("001", 2000, 750);

        assertTrue(user1.getId() == "001");
    }

    @Test
    public void testGoal() {
        SelfData user1 = new SelfData("001", 2000, 750);

        assertTrue(user1.getGoal() == "200");
    }
    @Test
    public void testStep() {
        SelfData user1 = new SelfData("001", 2000, 750);
        String date = "2019-03-15";
        assertTrue(user1.getDaily_steps(date) == "750");
    }
    @Test
    public void testNewGoal() {
        SelfData user1 = new SelfData("001", 2000, 750);
        HashMap<String, Object> map = new HashMap<>();
        map.put("goal", 3000);
        user1.setGoal(map);
        assertTrue(user1.getGoal() == "3000");
    }
    @Test
    public void testNewStep() {
        SelfData user1 = new SelfData("001", 2000, 750);
        String date = "2019-03-15";
        user1.setDaily_steps(date, 1000);
        assertTrue(user1.getId() == "001");
    }

}
