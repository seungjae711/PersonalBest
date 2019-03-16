package com.example.group12_project.sessions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group12_project.R;

public class StatsDialog extends AppCompatActivity {
    public Button back;
    public TextView step, time, speed;
    SharedPreferences stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.stats_dialog);
        this.setFinishOnTouchOutside(false);

        stats = getSharedPreferences("stats", MODE_PRIVATE);

        setSpeedText();
        setStepsText();
        setTimeText();

        back = (Button) findViewById(R.id.btn_return);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                finish();
            }
        });

    }


    private void setSpeedText() {
        TextView avg_spd = findViewById(R.id.avg_spd);
        avg_spd.setText(Float.toString(stats.getFloat("speed", 0)));
    }

    private void setStepsText() {
        TextView steps_taken = findViewById(R.id.steps_taken);
        steps_taken.setText(Long.toString(stats.getLong("steps", 0)));
    }

    private void setTimeText() {
        String timeFormat;
        TextView time_elapsed = findViewById(R.id.time_elapsed);
        Long time = stats.getLong("time", 0);

        if (time/3600 < 10) {
            timeFormat = "0" + Long.toString(time/3600); //hours
        }
        else {
            timeFormat = Long.toString(time/3600); //hours
        }
        time = time % 3600;

        if (time/60 < 10) {
            timeFormat = timeFormat + ":0" + Long.toString(time / 60);
        }
        else {
            timeFormat = timeFormat + ":" + Long.toString(time/60); //minutes
        }
        time = time % 60;

        if (time < 10) {
            timeFormat = timeFormat + ":0" + Long.toString(time);
        }
        else {
            timeFormat = timeFormat + ":" + Long.toString(time);
        }
        time_elapsed.setText(timeFormat);
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