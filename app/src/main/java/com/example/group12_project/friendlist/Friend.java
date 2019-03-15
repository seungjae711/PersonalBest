package com.example.group12_project.friendlist;


/**
 * Create friend object with necessary information to display
 */

public class Friend {

    String name;
    long goal;
    long currentStep;

    public Friend(String name, long goal, long currentStep) {
        this.name = name;
        this.goal = goal;
        this.currentStep = currentStep;
    }

    public String getName() {
        return name;
    }

    public long getGoal() {
        return goal;
    }

    public long getCurrentStep() {
        return currentStep;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGoal(long goal) {
        this.goal = goal;
    }

    public void setCurrentStep(long currentStep) {
        this.currentStep = currentStep;
    }
}
