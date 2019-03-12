package com.example.group12_project.friendlist;

import java.util.Calendar;

public interface CloudObserver {

    /**
     *  Update local friend list
     */
    void onCloudFriendChange(String newFriend);

    void onCloudRequestChange(String newRequest);


}

