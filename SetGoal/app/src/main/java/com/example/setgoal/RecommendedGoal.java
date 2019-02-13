package com.example.setgoal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class RecommendedGoal extends AppCompatActivity {
    public Button accept, customGoal, cancel;
    public TextView recommended;
    String recGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recommend_goal);
        //LayoutInflater inflater = getLayoutInflater();
        //View view = inflater.inflate(R.layout.content_main, null);

        accept = (Button) findViewById(R.id.accept_recommended);
        customGoal = (Button) findViewById(R.id.set_custom);
        cancel = (Button) findViewById(R.id.cancel_recommended);

        //get current goal and increase by factor of 500
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            recommended = findViewById(R.id.recommended);
            recommended.setText("null");
        }
        else {
            int currGoal = Integer.parseInt(extras.getString("CurrGoal"));
            currGoal += 500;
            recGoal = String.valueOf(currGoal);

            //show recommended goal
            recommended = findViewById(R.id.recommended);
            recommended.setText(recGoal);
        }

        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //if recommended goal is accepted
                Intent returnIntent = new Intent();
                returnIntent.putExtra("NewGoal", recGoal);
                setResult(1, returnIntent);
                finish();
            }
        });

        customGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //enter in custom goal instead
                //lead to custom goal page
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

    /*
    @Override
    public void update(Observable o, Object arg){
        final int currGoal = (int) arg;
    }*/
}
