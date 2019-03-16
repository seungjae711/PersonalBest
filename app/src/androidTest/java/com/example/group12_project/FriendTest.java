package com.example.group12_project;

import com.example.group12_project.friendlist.Friend;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FriendTest {

    @Test
    public void testConstructor() {
        Friend friend = new Friend("user1", 500, 200);
        assertNotNull(friend);
    }
    @Test
    public void testName() {
        Friend friend = new Friend("user1", 500, 200);
        assertTrue( friend.getName() == "user1");
    }
    @Test
    public void testGoal() {
        Friend friend = new Friend("user1", 500, 200);
        assertTrue( friend.getGoal() == 500);
    }
    @Test
    public void testStep() {
        Friend friend = new Friend("user1", 500, 200);
        assertTrue( friend.getCurrentStep() == 200);
    }
    @Test
    public void testSettingGoal() {
        Friend friend = new Friend("user1", 500, 200);
        friend.setGoal(1000);
        assertTrue(  friend.getGoal() == 1000);
    }
}
