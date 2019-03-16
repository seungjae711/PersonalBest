package com.example.group12_project.set_goal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.group12_project.NotificationBuilder;
import com.example.group12_project.R;

import static android.content.Context.MODE_PRIVATE;

public class GoalManagement extends AppCompatActivity {

    private Activity activity;
    private long numSteps;
    private long numGoal;
    private TextView goal;
    private Context context;

    public GoalManagement(Activity activity) {
        this.activity = activity;
        numSteps = numGoal = 0;
    }

    public GoalManagement(Activity activity, Context context) {
        this.activity = activity;
        numSteps = numGoal = 0;
        this.context = context;
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
        SharedPreferences halfGoal = activity.getSharedPreferences("GoalChecker", MODE_PRIVATE);
        SharedPreferences.Editor editor = halfGoal.edit();
        editor.putBoolean("goal", false);
        editor.putBoolean("halfGoal", false);
        editor.apply();
    }

    public boolean checkIfHalfGoal() {
        SharedPreferences storedGoal = activity.getSharedPreferences("storedGoal", MODE_PRIVATE);
        TextView stepsTv = activity.findViewById(R.id.daily_steps);
        numSteps = Long.parseLong(stepsTv.getText().toString());
        numGoal = Long.parseLong(storedGoal.getString("goal", ""));

        // check if it is already notified
        SharedPreferences halfGoal = activity.getSharedPreferences("GoalChecker", MODE_PRIVATE);
        Boolean checker = halfGoal.getBoolean("halfGoal", false);

        if ((numSteps >= (numGoal / 2)) && (numSteps < numGoal) && !checker) {
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
            SharedPreferences.Editor editor = halfGoal.edit();
            editor.putBoolean("halfGoal", true);
            editor.apply();
            return true;
        }
        return false;
    }

    public boolean checkIfGoalReached(boolean isPaused) {
        /*Button implementation*/

        goal = activity.findViewById(R.id.goal);

        SharedPreferences storedGoal = activity.getSharedPreferences("storedGoal", MODE_PRIVATE);
        TextView stepsTv = activity.findViewById(R.id.daily_steps);
        numSteps = Long.parseLong(stepsTv.getText().toString());
        numGoal = Long.parseLong(storedGoal.getString("goal", ""));

        // check if it is already notified
        SharedPreferences goal = activity.getSharedPreferences("GoalChecker", MODE_PRIVATE);
        Boolean checker = goal.getBoolean("goal", false);

        //if goal is reached
        if (numSteps >= numGoal && !checker) {
            isPaused = true;
            //Go to next acitivity to set up new goal
            Intent newGoalDialog = new Intent(activity.getApplicationContext(), GoalDialog.class);
            activity.startActivityForResult(newGoalDialog, 1);
            updateGoal(this.goal);
            SharedPreferences.Editor editor = goal.edit();
            editor.putBoolean("goal", true);
            editor.apply();

            // notification for goal reached
//            NotificationBuilder note = new NotificationBuilder(getApplicationContext(), "Goal Reached", "You have readched " + numGoal);
//            note.createNotification();
//        }).addOnFailureListener(error -> {
//            Log.e(TAG, error.getLocalizedMessage());
//        });
            return true;
        }
        return false;

    }
}