package com.andre.uberclone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends Activity {

    private Switch mSwitch;

    public void getStarted(View view) {
        ParseUser.getCurrentUser().
                put("riderDriver", mSwitch.isChecked() ? "driver" : "rider");

        ParseUser.getCurrentUser().saveInBackground();

        if(mSwitch.isChecked()){
            startActivity(new Intent(getApplicationContext(), ViewRequestActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), RiderActivity.class));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().hide();

        mSwitch = (Switch) findViewById(R.id.switch_uber);


        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.i("Uber", "Anonnymous login successful");
                    } else {
                        Log.i("Uber", "Anonnymous login failed");
                    }
                }
            });
        } else {
            String riderDriver = String.valueOf(ParseUser.getCurrentUser().get("riderDriver"));
            if (riderDriver != null) {
                if (riderDriver.equals("rider")) {
                    Log.i("Uber", "Logged as " + riderDriver);
                    mSwitch.setChecked(false);
                } else {
                    Log.i("Uber", "Logged as " + riderDriver);
                    mSwitch.setChecked(true);
                }
            }
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
