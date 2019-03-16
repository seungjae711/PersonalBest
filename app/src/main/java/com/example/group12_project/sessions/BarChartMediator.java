package com.example.group12_project.sessions;

import android.content.Intent;
import android.util.Log;

import com.example.group12_project.IReaderObserver;
import com.example.group12_project.MainActivity;
import com.example.group12_project.StepChart;
import com.example.group12_project.sessions.ISessionObserver;

public class BarChartMediator implements IReaderObserver, ISessionObserver {

    private boolean read = false;
    private boolean sesh = false;
    private int[] sessionSteps;
    private int[] allSteps;
    private MainActivity activity;
    private String TAG;

    public BarChartMediator(MainActivity activity) {
        TAG = "BarChartMediator";
        this.activity = activity;
    }


    @Override
    public void sessionUpdate() {
        Log.i(TAG, "SessionReader finished");
        sesh = true;
        if (read) {
            makeGraph();
        }
        else {
            Log.i(TAG, "Waiting for StepReader");
        }
    }

    @Override
    public void readerUpdate() {
        Log.i(TAG, "StepReader finished");
        read = true;
        if (sesh) {
        makeGraph();
        }
        else {
            Log.i(TAG, "Waiting for sessionReader");
        }
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
        Log.i(TAG, "Making graph");
        Intent intent = new Intent(activity, StepChart.class);
        intent.putExtra("SessionSteps", sessionSteps);
        intent.putExtra("AllSteps", allSteps);


        activity.startActivity(intent);
    }
}
