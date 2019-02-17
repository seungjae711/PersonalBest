package com.example.group12_project.fitness;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.example.group12_project.MainActivity;

public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";
    private static Map<String, BluePrint> blueprints = new HashMap<>();

    // put new blueprint into map blueprints
    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    // create a FitnessService
    public static FitnessService create(String key, MainActivity mainActivity) {
        Log.i(TAG, String.format("creating FitnessService with key %s", key));
        return blueprints.get(key).create(mainActivity);
    }

    // BluePrint interface
    public interface BluePrint {
        FitnessService create(MainActivity mainActivity);
    }
}
