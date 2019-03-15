package com.example.group12_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.group12_project.BarGraph;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class StepChart extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steps_chart);

        String TAG = "StepChart";

        Intent myIntent = this.getIntent();
        int[] sessionSteps = myIntent.getIntArrayExtra("SessionSteps");
        int[] allSteps = myIntent.getIntArrayExtra("AllSteps");


        BarGraph graph = new BarGraph(this);

        graph.createStepBarGraph(sessionSteps, allSteps);

     //   stepReader.register(graph);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}