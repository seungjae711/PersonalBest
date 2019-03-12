package com.example.group12_project.friendlist;

import java.util.ArrayList;
import java.util.Collection;

public class UserCloud implements ICloud {

    private Collection<CloudObserver> observers;

    public UserCloud(){
        observers = new ArrayList<>();
    }

    public void register(CloudObserver cloudObserver) {
        observers.add(cloudObserver);
    }
}
