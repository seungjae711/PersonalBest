package com.example.group12_project.friendlist;

import java.util.Calendar;

public interface CloudObserver {

    void GoalChange(int goal);

    void MphChange(int mph);

    void DailyDataChange(Calendar calendar, long stepCount);

    /**
     *  Update local friend list
     */
    void localFriendChange(String newFriend);

    void localRequestChange(String newRequest);


}
