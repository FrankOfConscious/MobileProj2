package com.blackwood3.driveroo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar)findViewById(R.id.);
//        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_login);

        Button logBtn=(Button) findViewById(R.id.button2);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameET=(EditText)findViewById(R.id.usereditText);
                EditText passwordET=(EditText)findViewById(R.id.passwordeditText);
                String username=usernameET.getText().toString();
                String password=passwordET.getText().toString();
                if(username.equals("admin") && password.equals("admin")){
                    Intent homeIntent= new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeIntent);
                }else{
                    TextView invalidPair=(TextView) findViewById(R.id.textView3);
                    invalidPair.setText("Invalid Username and Password Pair.");
                }
            }
        });
    }
}
