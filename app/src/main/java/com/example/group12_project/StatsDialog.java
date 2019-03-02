package com.example.group12_project;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.stats_dialog);
        this.setFinishOnTouchOutside(false);

        back = (Button) findViewById(R.id.btn_return);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                finish();
            }
        });

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