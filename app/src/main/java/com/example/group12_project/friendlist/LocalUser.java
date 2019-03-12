package com.example.group12_project.friendlist;

import android.util.Log;

import com.example.group12_project.set_goal.GoalManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LocalUser implements IUser{

    private Collection<UserObserver> observers;

    private Collection<String> friendRequests;

    private Collection<String> friendList;

    private Map<String, Integer> history;

    GoalManagement goalManagement;

    private String id;

    final String userTAG = "Local user: ";

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

    public void register(UserObserver userObserver){
        observers.add(userObserver);
    }

    public void unregister(UserObserver userObserver){
        observers.remove(userObserver);
    }

    /**
     * update local friend list and update observers
     * @param friendToAdd id of the friend to add
     */
    private void updateFriendList(String friendToAdd){
        friendList.add(friendToAdd);
        Log.d(userTAG, "friend added: " + friendToAdd);
        for (UserObserver observer : this.observers) {
            observer.onLocalFriendChange(friendToAdd);
        }
    }

    /**
     * update local friend requests and update observers
     * @param friendToAdd id of the friend to add to the request list
     */
    private void updateFriendRequests(String friendToAdd) {
        friendRequests.add(friendToAdd);
        Log.d(userTAG, "request added: " + friendToAdd);
        for (UserObserver observer : this.observers) {
            observer.onLocalRequestChange(friendToAdd);
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
            updateFriendList(friendId);
            return true;
        }

        // add own user id to friend's requests list
        updateFriendRequests(friendId);
        return true;
    }

    /**
     * set local user goal and update observers
     * @param newGoal: string represents new goal
     */
    public void setGoal(String newGoal) {
        goalManagement.setGoal(newGoal);
        Log.d(userTAG, "changed local goal to " + newGoal);
        for(UserObserver observer : this.observers) {
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
        for (UserObserver observer : this.observers) {
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
        for (UserObserver observer : this.observers) {
            observer.onLocalHistoryChange(update);
        }
    }

}
