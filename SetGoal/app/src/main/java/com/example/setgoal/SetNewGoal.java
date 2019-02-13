package com.example.setgoal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class SetNewGoal extends AppCompatActivity /*implements Observer*/ {

    public Button btnSave;
    public TextView goal, steps;
    int numGoal, numSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        goal = findViewById(R.id.goal);
        steps = findViewById(R.id.steps);

        /*REMOVING OBSERVABLES
        GoalData stepGoal = new GoalData();
        RecommendedGoal recGoal = new RecommendedGoal();
        GoalDialog dialog = new GoalDialog();
        stepGoal.addObserver(this);
        stepGoal.addObserver(recGoal);
        //goal.setText(stepGoal.getGoal());
        Toast.makeText(getApplicationContext(), stepGoal.getGoal(), Toast.LENGTH_LONG).show();*/

        numSteps = Integer.parseInt(steps.getText().toString());
        numGoal = Integer.parseInt(goal.getText().toString());

        //if steps reached
        if(numSteps >= numGoal) {
            Intent newGoalDialog = new Intent(this, GoalDialog.class);
            startActivityForResult(newGoalDialog, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (data.hasExtra("NewGoal")) {
                TextView goal = findViewById(R.id.goal);
                goal.setText(data.getStringExtra("NewGoal"));
            }
        }
    }


    /* REMOVING OBSERVABLES
    @Override
    public void update(Observable o, Object arg){
        numGoal = (int) arg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goal.setText(String.valueOf(numGoal));
            }
        });

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
