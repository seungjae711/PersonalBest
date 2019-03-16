package com.example.group12_project.friendlist;

import java.util.Map;

public interface CloudObserver {

    void onCloudFriendChange(Map<String, Object> newFriends);

    void onCloudRequestChange(Map<String, Object> newRequest);

    void onFriendDataChange(SelfData selfData);

}

