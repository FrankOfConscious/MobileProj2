package com.blackwood3.driveroo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
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
    private static final int WRONG = 0;
    private static final int RIGHT = 1;
    TextView invalidPair;
    private EditText editText;
    private ConstraintLayout rl;
    private TextView stateText;

    /**
     * Handler will get the message passed from the work thread, it will change the UI based on two
     * different results(permitted and denied) from server.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WRONG) {
                stateText.setText("Incorrect Username and Password Pair.");
                stateText.setTextColor(Color.RED);
            }
            if (msg.what == RIGHT) {
                stateText.setTextColor(Color.BLACK);
                stateText.setText("Authenticating...");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        stateText=(TextView)findViewById(R.id.textView3) ;
        editText=(EditText)findViewById(R.id.passwordeditText);

        /**
         * Set a Click Listener on Button "logBtn", pressing this button will send the user & password pair to
         *the server and try to login. If been permitted, jump to HomeActivity which is the guide page with
         *greeting, and finish this login activity. If been denied, keep this activity.
         */
        Button logBtn=(Button) findViewById(R.id.button2);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameET=(EditText)findViewById(R.id.usereditText);
                String username=usernameET.getText().toString();
                //Being convenient to the developers, there is a backdoor: using username "admin"  to log in without
                //requesting the server.
                if(username.equals("admin")){
                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    homeIntent.putExtra(Intent.EXTRA_TEXT, username);
                    startActivity(homeIntent);
                    finish();
                }else{
                    login();
                }
            }
        });

        /**
         *Set a Click Listener on TextView "signup", pressing this button will guide the user to sign up a new
         *account. After jumping to that page, this activity will be finished.
         */
        TextView signUp=(TextView)findViewById(R.id.login_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign_activity= new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(sign_activity);
                finish();
            }
        });

    }


    /**Start a thread to send the login request to the server, and get server's respond. The server's
     *respond will be passed to the main thread by Message handled by Handler.
     */
    private void login() {
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
                            Message msg= new Message();
                            msg.what=RIGHT;
                            handler.sendMessage(msg);
                            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            homeIntent.putExtra(Intent.EXTRA_TEXT, username);
                            startActivity(homeIntent);
                            finish();
                        }else {
                            Message msg= new Message();
                            msg.what=WRONG;
                            handler.sendMessage(msg);
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

    /**
     * For developers to test, you can ignore this. Not used any more in final version.
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    /**
     * For developers to validate the username and password, you can ignore this. Not used any more in final version.
     */
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
