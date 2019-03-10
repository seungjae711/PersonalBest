package com.example.group12_project.friendlist;

import java.util.Calendar;

public interface UserObserver {

    /**
     * update cloud friend change
     * @param newFriend
     */
    void cloudFriendChange(String newFriend);

    void cloudRequestChange(String newRequest);


}
