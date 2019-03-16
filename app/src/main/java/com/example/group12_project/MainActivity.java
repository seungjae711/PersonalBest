package com.example.group12_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.group12_project.friendlist.FriendListActivity;
import com.example.group12_project.friendlist.LocalUser;
import com.example.group12_project.friendlist.UserCloud;
import com.example.group12_project.friendlist.UserCloudMediator;
import com.example.group12_project.sessions.BarChartMediator;
import com.example.group12_project.sessions.SessionReader;
import com.example.group12_project.sessions.StatsDialog;
import com.example.group12_project.sessions.stepSession;
import com.example.group12_project.set_goal.CustomGoal;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        
    private TextView timerClock;
    private Button button_start, button_end;
    private TimerKeeper time;
    private long ellapsedTimer;
    private String fitnessServiceKey = "GOOGLE_FIT";
    private BackgroundStepAsyncTask runner;
    private TextView daily_steps, goal, goalString;
    private Button addSteps, changeTime;
    stepSession stepSesh;
    //private long numGoal, numSteps;
    private static final String TAG = "MainActivity";
    boolean isPaused = false;
    boolean goalReached = false;
    DataReader reader;
    SessionReader seshReader;
    BarChartMediator bcm;

    Calendar cal;
    EditText timeEntered;

    LocalUser localUser;
    UserCloud userCloud;
    UserCloudMediator userCloudMediator;

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* create friend list objects */

        SharedPreferences Email = getSharedPreferences("daily_stepCount", MODE_PRIVATE);
        userId = Email.getString("email", "default_user");
        FirebaseApp.initializeApp(this);
        localUser = new LocalUser(userId);
        userCloud = new UserCloud(localUser.getId());
        userCloudMediator = new UserCloudMediator(localUser, userCloud);
        localUser.register(userCloudMediator);
        userCloud.register(userCloudMediator);
        LocalUser.setLocalUser(localUser);
        UserCloud.setUserCloud(userCloud);

        /* launch bar chart */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBarChart();
            }
        });
        cal = Calendar.getInstance();
        timeEntered = (EditText)findViewById(R.id.edit_Time);

        /* drawer navigation */

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // For the drawer menu items
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* intentional work out session */

        time = new TimerKeeper();
        timerClock = findViewById(R.id.time);

        // end button needs to be default hidden
        button_end = findViewById(R.id.endTimer);
        button_end.setVisibility(View.INVISIBLE);

        button_start = findViewById(R.id.startTimer);
        button_start.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v ){
                // get start time a timer
                stepSesh = new stepSession(MainActivity.this);
                stepSesh.start();
                time.setStart();
                timerClock.setText("Have Fun Stepping!");
                button_start.setVisibility(View.INVISIBLE);
                button_end.setVisibility(View.VISIBLE);
            }
        });

        button_end.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v ){
                // TODO use ellapsed time for stats and average
                stepSesh.end();

                ellapsedTimer = time.getEllapsedTime();

                button_start.setVisibility(View.VISIBLE);
                button_end.setVisibility(View.INVISIBLE);
                timerClock.setText("Time Your Steps!");
                statsLaunch(stepSesh);
                //Toast.makeText(MainActivity.this, Long.toString(ellapsedTimer), Toast.LENGTH_LONG).show();
                if(ellapsedTimer > 200000000) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setCancelable(true);
                    alert.setMessage("Good Job on Finishing up a Timed Session!");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            }
        });

        /* goal */

        SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);

        // set first goal during first login
        if(storedGoal.getBoolean("firstStart",true)){
            firstLaunch();
        }

        // update height and goal to cloud
        SharedPreferences height = getSharedPreferences("height", MODE_PRIVATE);
        localUser.setHeight(height.getInt("height", -1));
        localUser.setGoalManagement(this);
        goalString = findViewById(R.id.goal_string);
        goal = findViewById(R.id.goal);
        localUser.goalManagement.updateGoal(goal);
        SharedPreferences newGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        localUser.setGoal(newGoal.getString("goal", ""));

        // update local user
        userCloud.updateRequest();
        userCloud.updateFriends();

        //if user clicks goal they can change to new goal
        goalString.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                customGoalActivity();
            }
        });
        goal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                customGoalActivity();
            }
        });

        /* fitness service setup */

        daily_steps = findViewById(R.id.daily_steps);
        localUser.createFitnessService(fitnessServiceKey, this);

        /* starting async tasks */

        runner = new BackgroundStepAsyncTask();
        runner.execute(0);


        /*TESTER BUTTONS*/
        addSteps = findViewById(R.id.add_steps);
        changeTime = findViewById(R.id.change_time);

        addSteps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                TextView stepsTv = findViewById(R.id.daily_steps);
                long steps =  Long.parseLong(stepsTv.getText().toString())+ 500;
                setStepCount(steps);
            }
        });

        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(timeEntered.getText().toString())) {
                    String time = timeEntered.getText().toString();
                    cal.setTimeInMillis(Long.parseLong(time));
                }
            }
        });

        //TODO test here, delete later
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String strDate = dateFormat.format(date);
        localUser.setHistory(strDate, 88);
    }

    /**
     * this activity let's you custom your goal
     */
    private void customGoalActivity() {
        Intent intent = new Intent(this, CustomGoal.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if user updates goal
        if (resultCode == 1) {
            localUser.goalManagement.updateGoal(goal);
            SharedPreferences newGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
            localUser.setGoal(newGoal.getString("goal", ""));
            isPaused = false;
        }
        //If authentication was required during google fit setup, this will be called after the user authenticates
        else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == localUser.fitnessService.getRequestCode()) {
                localUser.fitnessService.update_daily_steps();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    /**
     * show recent work out history via barchart
     */
    private void launchBarChart() {
//        Intent intent = new Intent(this, StepChart.class);
//        startActivity(intent);
        final Calendar today = Calendar.getInstance();
        final Calendar lastWeek = Calendar.getInstance();
        Date day = new Date();
        today.setTime(day);
        lastWeek.setTime(day);
        lastWeek.add(Calendar.DAY_OF_YEAR, -7);

        bcm = new BarChartMediator(this);

        seshReader = new SessionReader(this);
        seshReader.setTimeFrame(lastWeek, today);

        reader = new DataReader(this, lastWeek.getTimeInMillis(), today.getTimeInMillis());

        seshReader.register(bcm);
        reader.register(bcm);

        seshReader.aggregateSessionSteps();
        reader.aggregateStepsByDay(7);
    }

    private void statsLaunch(stepSession stepSesh) {
       /* SharedPreferences.Editor statsEdit = getSharedPreferences("stats", MODE_PRIVATE).edit();
        statsEdit.putLong("speed", (long)speed);
        statsEdit.putLong("steps", steps);
        statsEdit.putLong("time", time);
        statsEdit.apply();

        Intent intent = new Intent(this, StatsDialog.class);
        startActivity(intent); */
       stepSesh.launchDialog(stepSesh);
    }

    private void firstLaunch() {
        SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        SharedPreferences.Editor edit = storedGoal.edit();
        edit.putString("goal", "5");
        localUser.setGoalManagement(this);
        localUser.setGoal("5");
        userCloud.setUserId(localUser.getId());
        edit.putBoolean("firstStart", false);
        edit.apply();
        Intent intent = new Intent(this, HeightEmailManager.class);
        startActivity(intent);
    }


    // async task for update steps on background every 5 seconds
    private class BackgroundStepAsyncTask extends AsyncTask<Integer, Integer, Void> {

        // update steps every 5 seconds
        @Override
        protected Void doInBackground(Integer... paras) {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                localUser.fitnessService.update_daily_steps();
                if(!isPaused) {
                    publishProgress();
                }

            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.i(TAG, "Checking Goal");
            isPaused = localUser.goalManagement.checkIfGoalReached(isPaused);
            if(!isPaused){
                localUser.goalManagement.checkIfHalfGoal();
            }
        }
    }

    // set daily step count to text view
    public void setStepCount(final long stepCount) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                daily_steps.setText(String.valueOf(stepCount));

            }
        });

    }

    //store step count of the day to local (sharedPreference)
    public void storeDailyStepCount(int date, long stepCount){
        SharedPreferences sharedPreferences = getSharedPreferences("daily_stepCount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(String.valueOf(date),stepCount);
        editor.apply();
        //Toast.makeText(MainActivity.this, "today's step count is saved!", Toast.LENGTH_SHORT).show();
    }

    //weekly cumulative step count
    //Reset every seven days
    public void storeTotalStepCount(int day_of_week, long stepCount){

        SharedPreferences sharedPreferences = getSharedPreferences("weekly_stepCount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Starting to record the total (on a day other than Sunday)
        if(sharedPreferences.getLong("total_stepCount", -1) == -1){
            editor.putLong("total_stepCount",0);
            //Toast.makeText(MainActivity.this, "Starting to record weekly step count!", Toast.LENGTH_SHORT).show();
        }
        // Reset total every Sunday.
        else if(day_of_week == Calendar.SUNDAY){
            editor.putLong("total_stepCount", stepCount);
            //Toast.makeText(MainActivity.this, "Reset the total on Sunday", Toast.LENGTH_SHORT).show();
        }
        // Add up the steps
        else{
            long currentTotal = sharedPreferences.getLong("total_stepCount", 0);
            currentTotal += stepCount;
            editor.putLong("total_stepCount", currentTotal);
            //Toast.makeText(MainActivity.this, "today's step count is added to total!", Toast.LENGTH_SHORT).show();
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


    public double calculateAverageSpeed(){
        SharedPreferences sharedPreferences = getSharedPreferences("height", MODE_PRIVATE);
        double height = (double)sharedPreferences.getInt("height",0); //TODO:change key value
        if(height != 0){
            //Multiply height in inches by 0.413. This is a predetermined number that figures out average stride length.
            //Source: https://www.openfit.com/how-many-steps-walk-per-mile
            height = height * 0.413;
            height = height/12; //convert to feet (/step)
        }
        else{
            Toast.makeText(MainActivity.this, "User height data not found!",Toast.LENGTH_LONG).show();
        }
        long stepWalked = getDailyStepCount(cal.get(Calendar.DAY_OF_WEEK));
        //Get the intentional walking time
        long timeElapsed = time.getEllapsedTime();
        //calculate total distance from steps*stride length
        double distance = stepWalked * height;
        double averageSpeed = distance/timeElapsed;

        return averageSpeed;
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        Fragment fragment = null;
        Bundle bundle = new Bundle();

        if (id == R.id.nav_friends) {
            launchFriendListActivity();
        } else if (id == R.id.nav_mainpage) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * launch friendlist screen
     */
    public void launchFriendListActivity(){
        Intent intent = new Intent(this, FriendListActivity.class);
        startActivity(intent);
    }
}
