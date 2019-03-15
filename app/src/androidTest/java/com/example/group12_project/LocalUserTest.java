package com.example.group12_project;

import com.example.group12_project.friendlist.LocalUser;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocalUserTest {
    @Test
    public void testConstructor() {
        LocalUser user1 = new LocalUser("user01");

        assertNotNull(user1);
    }

    @Test
    public void testId() {
        LocalUser user1 = new LocalUser("user01");

        assertTrue(user1.getId() == "001");
    }

    @Test
    public void testHeight() {
        LocalUser user1 = new LocalUser("user01");
        user1.setHeight(1000);
        assertTrue(user1.getHeight() == 1000);
    }

    @Test
    public void testFriendList() {
        LocalUser user1 = new LocalUser("user01");
        assertTrue(user1.getFriendList().isEmpty());
    }
    @Test
    public void testNewHeight() {
        LocalUser user1 = new LocalUser("user01");
        user1.setHeight(1000);
        assertTrue(user1.getHeight() == 1000);

        user1.setHeight(1200);
        assertTrue(user1.getHeight() == 1200);
    }
    @Test
    public void testMoreFriendsList() {
        LocalUser user1 = new LocalUser("user01");
        user1.addFriend("user2");

        assertTrue(!user1.getFriendList().isEmpty());
    }
}
