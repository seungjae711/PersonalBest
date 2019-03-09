package com.example.group12_project;





import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

import static android.content.Context.MODE_PRIVATE;
import static java.text.DateFormat.getTimeInstance;

//https://developers.google.com/fit/android/using-sessions


public class stepSession {

    private final String TAG = "stepSession";


    GoogleSignInAccount lastSignedInAccount;
    Session session;
    MainActivity activity;
    Task<Void> sesTask;
    long endTime, startTime;
    Calendar cal;
    long steps;
    private final int FEET_TO_MILE = 5280;

    stepSession(MainActivity activity){

        this.activity = activity;

        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);


        cal = Calendar.getInstance();
        Date day = new Date();
        cal.setTime(day);
        String date = Integer.toString(cal.get(Calendar.YEAR)) + " : " + Integer.toString(cal.get(Calendar.DAY_OF_YEAR));

        Log.i(TAG, "Starting session. Date is: " + date);

        session = new Session.Builder()
                .setName("Session")
                .setIdentifier(date + " : " + Integer.toString((int)startTime))
                .setDescription("Step Tracking Session")
                .setStartTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public void start() {
        sesTask = Fitness.getSessionsClient(activity, lastSignedInAccount)
                                .startSession(session);
        sesTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Starting Session");
            }
        });
        startTime = session.getStartTime(TimeUnit.SECONDS);
    }


    public void end() {
        Task<List<Session>> endSes = Fitness.getSessionsClient(activity, lastSignedInAccount)
                                        .stopSession(session.getIdentifier());
        endSes.addOnSuccessListener(new OnSuccessListener<List<Session>>() {
            @Override
            public void onSuccess(List<Session> s) {
                Log.i(TAG, "Inserting Session");
                endTime = session.getEndTime(TimeUnit.SECONDS);
              //  insertSession(); breaks code cause endtime is always 0 somehow
            }
        });
        endTime = startTime + 1000; //Lazy test to see that stats dialog works
    }



    private void insertSession() {
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                //.addDataSet(speedDataSet)
                .build();

        Fitness.getSessionsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .insertSession(insertRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // At this point, the session has been inserted and can be read.
                        Log.i(TAG, "Session insert was successful!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem inserting the session: " +
                                e.getLocalizedMessage());
                    }
                });
    }

    public double calculateSessionSpeed(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("height", MODE_PRIVATE);
        double height = (double)sharedPreferences.getInt("height",0); //TODO:change key value
        double stride;
        if(height != 0){
            //Multiply height in inches by 0.413. This is a predetermined number that figures out average stride length.
            //Source: https://www.openfit.com/how-many-steps-walk-per-mile
            stride = height * 0.413 / 12; //converted to feet
        }
        else{
            Toast.makeText(activity, "User height data not found!",Toast.LENGTH_LONG).show();
            return 0;
        }
        DataReader reader = new DataReader(activity,startTime,endTime);
        List<DataSet> data = reader.getData();

        long stepWalked = getSteps(data);
        //Get the intentional walking time
        //calculate total distance from steps*stride length
        double distance = stepWalked * stride;
        double averageSpeed = (distance/FEET_TO_MILE)/(endTime/60 - startTime/60);

        return averageSpeed;
    }

    private long getSteps(List<DataSet> data) {
        if (data != null && data.size() > 0) {
            for (DataSet ds : data) {
                Log.i(TAG, "Data returned for Data type: " + ds.getDataType().getName());
                DateFormat dateFormat = getTimeInstance();
                for (DataPoint dp : ds.getDataPoints()) {
                    Log.i(TAG, "Data point:");
                    Log.i(TAG, "\tType: " + dp.getDataType().getName());
                    Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    for (Field field : dp.getDataType().getFields()) {
                        Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                        if (field.equals(Field.FIELD_STEPS)) {
                            steps = dp.getValue(field).asInt();
                            return steps;
                        }
                    }
                }
            }
        }
        return 100; //NonZero value for testing
    }
    public long getSessionSteps() {
        return steps;
    }

    public long getTotalTime() {
        return endTime - startTime;
    }


}
