package com.example.group12_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.group12_project.R;

public class HeightManager extends AppCompatActivity {
    public Button confirm;
    public EditText heightInput;
    //public heightManager() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.height_dialog);
        this.setFinishOnTouchOutside(false);

        confirm = (Button) findViewById(R.id.btn_height);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                setHeight();
            }
        });

    }

    public void setHeight() {
        heightInput = (EditText) findViewById(R.id.height_input);
        if (heightInput.getText().toString().equals("")) {
            Toast.makeText(this,"Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        int height = Integer.parseInt( heightInput.getText().toString() );
        if (height <= 0) {
            Toast.makeText(this,"Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editHeight = getSharedPreferences("height", MODE_PRIVATE).edit();
        editHeight.putInt("height",height);
        editHeight.apply();
        finish();
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