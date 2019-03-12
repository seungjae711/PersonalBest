package com.example.group12_project.friendlist;

public interface IUser {

    void register(IUserObserver IUserObserver);

    void unregister(IUserObserver IUserObserver);

    void setGoal(String newGoal);

    void setHeight(int height);

    void setHistory(String date, int steps);

}
