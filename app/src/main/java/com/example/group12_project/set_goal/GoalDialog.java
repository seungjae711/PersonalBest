package com.example.group12_project.set_goal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.group12_project.R;

public class GoalDialog extends AppCompatActivity {
    public Button yes, notNow;

    public GoalDialog() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.goal_dialog);
        this.setFinishOnTouchOutside(false);

        yes = (Button) findViewById(R.id.btn_yes);
        notNow = (Button) findViewById(R.id.btn_no);

        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                launchActivity();
            }
        });

        notNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                finish();
            }
        });

    }

    public void launchActivity() {
        Intent intent = new Intent(this, RecommendedGoal.class);
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