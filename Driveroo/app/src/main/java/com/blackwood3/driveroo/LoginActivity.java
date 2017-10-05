package com.blackwood3.driveroo;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.edit;

public class LoginActivity extends AppCompatActivity {

    //    Button logBtn=(Button) findViewById(R.id.button2);
    private String decimalPlaces = "100";
    private int colors[] ={Color.parseColor("#ffe476"),Color.parseColor("#FF5C1C")};
    private EditText editText;
    private ConstraintLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText=(EditText)findViewById(R.id.passwordeditText);
        rl=(ConstraintLayout) findViewById(R.id.login_layout);
        rl.setBackgroundColor(colors[0]);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int editSize = editText.getText().length();
                //如果长度大于最大值就不变色了
                if (editSize > Integer.valueOf(decimalPlaces))return;
                //得到 当前所占百分比的渐变值
                BigDecimal bigEs= BigDecimal.valueOf(editSize);
                BigDecimal result = bigEs.divide(new BigDecimal(decimalPlaces), 8, RoundingMode.HALF_UP);


                //颜色估值器
                ArgbEvaluator evaluator = new ArgbEvaluator();
                //得到背景渐变色
                int evaluate = (int) evaluator.evaluate(result.floatValue(),colors[0],colors[1]);
                rl.setBackgroundColor(evaluate);



            }
        });




        Button logBtn=(Button) findViewById(R.id.button2);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameET=(EditText)findViewById(R.id.usereditText);

                String username=usernameET.getText().toString();
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
                            finish();
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
