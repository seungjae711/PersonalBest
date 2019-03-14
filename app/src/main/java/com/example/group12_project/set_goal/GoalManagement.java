package com.example.group12_project.set_goal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.example.group12_project.R;

import static android.content.Context.MODE_PRIVATE;

public class GoalManagement {

    private Activity activity;
    private long numSteps;
    private long numGoal;
    private TextView goal;

    public GoalManagement(Activity activity) {
        this.activity = activity;
        numSteps = numGoal = 0;
    }

    public String updateGoal(TextView goal) {
        SharedPreferences storedGoal = activity.getSharedPreferences("storedGoal", MODE_PRIVATE);
        String newGoal = storedGoal.getString("goal", "");
        goal.setText(newGoal);
        numGoal = Long.parseLong(newGoal);
        return newGoal;
    }

    public long getGoal() {
        return numGoal;
    }

    public void setGoal(String newGoal) {
        SharedPreferences storedGoal = activity.getSharedPreferences("storedGoal", MODE_PRIVATE);
        SharedPreferences.Editor editGoal = storedGoal.edit();
        editGoal.putString("goal", newGoal);
        editGoal.apply();
        goal = activity.findViewById(R.id.goal);
        updateGoal(goal);
    }

    public void checkIfHalfGoal() {
        SharedPreferences storedGoal = activity.getSharedPreferences("storedGoal", MODE_PRIVATE);
        TextView stepsTv = activity.findViewById(R.id.daily_steps);
//        numSteps = Long.parseLong(stepsTv.getText().toString());
//        numGoal = Long.parseLong(storedGoal.getString("goal", ""));

        if ((numSteps >= (numGoal / 2)) && (numSteps < numGoal)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this.activity);

            alert.setCancelable(true);
            alert.setMessage("You've nearly doubled your steps. Keep up the good work!");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public boolean checkIfGoalReached(boolean isPaused) {
        /*Button implementation*/

        goal = activity.findViewById(R.id.goal);

        SharedPreferences storedGoal = activity.getSharedPreferences("storedGoal", MODE_PRIVATE);
        TextView stepsTv = activity.findViewById(R.id.daily_steps);
//        numSteps = Long.parseLong(stepsTv.getText().toString());
//        numGoal = Long.parseLong(storedGoal.getString("goal", ""));

        //if goal is reached
        if (numSteps >= numGoal) {
            isPaused = true;
            //Go to next acitivity to set up new goal
            Intent newGoalDialog = new Intent(activity.getApplicationContext(), GoalDialog.class);
            activity.startActivityForResult(newGoalDialog, 1);
            updateGoal(goal);
            return true;
        }
        return false;

    }
}