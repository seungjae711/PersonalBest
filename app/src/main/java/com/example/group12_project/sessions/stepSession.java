package com.example.group12_project.sessions;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.group12_project.DataReader;
import com.example.group12_project.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
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


    private GoogleSignInAccount lastSignedInAccount;
    private Session session;
    private MainActivity activity;
    private Task<Void> sesTask;
    private long endTime = 1000, startTime;
    private Calendar cal;
    private long steps;
    private final int FEET_TO_MILE = 5280;

    public stepSession(MainActivity activity) {

        this.activity = activity;

        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);

        cal = Calendar.getInstance();


        Date day = new Date();
        cal.setTime(day);
        String date = Integer.toString(cal.get(Calendar.YEAR)) + " : " + Integer.toString(cal.get(Calendar.DAY_OF_YEAR));

        startTime = cal.getTimeInMillis();

        Log.i(TAG, "Starting session. Date is: " + date);
        String id = cal.toString();

        session = new Session.Builder()
                //       .setName("Step Session: " + date + " : " + Integer.toString((int)startTime))
                .setIdentifier(id)
                //  .setDescription("Step Tracking Session" + Integer.toString((int)startTime))
                .setStartTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public void start() {
        setCal();
        startTime = cal.getTimeInMillis();
        Log.i(TAG, "Session start time: " + startTime);
        /*
        sesTask = Fitness.getSessionsClient(activity, lastSignedInAccount)
                                .startSession(session);
        sesTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Starting Session");
                startTime = session.getStartTime(TimeUnit.SECONDS);
                Log.i(TAG, "Session start time: " + startTime%10000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed session start", e);
            }
        });
        startTime = session.getStartTime(TimeUnit.SECONDS); */

    }


    public void end() {
        setCal();
        endTime = cal.getTimeInMillis();
        Log.i(TAG, "Session end time: " + endTime);
        insertSession();
        /*
        Task<List<Session>> endSes = Fitness.getSessionsClient(activity, lastSignedInAccount)
                                        .stopSession(null);
        endSes.addOnSuccessListener(new OnSuccessListener<List<Session>>() {
            @Override
            public void onSuccess(List<Session> s) {
                Log.i(TAG, "Inserting Session");
                endTime = session.getEndTime(TimeUnit.SECONDS);
                Log.i(TAG, "Session end time: " + endTime);
                //insertSession(); //breaks code cause endtime is always 0 somehow
            }
        });
        */
        //endTime = startTime + 1000; //Lazy test to see that stats dialog works
    }


    private void insertSession() {
        session = new Session.Builder()
                .setName("Step Session")
                .setIdentifier(cal.toString())
                .setDescription("Step Tracking")
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
                .build();

        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
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

    public double calculateSessionSpeed() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("height", MODE_PRIVATE);
        double height = (double) sharedPreferences.getInt("height", 0); //TODO:change key value
        double stride;
        if (height != 0) {
            //Multiply height in inches by 0.413. This is a predetermined number that figures out average stride length.
            //Source: https://www.openfit.com/how-many-steps-walk-per-mile
            stride = height * 0.413 / 12; //converted to feet
        } else {
            Toast.makeText(activity, "User height data not found!", Toast.LENGTH_LONG).show();
            return 0;
        }

        long stepWalked = getSessionSteps();
        Log.i(TAG, "Steps walked: " + stepWalked);
        //Get the intentional walking time
        //calculate total distance from steps*stride length
        double distance = stepWalked * stride;
        Log.i(TAG, "Distance: " + distance);

        double timeInSeconds = (endTime - startTime) / 1000;
        Log.i(TAG, "Time in seconds: " + timeInSeconds);


        double averageSpeed = (distance / FEET_TO_MILE) / (timeInSeconds / 3600);

        Log.i(TAG, "Speed: " + averageSpeed);


        return averageSpeed;
    }

    private long getSteps(List<DataSet> data) {
        steps = 100;
        return 100; //Returning only 100 right now for testing with emulator
        //Comment out the above 2 lines and uncomment below for testing with phone
        /*if (data != null && data.size() > 0) {
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
        return 100; //NonZero value for testing */
    }

    public long getSessionSteps() {
        return steps;
    }

    public long getTotalTime() {
        return (endTime - startTime) / 1000;
    }

    private void setCal() {
        Date day = new Date();
        cal.setTime(day);
    }

    public void launchDialog(stepSession sesh) {
        DataReader reader = new DataReader(activity, startTime, endTime);
        Task<DataReadResponse> task = reader.getHistoryTask();
        task.addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                long stepSum = 0;
                List<DataSet> dataSets = dataReadResponse.getDataSets();
                for (DataSet data : dataSets) {
                    Log.d(TAG, data.toString());
                    stepSum = stepSum + (data.isEmpty() ? 0 :
                            data.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
                }

                steps = stepSum;
                SharedPreferences.Editor statsEdit = activity.getSharedPreferences("stats", MODE_PRIVATE).edit();
                statsEdit.putFloat("speed", (float) calculateSessionSpeed());
                statsEdit.putLong("steps", getSessionSteps());
                statsEdit.putLong("time", getTotalTime());
                statsEdit.apply();
                Intent intent = new Intent(activity, StatsDialog.class);
                activity.startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "History task failed: ", e);
            }
        });


    }

}
