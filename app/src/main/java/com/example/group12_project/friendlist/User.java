package com.example.group12_project.friendlist;

import java.util.Collection;

public class User implements IUser{

    private Collection<UserObserver> observers;

    Collection<String> friendRequest;

    Collection<String> friendList;

    String id;

    /**
     * input from first launch of the app
     * @param id
     */
    public User(String id){
        this.id = id;
    }

    public void register(UserObserver userObserver){
        observers.add(userObserver);
    }

    public void unrgister(UserObserver userObserver){
        observers.remove(userObserver);
    }

    private void updateFriendRequest(String request){
        friendRequest.add(request);
    }


    private void updateFriendList(String friendToAdd){
        friendList.add(friendToAdd);
    }

    private void createFitnessService(){
        //TODO
    }


}
