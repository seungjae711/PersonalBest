package com.example.group12_project.set_goal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.group12_project.R;

public class CustomGoal extends AppCompatActivity {
    public Button accept, cancel;
    public EditText custom;
    double minSteps = 2000;
    double maxSteps = 30000;
    String custGoal = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_goal);

        accept = (Button) findViewById(R.id.accept_custom);
        cancel = (Button) findViewById(R.id.cancel_custom);

        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                custom = (EditText) findViewById(R.id.custom);
                custGoal = custom.getText().toString();
                //make sure EditText isn't empty
                if (TextUtils.isEmpty(custom.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter in a new goal amount", Toast.LENGTH_LONG).show();
                } else {
                    //2,000 min - 50,000 max
                    //encourage more steps if below min
                    if(Double.parseDouble(custGoal) < minSteps) {
                        Toast.makeText(getApplicationContext(), "You can do at least 2,000 steps! " +
                                "Try another goal.", Toast.LENGTH_LONG).show();
                    }
                    //warn of too high a goal if above max
                    else if (Double.parseDouble(custGoal) > maxSteps) {
                        Toast.makeText(getApplicationContext(), "More than 30,000 is a marathon a day! " +
                                "Try another goal.", Toast.LENGTH_LONG).show();
                    }
                    //within range
                    else {
                        //if custom goal is accepted, return
                        SharedPreferences newGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
                        SharedPreferences.Editor goalEditor = newGoal.edit();
                        custGoal = custom.getText().toString();
                        goalEditor.putString("goal", custGoal);
                        goalEditor.apply();;

                        Intent returnIntent = new Intent();
                        setResult(1, returnIntent);
                        finish();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //don't set a new goal
                finish();
            }
        });
    }
}