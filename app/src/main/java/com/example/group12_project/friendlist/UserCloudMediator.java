package com.example.group12_project.friendlist;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;

public class UserCloudMediator implements CloudObserver, UserObserver{

    User user;
    UserCloud cloud;

    FirebaseDatabase database;
    DatabaseReference ref;

    public UserCloudMediator(User user, UserCloud cloud){


        this.cloud = cloud;
        this.user = user;
        database = FirebaseDatabase.getInstance();
        ref = database.getInstance().getReference();

    }

    public void GoalChange(int goal){
    }

    public void MphChange(int mph){}

    public void DailyDataChange(Calendar calendar, long stepCount){}

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
