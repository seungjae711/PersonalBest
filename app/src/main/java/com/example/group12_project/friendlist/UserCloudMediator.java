package com.example.group12_project.friendlist;

import java.util.Calendar;

public class UserCloudMediator implements CloudObserver, UserObserver{

    User user;
    UserCloud cloud;

    public UserCloudMediator(User user, UserCloud cloud){
        this.cloud = cloud;
        this.user = user;
    }

    public void GoalChange(int goal){

    }

    public void MphChange(int mph){}

    public void DailyDataChange(Calendar caldnear, long stepCount){}

    /**
     *  Update local friend list
     */
    public void localFriendChange(String newFriend){}

    public void localRequestChange(String newRequest){}

    /**
     * update cloud friend change
     * @param newFriend
     */
    public void cloudFriendChange(String newFriend){}

    public void cloudRequestChange(String newRequest){}

}
