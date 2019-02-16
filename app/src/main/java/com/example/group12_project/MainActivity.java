package com.example.group12_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
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

import com.example.group12_project.fitness.FitnessService;
import com.example.group12_project.fitness.FitnessServiceFactory;
import com.example.group12_project.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView goal, goalString;
    int numGoal, numSteps;

    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private BackgroundStepAsyncTask runner;
    private TextView daily_steps;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Yixiang's implementation on basic daily steps counting
        daily_steps = findViewById(R.id.daily_steps);
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity mainActivity) {
                return new GoogleFitAdapter(mainActivity);
            }
        });
        
        //TODO set first goal during first login
        SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        SharedPreferences.Editor editor = storedGoal.edit();
        editor.putString("goal", "5000");
        editor.apply();

        /*GOAL SETTING*/
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
        });

        numSteps = Integer.parseInt(getStepCount());
        numGoal = Integer.parseInt(storedGoal.getString("goal",""));

        //if steps reached
        //TODO put in inBackground
        if(numSteps >= numGoal) {
            Intent newGoalDialog = new Intent(this, GoalDialog.class);
            startActivityForResult(newGoalDialog, 1);
            updateGoal(goal);
        }

        // create fitness service
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        // starting async tasks
        runner = new BackgroundStepAsyncTask();
        runner.execute(0);

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
        }
    }

    // set daily step count to text view
    public void setStepCount(long stepCount) {
        daily_steps.setText(String.valueOf(stepCount));
    }
    
    //get daily step count
    public String getStepCount() {
        return daily_steps.getText().toString();
    }

    //DELETE not sure what's the usage
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

    public void updateGoal(TextView goal) {
        SharedPreferences storedGoal = getSharedPreferences("storedGoal", MODE_PRIVATE);
        goal.setText(storedGoal.getString("goal",""));
    }

    public void launchActivity() {
        Intent intent = new Intent(this, CustomGoal.class);
        startActivityForResult(intent, 1);
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
