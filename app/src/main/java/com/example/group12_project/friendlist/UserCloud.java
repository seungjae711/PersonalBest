package com.example.group12_project.friendlist;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserCloud implements ICloud {

    private Collection<CloudObserver> observers;

    FirebaseFirestore db;

    String userId;

    Map<String, Object> working;

    final private String TAG = "UserCloud: ";
    private String GOAL_KEY = "goal";
    private String HEIGHT_KEY = "height";
    private String DATA_KEY = "data";
    private String FRIENDS_KEY = "accepted";
    private String REQUEST_KEY = "requests";

    /**
     * constructor
     */
    public UserCloud(String userId){
        this.userId = userId;
        observers = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * method to register observers
     * @param cloudObserver the target observer to add
     */
    public void register(CloudObserver cloudObserver) {
        observers.add(cloudObserver);
    }

    /**
     * set goal on the cloud
     * @param goal new goal to store in the cloud
     */
    void setGoal(final int goal) {
        Map<String, Integer> goalMap = new HashMap<>();
        goalMap.put("number", goal);

        db.collection(userId).document(GOAL_KEY)
                .set(goalMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "new goal successfully written: " + goal);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error setting goal");
                    }
                });
    }

    /**
     * set height on the cloud
     * @param height user height to store in the cloud
     */
    void setHeight(final int height) {
        Map<String, Integer> heightMap = new HashMap<>();
        heightMap.put("number", height);

        db.collection(userId).document(HEIGHT_KEY)
                .set(heightMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "height successfully written: " + height);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error setting height");
                    }
                });
    }

    /**
     * merge new daily steps to data base
     * @param update map contains date and daily steps
     */
    void addHistory(Map<String, Integer> update) {
        db.collection(userId).document(DATA_KEY)
                .set(update, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "new daily data written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "error written daily data");
                    }
                });
    }

    /**
     * add new friend to data base
     * @param friendToAdd id of the user to be added
     */
    void addFriend(final String friendToAdd) {

        // write friend to your friend list
        Map<String, String> newFriend = new HashMap<>();
        newFriend.put(friendToAdd, friendToAdd);

        db.collection(userId).document(FRIENDS_KEY)
                .set(newFriend, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "new friend added" + friendToAdd);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding friend");
                    }
                });

        // add yourself to you friend's friend list
        Map<String, String> youself = new HashMap<>();
        newFriend.put(userId, userId);

        db.collection(friendToAdd).document(FRIENDS_KEY)
                .set(youself, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "added yourself to your friend's list");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding yourself");
                    }
                });
    }

    /**
     * add new request to data base (in other user's section)
     * @param request id of the requesting user
     */
    void addRequested(final String request) {
        Map<String, String> newRequest = new HashMap<>();
        newRequest.put(userId, userId);

        db.collection(request).document(REQUEST_KEY)
                .set(newRequest, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "new request written in " + request);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error written request");
                    }
                });
    }

    /**
     * update friend requests from cloud
     */
    public void updateRequest() {
        readFromCloud(userId, REQUEST_KEY);
        for (CloudObserver observer : this.observers) {
            observer.onCloudRequestChange(this.working);
        }
    }

    /**
     * update friend list
     */
    public void updateFriends() {
        readFromCloud(userId, FRIENDS_KEY);
        for (CloudObserver oberserver : this.observers) {
            oberserver.onCloudFriendChange(this.working);
        }
    }
    /**
     * helper method for reading from cloud
     * @param adapter map to get from cloud
     */
    private void extractor(Map<String, Object> adapter) {
        this.working = adapter;
    }

    /**
     * read a map from cloud
     * @param collection collection key
     * @param document document key
     */
    private void readFromCloud(String collection, final String document) {
        db.collection(collection).document(document).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> adapter = documentSnapshot.getData();
                            extractor(adapter);
                            Log.d(TAG, "Successfully read " + document);
                        } else {
                            Log.w(TAG, "no " + document + " to read");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting " + document);
                    }
                });
    }
}
