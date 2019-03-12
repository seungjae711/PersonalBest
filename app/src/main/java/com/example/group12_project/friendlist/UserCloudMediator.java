package com.example.group12_project.friendlist;


import java.util.Map;

public class UserCloudMediator implements CloudObserver, UserObserver{

    LocalUser localUser;
    UserCloud cloud;

    public UserCloudMediator(LocalUser localUser, UserCloud cloud){
        this.cloud = cloud;
        this.localUser = localUser;
    }

    public void onLocalGoalChange(int goal) {}

    public void onLocalHeightChange(int height) {}

    public void onLocalHistoryChange(Map<String, Integer> update) {
        // set mode to merge
    }

    /**
     *  Update local friend list
     */
    public void onCloudFriendChange(String newFriend) {}

    public void onCloudRequestChange(String newRequest) {}

    /**
     * update cloud friend change
     * @param newFriend
     */
    public void onLocalFriendChange(String newFriend) {}

    public void onLocalRequestChange(String newRequest) {}

}
