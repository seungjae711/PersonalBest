package com.example.group12_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class RecommendedGoal extends AppCompatActivity {
    public Button accept, customGoal, cancel;
    public TextView recommended;
    String recGoal ="";
    int currGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recommend_goal);

        accept = (Button) findViewById(R.id.accept_recommended);
        customGoal = (Button) findViewById(R.id.set_custom);
        cancel = (Button) findViewById(R.id.cancel_recommended);

        //get current goal and increase by factor of 500
        SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        currGoal = Integer.parseInt(storedGoal.getString("goal",""));
        currGoal += 500;
        recGoal = String.valueOf(currGoal);

        //show recommended goal
        recommended = findViewById(R.id.recommended);
        recommended.setText(recGoal);

        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //if recommended goal is accepted
                SharedPreferences newGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
                SharedPreferences.Editor goalEditor = newGoal.edit();
                goalEditor.putString("goal", recGoal);
                goalEditor.apply();;

                Intent returnIntent = new Intent();
                setResult(1, returnIntent);
                finish();
            }
        });

        customGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //enter in custom goal instead
                //lead to custom goal page
                launchActivity();
                finish();
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


    public void launchActivity() {
        Intent intent = new Intent(this, CustomGoal.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Intent returnIntent = new Intent();
            setResult(1, returnIntent);
            finish();
        }
    }
}

