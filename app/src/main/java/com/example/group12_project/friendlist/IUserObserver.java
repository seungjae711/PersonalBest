package com.example.group12_project.friendlist;

import java.util.Collection;

public interface IUserObserver {

    void onLocalFriendChange(String newFriend, Object friendData, Object selfData);

    void onLocalRequestChange(String newRequest);

    void onLocalHeightChange(int height);

    void onSelfDataChange(SelfData selfData, Collection<String> friendsIds);

    void readFriendData(String friendId);
}

