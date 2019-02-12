package com.example.group12_project.fitness;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.group12_project.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GoogleFitAdapter implements FitnessService {

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

    // we need this for google fit
    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    // setup the fitness tracking options
    public void setup() {

        // declaring the Fit API data types and access required
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        // check if the user has previously granted the necessary data access
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity),
                fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions
            );
        } else {
            startRecording();
        }
    }

    // subscribe step count for Google to record
    public void startRecording() {

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);

        // check if already signed in
        if (lastSignedInAccount == null) {
            return;
        }

        // subscribe
        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d(TAG, dataSet.toString());
                        long total = dataSet.isEmpty() ? 0 :
                                dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        Log.d(TAG, "Total steps: " + total);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "There was a problem getting the step count.", e);
                    }
                });
    }


}
