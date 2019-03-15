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

    private FirebaseFirestore db;

    private String userId;

    private static UserCloud cloud;

    final private String TAG = "UserCloud: ";
    private String FRIENDS_KEY = "accepted";
    private String REQUEST_KEY = "requests";
    private String SELF_DATA_KEY = "self_data";

    /**
     * constructor
     * DO NOT call except in Main activity
     */
    public UserCloud(String userId) {
        this.userId = userId;
        observers = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * pass firestore instance
     */
    public FirebaseFirestore passInstance(){
        return this.db;
    }
    /**
     * set up user cloud to be static object
     *
     * @param userCloud the only real UserCloud object
     */
    public static void setUserCloud(UserCloud userCloud) {
        cloud = userCloud;
    }

    /**
     * whenever need a UserCloud object, get from here
     *
     * @return the real UserCloud object
     */
    public static UserCloud getUserCloud() {
        return cloud;
    }

    /**
     * method to register observers
     *
     * @param cloudObserver the target observer to add
     */
    public void register(CloudObserver cloudObserver) {
        observers.add(cloudObserver);
    }

    /**
     * set height on the cloud
     *
     * @param height user height to store in the cloud
     */
    void setHeight(final int height) {
        String HEIGHT_KEY = "height";
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
     *
     * @param selfData  object of own goal and data
     * @param friendIds all the friends need to notify
     */
    void addSelfData(SelfData selfData, Collection<String> friendIds) {
        // update self data
        Map<String, Object> data = new HashMap<>();
        data.put("self_data", selfData);
        db.collection(userId).document(SELF_DATA_KEY)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "new self data written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "error written self data");
                    }
                });

        // update friends
        for (final String friendId : friendIds) {
            data = new HashMap<>();
            data.put(friendId, selfData);
            db.collection(friendId).document(FRIENDS_KEY)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "data updated for" + friendId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "error written self data to " + friendId);
                        }
                    });
        }

    }

    /**
     * add new friend to data base
     *
     * @param friendToAdd id of the user to be added
     * @param friendData  initial data of the friend
     * @param selfData    add yourself to your friend's cloud
     */
    void addFriend(final String friendToAdd, Object friendData, Object selfData) {

        // write friend to your friend list
        Map<String, Object> newFriend = new HashMap<>();
        newFriend.put(friendToAdd, friendData);

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
        Map<String, Object> youself = new HashMap<>();
        youself.put(userId, selfData);

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
     *
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
    }

    /**
     * update friend list
     */
    public void updateFriends() {
        readFromCloud(userId, FRIENDS_KEY);
    }

    /**
     * read exercise data from user with passed in id
     *
     * @param friendId id of the user to read from
     */
    void readFriendData(String friendId) {
        readFromCloud(friendId, SELF_DATA_KEY);
    }

    /**
     * read a map from cloud to this.working
     *
     * @param collection collection key
     * @param document   document key
     */
    private void readFromCloud(String collection, final String document) {
        db.collection(collection).document(document).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> adapter = documentSnapshot.getData();
                            switch (document) {

                                // update accepted
                                case "accepted":
                                    for (CloudObserver observer : observers) {
                                        observer.onCloudFriendChange(adapter);
                                    }
                                    break;

                                // update friend request
                                case "requests":
                                    for (CloudObserver observer : observers) {
                                        observer.onCloudRequestChange(adapter);
                                    }
                                    break;

                                case "self_data":
                                    SelfData data = documentSnapshot.toObject(SelfData.class);
                                    for (CloudObserver observer : observers) {
                                        observer.onFriendDataChange(data);
                                    }
                                    break;
                                default:
                            }
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
