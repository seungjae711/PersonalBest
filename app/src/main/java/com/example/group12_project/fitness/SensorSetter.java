package com.example.group12_project.fitness;



import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.Result;
import com.example.group12_project.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;




//https://developers.google.com/fit/android/sensors
public class SensorSetter {

    MainActivity activity;
    String TAG;

    public SensorSetter(MainActivity activity) {
        this.activity = activity;
        TAG = "SensorSetter";
    }

    public void makeService() {

        OnDataPointListener dataListener =
                new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                }
            }
        };

        GoogleApiClient client = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.SENSORS_API)
                .build();
        client.connect();


        //Fitness.SensorsApi.findDataSources()

        PendingResult<Status> pendingResult = Fitness.SensorsApi.add(
                client,
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setSamplingRate(1, TimeUnit.MINUTES)  // sample once per minute
                        .build(),
                dataListener);

    }

}
