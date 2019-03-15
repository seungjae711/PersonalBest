package com.example.group12_project;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.group12_project.sessions.ISessionObserver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;

public class DataReader implements ISubject<IReaderObserver> {
    private MainActivity activity;
    private DataReadRequest readRequest;
    private final String TAG = "DataReader";
    private List<DataSet> dataSets;
    private long total;
    private long start, end;
    private ArrayList<Integer> aggSteps;
    private Collection<IReaderObserver> observers;

    public DataReader(MainActivity activity, long startTime, long endTime) {
        this.activity = activity;
        this.total = 0;
        this.start = startTime;
        this.end = endTime;
        this.observers = new ArrayList<IReaderObserver>();
        this.aggSteps = new ArrayList<>();

        //setData(startTime, endTime);
    }

    public void setData(long startTime, long endTime) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);

        if (lastSignedInAccount == null) {
            return;
        }

        this.readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readData(readRequest);

        response.addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
            @Override
            public void onComplete(@NonNull Task<DataReadResponse> readResponse) {
                if (readResponse.isSuccessful()) {
                    Log.i(TAG, "History Task Worked");
                    dataSets = readResponse.getResult().getDataSets();
                    //for (DataSet ds : dataSets) {
                    //  dumpDataSet(ds);
                    //}
                } else {
                    Log.i(TAG, "History Task Failed");
                    //Exception e = readResponse.getException();
                }
            }
        });
    }

    public void dumpDataSets() {
        if (this.dataSets != null && this.dataSets.size() > 0) {
            for (DataSet ds : this.dataSets) {
                Log.i(TAG, "Data returned for Data type: " + ds.getDataType().getName());
                DateFormat dateFormat = getTimeInstance();

                for (DataPoint dp : ds.getDataPoints()) {
                    Log.i(TAG, "Data point:");
                    Log.i(TAG, "\tType: " + dp.getDataType().getName());
                    Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    for (Field field : dp.getDataType().getFields()) {
                        Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                    }
                }
            }
        }

    }

    public void aggregateStepsByDay(int size) {
        Calendar pastDay = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        pastDay.setTimeInMillis(start);
        today.setTimeInMillis(end);
        pastDay = setStartofDay(pastDay);
        today = setStartofDay(today);
        Log.i(TAG, pastDay.toString() + " : " + today.toString());

        while (pastDay.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)) {
            long start = pastDay.getTimeInMillis();
            pastDay.add(Calendar.DAY_OF_YEAR, 1);
            long end = pastDay.getTimeInMillis();
            buildAggData(start,end,size);

        }

    }

    private void buildAggData(long startTime, long endTime, final int size) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);

        if (lastSignedInAccount == null) {
            return;
        }

        this.readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readData(readRequest);

        response.addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
            @Override
            public void onComplete(@NonNull Task<DataReadResponse> readResponse) {
                if (readResponse.isSuccessful()) {
                    Log.i(TAG, "History Task Worked");
                    long dailySum = 0;
                    dataSets = readResponse.getResult().getDataSets();
                    for (DataSet data : dataSets) {
                        Log.d(TAG, data.toString());
                        dailySum = dailySum + (data.isEmpty() ? 0 :
                                data.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
                    }
                    aggSteps.add((int)dailySum);
                    if (aggSteps.size() == size) {
                        updateObservers();
                    }

                    //for (DataSet ds : dataSets) {
                    //  dumpDataSet(ds);
                    //}
                } else {
                    Log.i(TAG, "History Task Failed");
                    //Exception e = readResponse.getException();
                }
            }
        });
    }

    public List<DataSet> getData() {
        return this.dataSets;
    }

    public long getDailyData() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);

        // check if already signed in
        if (lastSignedInAccount == null) {
            return 0;
        }

        // request data from google
        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d(TAG, dataSet.toString());
                        total = dataSet.isEmpty() ? 0 :
                                dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        Log.d(TAG, "Total steps: " + total);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        total = 0;
                        Log.d(TAG, "There was a problem getting the step count.", e);
                    }
                });
        return total;
    }


    private Calendar setStartofDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;

    }


    @Override
    public void register(IReaderObserver observer) {
        observers.add(observer);
    }

    private void updateObservers() {
        for (IReaderObserver o : observers) {
            int[] array = new int[aggSteps.size()];
            for (int i = 0; i < aggSteps.size(); i++) {
                array[i] = aggSteps.get(i);
            }
            o.getAllSteps(array);
            o.readerUpdate();
        }
    }


}
