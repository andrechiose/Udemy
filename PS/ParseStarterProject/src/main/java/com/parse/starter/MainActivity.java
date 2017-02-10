/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName().toString();
    private EditText edtUsername;
    private EditText edtPassword;
    private TextView txtChangeMode;
    private Button btnSignUp;

    private boolean isSignUpMode = true;

    public void showUserList(){

        startActivity(new Intent(getApplicationContext(),UserListActivity.class));
    }

    public void signUp(View view) {

        if (TextUtils.isEmpty(edtUsername.getText()) || TextUtils.isEmpty(edtPassword.getText())) {
            Toast.makeText(this, "A username and password are required", Toast.LENGTH_SHORT).show();
        } else {

            if (isSignUpMode) {
                ParseUser user = new ParseUser();
                user.setUsername(edtUsername.getText().toString());
                user.setPassword(edtPassword.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i(TAG, "Sign up Successful");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(edtUsername.getText().toString(),
                        edtPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    showUserList();
                                    Log.i(TAG, "Login Successful");
                                } else {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Instagram");

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        txtChangeMode = (TextView) findViewById(R.id.txtChangeMode);

        txtChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignUpMode) {
                    btnSignUp.setText("Login");
                    txtChangeMode.setText("or, SignUp");
                    isSignUpMode = false;
                } else {
                    btnSignUp.setText("SignUp");
                    txtChangeMode.setText("or, Login");
                    isSignUpMode = true;
                }
            }
        });

        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    signUp(v);
                }

                return false;
            }
        });

        if(ParseUser.getCurrentUser() != null){
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
