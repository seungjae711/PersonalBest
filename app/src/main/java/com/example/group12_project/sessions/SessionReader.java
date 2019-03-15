package com.example.group12_project.sessions;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.group12_project.ISubject;
import com.example.group12_project.MainActivity;
import com.example.group12_project.sessions.ISessionObserver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class SessionReader implements ISubject<ISessionObserver> {
    private long start, end;
    //private Date startDate, endDate;
    private Calendar startDate, endDate;
    private String TAG;
    private int mData;
    private MainActivity activity;
    private SessionReadResponse response;
    private boolean waiting = false;
    private List<Session> sessions;
    private ArrayList<ISessionObserver> observers;
    private ArrayList<Integer> sessionSteps;


    public SessionReader(MainActivity activity) {
        TAG = "SessionReader";
        this.activity = activity;
        this.observers = new ArrayList<ISessionObserver>();
        this.sessionSteps = new ArrayList<Integer>();

    }

    public void setTimeFrame(Calendar startDate, Calendar endDate) {
        this.startDate = startDate;
        this.start = startDate.getTimeInMillis();
        this.endDate = endDate;
        this.end = endDate.getTimeInMillis();
    }

    public Task<SessionReadResponse> readData() {
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(start, end, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .build();

        return Fitness.getSessionsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        // Get a list of the sessions that match the criteria to check the result.
                        List<Session> sessions = sessionReadResponse.getSessions();
                        Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                                + sessions.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Failed to read session");
                    }
                });
    }

    public void /*List<Session>*/ getSessions() {
        Task<SessionReadResponse> task = readData();
        task.addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
            @Override
            public void onSuccess(SessionReadResponse sessionReadResponse) {
                Log.i(TAG, "Successful Session retrieval");
                response = sessionReadResponse;
                sessions = response.getSessions();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed Session retrieval");
                response = null;
            }
        });

  /*      if (response != null) {
            List<Session> sessions = response.getSessions();
            Log.i(TAG, "Number of sessions: " + sessions.size());
            return response.getSessions();
        } else {
            Log.i(TAG, "null sessions");
            return null;
        } */
    }

    public void aggregateSessionSteps() {
        Task<SessionReadResponse> task = readData();
        task.addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
            @Override
            public void onSuccess(SessionReadResponse sessionReadResponse) {
                Log.i(TAG, "Successful Session retrieval");
                response = sessionReadResponse;
                sessions = response.getSessions();
                saveData(sessions);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed Session retrieval");
                response = null;
            }
        });
    }

    private void saveData(List<Session> sessions) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(startDate.getTime());
        endOfDay = setStartOfNextDay(endOfDay);

        if (sessions == null) {
            Log.e(TAG, "Empty sessions list");
        }

        long dailySum = 0;

        for (Session s : sessions) {
            if (s.getStartTime(TimeUnit.MILLISECONDS) > endOfDay.getTimeInMillis()) {
                sessionSteps.add((int) dailySum);
                dailySum = 0;
                endOfDay.add(Calendar.DAY_OF_MONTH, 1);
            }
            for (DataSet data : this.response.getDataSet(s)) {
                Log.d(TAG, data.toString());
                dailySum = dailySum + (data.isEmpty() ? 0 :
                        data.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
            }
        }
        updateObservers();
    }


    public ArrayList<Integer> aggregateSessionStepsz() {

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(startDate.getTime());
        endOfDay = setStartOfNextDay(endOfDay);
        getSessions();
        ArrayList<Integer> aggregatedSteps = new ArrayList();
        long dailySum = 0;

        if (sessions == null) {
            Log.e(TAG, "Empty sessions list");
            return new ArrayList<Integer>(0);
        }


        for (Session s : sessions) {
            if (s.getStartTime(TimeUnit.MILLISECONDS) > endOfDay.getTimeInMillis()) {
                aggregatedSteps.add((int) dailySum);
                dailySum = 0;
                endOfDay.add(Calendar.DAY_OF_MONTH, 1);
            }
            for (DataSet data : this.response.getDataSet(s)) {
                Log.d(TAG, data.toString());
                dailySum = dailySum + (data.isEmpty() ? 0 :
                        data.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
            }
        }
        return aggregatedSteps;
    }


    private Calendar setStartOfNextDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        return cal;

    }


    @Override
    public void register(ISessionObserver observer) {
        observers.add(observer);
    }

    private void updateObservers() {
        for (ISessionObserver o : observers) {
            int[] array = new int[sessionSteps.size()];
            for (int i = 0; i < sessionSteps.size(); i++) {
                array[i] = sessionSteps.get(i);
            }
            o.getSessionSteps(array);
            o.sessionUpdate();
        }
    }



}
