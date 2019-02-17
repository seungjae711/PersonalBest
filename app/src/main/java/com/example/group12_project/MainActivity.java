package com.example.group12_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import com.example.group12_project.fitness.FitnessService;
import com.example.group12_project.fitness.FitnessServiceFactory;
import com.example.group12_project.fitness.GoogleFitAdapter;
import com.example.group12_project.set_goal.CustomGoal;
import com.example.group12_project.set_goal.GoalDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private BackgroundStepAsyncTask runner;
    private TextView daily_steps, goal, goalString;
    private Button addSteps, changeTime;
    private int numGoal, numSteps;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button read = findViewById(R.id.dataRead);

        FloatingActionButton fab = findViewById(R.id.fab);

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Button that will let us test data history
                fitnessService.dataSetup();
                fitnessService.dataReader();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /*GOAL SETTING*/
        /*SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        SharedPreferences.Editor editor = storedGoal.edit();

        //set first goal during first login
        if(storedGoal.getBoolean("firstStart",true)){
            editor.putString("goal", "5000");
            editor.putBoolean("firstStart", false);
            editor.apply();
        }

        goalString = findViewById(R.id.goal_string);
        goal = findViewById(R.id.goal);
        updateGoal(goal);

        //if user clicks goal they can change to new goal
        goalString.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                launchActivity();
            }
        });
        goal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                launchActivity();
            }
        });*/


        // Yixiang's implementation on basic daily steps counting
        daily_steps = findViewById(R.id.daily_steps);
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity mainActivity) {
                return new GoogleFitAdapter(mainActivity);
            }
        });

        /*CREATE FITNESS SERVICE*/
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        // starting async tasks
        runner = new BackgroundStepAsyncTask();
        runner.execute(0);


        /*TESTER BUTTONS*/
        /*addSteps = findViewById(R.id.add_steps);
        changeTime = findViewById(R.id.change_time);

        addSteps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //long steps = getDailyStepCount() + 100;
                //setStepCount(steps);
            }
        });

        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO change time
            }
        });*/


    }

    private void launchActivity() {
        Intent intent = new Intent(this, CustomGoal.class);
        startActivityForResult(intent, 1);
    }

    private void updateGoal(TextView goal) {
        SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        goal.setText(storedGoal.getString("goal",""));
    }

    // async task for update steps on background every 5 seconds
    private class BackgroundStepAsyncTask extends AsyncTask<Integer, Integer, Void> {

        int i;    // DELETE debug value

        // update steps every 5 seconds
        @Override
        protected Void doInBackground(Integer... paras) {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fitnessService.update_daily_steps();
                i++;
                publishProgress(i);

            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            String message = "Updated" + progress[0].toString();
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();


            /*SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
            numSteps = Integer.parseInt(getStepCount());
            numGoal = Integer.parseInt(storedGoal.getString("goal",""));

            //if goal is reached
            if(numSteps >= numGoal) {
                Intent newGoalDialog = new Intent(getApplicationContext(), GoalDialog.class);
                startActivityForResult(newGoalDialog, 1);
                updateGoal(goal);
            }*/
        }
    }

    // set daily step count to text view
    public void setStepCount(long stepCount) {
        daily_steps.setText(String.valueOf(stepCount));
    }

    //store step count of the day to local (sharedPreference)
    public void storeDailyStepCount(int date, long stepCount){
        SharedPreferences sharedPreferences = getSharedPreferences("daily_stepCount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(String.valueOf(date),stepCount);
        editor.apply();
        Toast.makeText(MainActivity.this, "today's step count is saved!", Toast.LENGTH_SHORT).show();
    }

    //weekly cumulative step count
    //Reset every seven days
    public void storeTotalStepCount(int day_of_week, long stepCount){

        SharedPreferences sharedPreferences = getSharedPreferences("weekly_stepCount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Starting to record the total (on a day other than Sunday)
        if(sharedPreferences.getLong("total_stepCount", -1) == -1){
            editor.putLong("total_stepCount",0);
            Toast.makeText(MainActivity.this, "Starting to record weekly step count!", Toast.LENGTH_SHORT).show();
        }
        // Reset total every Sunday.
        else if(day_of_week == Calendar.SUNDAY){
            editor.putLong("total_stepCount", stepCount);
            Toast.makeText(MainActivity.this, "Reset the total on Sunday", Toast.LENGTH_SHORT).show();
        }
        // Add up the steps
        else{
            long currentTotal = sharedPreferences.getLong("total_stepCount", 0);
            currentTotal += stepCount;
            editor.putLong("total_stepCount", currentTotal);
            Toast.makeText(MainActivity.this, "today's step count is added to total!", Toast.LENGTH_SHORT).show();
        }
    }

    public long getDailyStepCount(int date){
        SharedPreferences sharedPreferences = getSharedPreferences("daily_stepCount", MODE_PRIVATE);
        return sharedPreferences.getLong(String.valueOf(date),0);
    }

    public long getTotalStepCount(){
        SharedPreferences sharedPreferences = getSharedPreferences("weekly_stepCount", MODE_PRIVATE);
        return sharedPreferences.getLong("total_stepCount", 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if user updates goal
        if (resultCode == 1) {
            updateGoal(goal);
        }
        //If authentication was required during google fit setup, this will be called after the user authenticates
        else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.update_daily_steps();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }
    
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
