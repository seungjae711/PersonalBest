package com.example.setgoal;

import java.util.Observable;

public class GoalData extends Observable {
    int goal;

    public GoalData(){
        //initiaze to 5,000
        this.goal = 5000;

    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int newGoal){
        this.goal = newGoal;
        this.setChanged();
        notifyObservers(goal);
    }
}
