package com.example.group12_project.fitness;

import com.example.group12_project.MainActivity;

public class GoogleFitAdapter {

    // for fit api permission d
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
            = System.identityHashCode(this) & 0xFFFF;

    // for logging
    private final String TAG = "GoogleFitAdapter";

    // to link the activity with with adapter
    private MainActivity activity;

    // constructor for GoogleFitAdapter
    public GoogleFitAdapter(MainActivity activity) {
        this.activity = activity;
    }
}
