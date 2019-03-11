package com.example.group12_project.friendlist;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserCloudMediator implements CloudObserver, UserObserver{

    User user;
    UserCloud cloud;

    FirebaseDatabase database;
//    DatabaseReference ref;

    CollectionReference ref;

    public UserCloudMediator(User user, UserCloud cloud){


        this.cloud = cloud;
        this.user = user;
        database = FirebaseDatabase.getInstance();
        ref = FirebaseFirestore.getInstance().collection("test");

    }

    public void GoalChange(int goal){
        Map<String, String> map = new HashMap<>();
        map.put("row1", "test1");
        map.put("row2", "test2");
        ref.add(map);
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
