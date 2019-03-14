package com.example.group12_project.friendlist;

import android.app.Activity;
import android.util.Log;

import com.example.group12_project.MainActivity;
import com.example.group12_project.fitness.FitnessService;
import com.example.group12_project.fitness.FitnessServiceFactory;
import com.example.group12_project.fitness.GoogleFitAdapter;
import com.example.group12_project.set_goal.GoalManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LocalUser implements IUser {

    private Collection<IUserObserver> observers;

    private Collection<String> friendRequests;

    private Map<String, Object> friendList;

    private Map<String, Object> history;

    public GoalManagement goalManagement;

    public FitnessService fitnessService;

    public SelfData selfData;

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
        friendList = new HashMap<>();
        Map<String, Object> temp = new HashMap<>();
        temp.put("goal", -1);
        selfData = new SelfData();
        selfData.goal = temp;
        selfData.daily_steps = history;
        selfData.id = id;
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
        friendList.put(friendToAdd, new Friend(friendToAdd, -1, -1));
        Log.d(userTAG, "friend added: " + friendToAdd);
        for (IUserObserver observer : this.observers) {
            observer.onLocalFriendChange(friendToAdd);
        }
    }

    /**
     * update local friend list due to cloud change. This should be called by mediator
     * @param newFriends map representing all friends from cloud
     */
    void updateFriendList(Map<String, Object> newFriends) {
        friendList = newFriends;
    }

    /**
     * update local friend requests and update observers. This should be called by mediator
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

    public void createFitnessService(String serviceKey, MainActivity activity){
        FitnessServiceFactory.put(serviceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity mainActivity) {
                return new GoogleFitAdapter(mainActivity);
            }
        });
        fitnessService = FitnessServiceFactory.create(serviceKey, activity);
        fitnessService.setup();
    }

    /**
     * add a friend by user id
     * @param friendId user id of the person you are trying to add
     * @return true if successful, false otherwise
     */
    public Boolean addFriend(String friendId) {

        // check own friend list, if already added, return false
        if (friendList.containsKey(friendId)) {
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

    /**
     * read friend's daily exercise from cloud
     * this method delegate this responsibility to UserCloudMediator
     * this method require the requested friend does exist on cloud
     * @param friendID user id of the friend to read his/her data
     */
    public void readFriendData(String friendID) {
        Log.d(userTAG, "Requesting to read " + friendID);
        for (IUserObserver observer : this.observers) {
            observer.readFriendData(friendID);
        }
    }

    public void changeFriendData(SelfData friendsData) {
        Log.d(userTAG, "user" + friendsData.id + "data updated");
        friendList.put(friendsData.id, friendsData);
    }

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
        Map<String, Object> working = new HashMap<>();
        working.put("goal", Long.parseLong(newGoal));
        selfData.goal = working;
        Log.d(userTAG, "update new goal to cloud " + newGoal);
        for(IUserObserver observer : this.observers) {
            observer.onSelfDataChange(selfData);
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
     * getter method for height
     * @return user height
     */
    public int getHeight() {
        return height;
    }

    /**
     * set daily exercise result
     * @param steps total steps in a day
     */
    public void setHistory(String date, int steps) {
        history.put(date, steps);
        Log.d(userTAG, "add history" + date + steps);
        for (IUserObserver observer : this.observers) {
            observer.onSelfDataChange(selfData);
        }
    }


    public Map<String, Object> getFriendList(){
        return this.friendList;
    }

}
