package com.example.group12_project.friendlist;

import java.util.Map;

public interface IUserObserver {

    void onLocalFriendChange(String newFriend);

    void onLocalRequestChange(String newRequest);

    void onLocalGoalChange(int goal);

    void onLocalHeightChange(int height);

    void onLocalHistoryChange(Map<String, Integer> history);

    Map<String, Object> readFriendData(String friendId);

    Map<String, Object> readFriendGoal(String friendId);

}

