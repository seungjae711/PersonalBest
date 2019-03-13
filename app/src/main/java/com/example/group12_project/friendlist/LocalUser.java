package com.example.group12_project.friendlist;

import android.app.Activity;
import android.util.Log;

import com.example.group12_project.set_goal.GoalManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LocalUser implements IUser{

    private Collection<IUserObserver> observers;

    private Collection<String> friendRequests;

    private Collection<String> friendList;

    private Map<String, Integer> history;

    public GoalManagement goalManagement;

    private String id;

    final private String userTAG = "Local user: ";

    private int height;

    /**
     * input from first launch of the app
     * @param id user id
     */
    public LocalUser(String id){
        this.id = id;
        observers = new ArrayList<>();
        history = new HashMap<>();
        friendRequests = new HashSet<>();
        friendList = new HashSet<>();
    }

    public void register(IUserObserver IUserObserver){
        observers.add(IUserObserver);
    }

    public void unregister(IUserObserver IUserObserver){
        observers.remove(IUserObserver);
    }

    /**
     * getter method for user id
     */
    public String getId() {
        return id;
    }

    /**
     * update local friend list and update observers due to own action
     * @param friendToAdd id of the friend to add
     */
    public void updateFriendListLocal(String friendToAdd){
        friendList.add(friendToAdd);
        Log.d(userTAG, "friend added: " + friendToAdd);
        for (IUserObserver observer : this.observers) {
            observer.onLocalFriendChange(friendToAdd);
        }
    }

    /**
     * update local friend list due to cloud change
     * @param newFriends map representing all friends from cloud
     */
    void updateFriendList(Map<String, Object> newFriends) {
        friendRequests = new HashSet<>(newFriends.keySet());;
    }

    /**
     * update local friend requests and update observers
     * @param newRequests newRequests from cloud
     */
    void updateFriendRequests(Map<String, Object> newRequests) {
        friendRequests = new HashSet<>(newRequests.keySet());
    }

    /**
     * write a request to other user's section
     * @param request id of the user to write request
     */
    private void writeRequest(String request) {
        Log.d(userTAG, "write request to " + request);
        for (IUserObserver observer : this.observers) {
            observer.onLocalRequestChange(request);
        }
    }

    private void createFitnessService(){
        //TODO
    }

    /**
     * add a friend by user id
     * @param friendId user id of the person you are trying to add
     * @return true if successful, false otherwise
     */
    public Boolean addFriend(String friendId) {

        // check own friend list, if already added, return false
        if (friendList.contains(friendId)) {
            Log.d(userTAG, "trying to add an existing friend " + friendId);
            return false;
        }

        // check friend requests from others, if exist, add friend
        if (friendRequests.contains(friendId)) {
            friendRequests.remove(friendId);
            updateFriendListLocal(friendId);
            return true;
        }

        // add own user id to friend's requests list
        writeRequest(friendId);
        return true;
    }

    //TODO get friends data

    /**
     * initialize goal management
     * @param activity activity for goal management
     */
    public void setGoalManagement(Activity activity) {
        goalManagement = new GoalManagement(activity);
    }

    /**
     * set local user goal and update observers
     * @param newGoal: string represents new goal
     */
    public void setGoal(String newGoal) {
        goalManagement.setGoal(newGoal);
        Log.d(userTAG, "changed local goal to " + newGoal);
        for(IUserObserver observer : this.observers) {
            observer.onLocalGoalChange(Integer.parseInt(newGoal));
        }
    }

    /**
     * set local user's height
     * @param height user's height
     */
    public void setHeight(int height) {
        this.height = height;
        Log.d(userTAG, "height set to " + height);
        for (IUserObserver observer : this.observers) {
            observer.onLocalHeightChange(height);
        }
    }

    /**
     * set daily exercise result
     * @param steps total steps in a day
     */
    public void setHistory(String date, int steps) {
        history.put(date, steps);
        Map<String, Integer> update = new HashMap<>();
        update.put(date, steps);
        Log.d(userTAG, "add history" + date + steps);
        for (IUserObserver observer : this.observers) {
            observer.onLocalHistoryChange(update);
        }
    }


    public Collection<String> getFriendList(){
        return this.friendList;
    }

}
