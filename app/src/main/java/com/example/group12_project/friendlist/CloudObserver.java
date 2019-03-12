package com.example.group12_project.friendlist;

import java.util.Map;

public interface CloudObserver {

    /**
     *  Update local friend list
     * @param newFriends
     */
    void onCloudFriendChange(Map<String, Object> newFriends);

    void onCloudRequestChange(Map<String, Object> newRequest);


}

