package com.example.group12_project.friendlist;


import java.util.Map;

public class UserCloudMediator implements CloudObserver, IUserObserver {

    LocalUser localUser;
    UserCloud cloud;

    /**
     * Update Cloud
     */

    public UserCloudMediator(LocalUser localUser, UserCloud cloud){
        this.cloud = cloud;
        this.localUser = localUser;
    }

    public void onLocalGoalChange(int goal) {
        cloud.setGoal(goal);
    }

    public void onLocalHeightChange(int height) {
        cloud.setHeight(height);
    }

    public void onLocalHistoryChange(Map<String, Integer> update) {
        cloud.addHistory(update);
    }

    public void onLocalFriendChange(String newFriend) {
        cloud.addFriend(newFriend);
    }

    public void onLocalRequestChange(String newRequest) {
        cloud.addRequested(newRequest);
    }

    /**
     * Update local
     */

    public void onCloudFriendChange(Map<String, Object> newFriends) {
        localUser.updateFriendList(newFriends);
    }

    public void onCloudRequestChange(Map<String, Object> newRequest) {
        localUser.updateFriendRequests(newRequest);
    }

    public Map<String, Object> readFriendData(String friendId) {
        return cloud.readFriendData(friendId);
    }
}
