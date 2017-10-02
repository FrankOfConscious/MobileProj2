package com.blackwood3.driveroo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //    Button logBtn=(Button) findViewById(R.id.button2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button logBtn=(Button) findViewById(R.id.button2);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });
    }
    private void login() {
//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                JSONObject post_result = null;

                EditText usernameET=(EditText)findViewById(R.id.usereditText);
                EditText passwordET=(EditText)findViewById(R.id.passwordeditText);
                String username=usernameET.getText().toString();
                String password=passwordET.getText().toString();
                params.put("username", username);
                params.put("password", password);
                try {
                    post_result = HttpUtils.submitPostData(params, "utf-8", "login");
                    try {
                        String loginStatus = post_result.getString("login_status");
                        Log.i("POST_RESULT", loginStatus);
                        if(loginStatus.equals("true")) {
                            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            homeIntent.putExtra(Intent.EXTRA_TEXT, username);
                            startActivity(homeIntent);
                        }else {
                            TextView invalidPair=(TextView) findViewById(R.id.textView3);
                            invalidPair.setText("Incorrect Username and Password Pair.");
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //    public void onLoginSuccess() {
//        logBtn.setEnabled(true);
//        finish();
//    }
//
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

//        logBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        EditText usernameET=(EditText)findViewById(R.id.usereditText);
        EditText passwordET=(EditText)findViewById(R.id.passwordeditText);
        String username=usernameET.getText().toString();
        String password=passwordET.getText().toString();

        if (username.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            usernameET.setError("enter a valid username");
            valid = false;
        } else {
            usernameET.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordET.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordET.setError(null);
        }

        return valid;
    }
}
