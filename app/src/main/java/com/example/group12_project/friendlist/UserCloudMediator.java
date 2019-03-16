package com.example.group12_project.friendlist;


import java.util.Collection;
import java.util.Map;

public class UserCloudMediator implements CloudObserver, IUserObserver {

    private LocalUser localUser;
    private UserCloud cloud;

    /**
     * Update Cloud
     */

    public UserCloudMediator(LocalUser localUser, UserCloud cloud){
        this.cloud = cloud;
        this.localUser = localUser;
    }


    public void onLocalHeightChange(int height) {
        cloud.setHeight(height);
    }

    public void onSelfDataChange(SelfData selfData, Collection<String> friendsIds) {
        cloud.addSelfData(selfData, friendsIds);
    }



    public void onLocalFriendChange(String newFriend, Object friendData, Object selfData) {
        cloud.addFriend(newFriend, friendData, selfData);
    }

    public void onLocalRequestChange(String newRequest) {
        cloud.addRequested(newRequest);
    }

    /**
     * Update local
     */

    public void readFriendData(String friendId) {
        cloud.readFriendData(friendId);
    }

    public void onFriendDataChange(SelfData selfData) {
        localUser.changeFriendData(selfData);
    }

    public void onCloudFriendChange(Map<String, Object> newFriends) {
        localUser.friendListCloudUpdate(newFriends);
    }

    public void onCloudRequestChange(Map<String, Object> newRequest) {
        localUser.friendRequestsCloudUpdate(newRequest);
    }

}
