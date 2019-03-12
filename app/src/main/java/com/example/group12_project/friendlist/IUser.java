package com.example.group12_project.friendlist;

import java.util.Collection;

public interface IUser {

    void register(UserObserver userObserver);

    void unregister(UserObserver userObserver);

    void setGoal(String newGoal);

    void setHeight(int height);

    void setHistory(String date, int steps);

}
