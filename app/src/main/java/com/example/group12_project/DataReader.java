package com.example.group12_project;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;

public class DataReader {
    private MainActivity activity;
    private DataReadRequest readRequest;
    private final String TAG = "DataReader";
    private List<DataSet> dataSets;
    private long total;



    public DataReader(MainActivity activity, long startTime, long endTime) {
        this.activity = activity;
        this.total = 0;
        setData(startTime, endTime);
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
        if (this .dataSets != null && this.dataSets.size() > 0) {
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



}
