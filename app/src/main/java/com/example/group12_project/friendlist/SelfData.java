package com.example.group12_project.friendlist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SelfData {
    Map<String, Object> goal;
    Map<String, Object> daily_steps;
    String id;

    SelfData() {
    }

    SelfData(String id, long goalNum, long stepNum) {
        this.id = id;
        this.goal = new HashMap<>();
        this.goal.put("goal", goalNum);
        this.daily_steps = new HashMap<>();
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String strDate = dateFormat.format(date);
        this.daily_steps.put(strDate, stepNum);
    }

    public String getGoal() {
        return Long.toString((long) goal.get("goal"));
    }

    public String getDaily_steps(String date) {
        return Long.toString((long) daily_steps.get(date));
    }

    public String getId() {
        return id;
    }

    public void setGoal(Map<String, Object> goal) {
        this.goal = goal;
    }

    public void setDaily_steps(String date, int steps) {
        daily_steps.put(date, steps);
    }

    public void setId(String id) {
        this.id = id;
    }
}
