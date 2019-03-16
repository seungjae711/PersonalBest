package com.example.group12_project.friendlist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.group12_project.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FriendDataDisplay extends AppCompatActivity {

    LocalUser user;
    TextView dispay_data;
    int numToRead;
    SelfData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_data_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = LocalUser.getLocalUser();
        data = user.selfData;

        numToRead = 28;

        dispay_data = findViewById(R.id.friend_data_display);

//        SelfData dataMap =  data;

        Map<String, Object> daily_steps = data.daily_steps;


        for(Map.Entry<String, Object> entry : daily_steps.entrySet()){
            int daitySteps = (int)entry.getValue();
            String date = entry.getKey();
            dispay_data.append("\n" + date + ": " + Long.toString(daitySteps));
        }

    }

}
