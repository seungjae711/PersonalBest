package com.example.group12_project.friendlist;

import java.util.Map;

public interface IUserObserver {

    void onLocalFriendChange(String newFriend);

    void onLocalRequestChange(String newRequest);

    void onLocalHeightChange(int height);

    void onSelfDataChange(SelfData selfData);

    void readFriendData(String friendId);
}

