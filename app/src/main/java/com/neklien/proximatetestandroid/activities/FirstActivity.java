package com.neklien.proximatetestandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.neklien.proximatetestandroid.R;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        SharedPreferences sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String email = sharedPref.getString("email_preferences", "");

        if (email.equals("")) {
            Intent i = new Intent(this, LoginActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            finish();
        }else{
            Intent i = new Intent(this, ProfileActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            finish();
        }
    }
}
