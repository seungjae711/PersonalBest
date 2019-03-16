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

import com.example.group12_project.friendlist.LocalUser;

public class HeightEmailManager extends AppCompatActivity {
    public Button confirm;
    public EditText heightInput;
    public EditText emailInput;

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
                setEmailAndHeight();
            }
        });

    }

    public void setEmailAndHeight() {

        // set height
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

        // set email
        emailInput = findViewById(R.id.email_input);
        String email = emailInput.getText().toString();
        LocalUser user = LocalUser.getLocalUser();
        user.setId(email);
        SharedPreferences.Editor editEmail = getSharedPreferences("daily_stepCount", MODE_PRIVATE).edit();
        editEmail.putString("email", email);
        editEmail.apply();

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