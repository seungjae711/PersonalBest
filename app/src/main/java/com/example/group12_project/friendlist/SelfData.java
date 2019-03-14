package com.example.group12_project.friendlist;

import java.util.Map;

public class SelfData {
    Map<String, Object> goal;
    Map<String, Object> daily_steps;
    String id;

    public Map<String, Object> getGoal() {
        return goal;
    }

    public Map<String, Object> getDaily_steps() {
        return daily_steps;
    }

    public String getId() {
        return id;
    }

    public void setGoal(Map<String, Object> goal) {
        this.goal = goal;
    }

    public void setDaily_steps(Map<String, Object> daily_steps) {
        this.daily_steps = daily_steps;
    }

    public void setId(String id) {
        this.id = id;
    }
}
