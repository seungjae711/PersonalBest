package com.example.group12_project;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class BarChartMediator implements IReaderObserver, ISessionObserver {

    private boolean read = false;
    private boolean sesh = false;
    private int[] sessionSteps;
    private int[] allSteps;
    private MainActivity activity;

    public BarChartMediator(MainActivity activity) {
        this.activity = activity;
    }


    @Override
    public void sessionUpdate() {
        sesh = true;
        // if (read) {
        makeGraph();
        //  }
    }

    @Override
    public void readerUpdate() {
        read = true;
        // if (sesh) {
        makeGraph();
        //  }
    }

    @Override
    public void getSessionSteps(int[] steps) {
        this.sessionSteps = steps;
    }

    @Override
    public void getAllSteps(int[] steps) {
        this.allSteps = steps;
    }

    private void makeGraph() {
        Intent intent = new Intent(activity, StepChart.class);
        intent.putExtra("SessionSteps", sessionSteps);
        intent.putExtra("AllSteps", allSteps);


        activity.startActivity(intent);
    }
}
