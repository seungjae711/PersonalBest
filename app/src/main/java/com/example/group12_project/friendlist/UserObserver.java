package com.example.group12_project.friendlist;

import java.util.Map;

public interface UserObserver {

    /**
     * update cloud friend change
     * @param newFriend
     */
    void onLocalFriendChange(String newFriend);

    void onLocalRequestChange(String newRequest);

    void onLocalGoalChange(int goal);

    void onLocalHeightChange(int height);

    void onLocalHistoryChange(Map<String, Integer> history);

}

