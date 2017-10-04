package com.blackwood3.driveroo;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView sign_in=(TextView) findViewById(R.id.signup_signin);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(signIn);
                finish();
            }
        });

        Button click_to_sign=(Button)findViewById(R.id.click_to_sign);
        click_to_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailET= (EditText)findViewById(R.id.emailET);
                String email=emailET.getText().toString();

                EditText passwordET= (EditText)findViewById(R.id.passwordET);
                String password1=passwordET.getText().toString();

                EditText passwordET2= (EditText)findViewById(R.id.passwordET2);
                String password2=passwordET2.getText().toString();

                EditText mobileET=(EditText)findViewById(R.id.phoneET);
                String mobileNo=mobileET.getText().toString();

                ImageView emailStatus=(ImageView) findViewById(R.id.status1);
                ImageView passwordStatus=(ImageView)findViewById(R.id.status2);
                ImageView setPassword=(ImageView) findViewById(R.id.set_password);
                ImageView mobile_status=(ImageView)findViewById(R.id.mobile);


                Boolean isMoblie=isMobileNO(mobileNo);
                Boolean isMatch= password1.equals(password2);
                Boolean nullpassword=password1.equals("");
                Boolean isEmailValid=false;

                if(testMail(email) && !nullpassword && isMatch && isMoblie){
                    // Send them to server to check is Email has been signed
                    isEmailValid=false; //change value from the respond of the server
                    emailStatus.setImageLevel(1);
                    setPassword.setImageLevel(1);
                    mobile_status.setImageLevel(1);
                    passwordStatus.setImageLevel(1);
                    if(email.equals("y@y.y")||isEmailValid){
                        Intent jumpToLogin= new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(jumpToLogin);
                        finish();
                    }else{
                        //Image1.set
                        emailStatus.setImageLevel(2);
                        emailET.setHint("Account already exists");

                    }
                }else{


                    if(!testMail(email)) {
                        //Image1.set
                        emailStatus.setImageLevel(2);
                        emailET.setHint("Invalid Email address");
                    }else emailStatus.setImageLevel(1);
                    if(!isMatch) {
                        //Image2.set
                        passwordStatus.setImageLevel(2);
                        passwordET2.setHint("Not consistent");
                    }else passwordStatus.setImageLevel(1);

                    if(nullpassword) {setPassword.setImageLevel(2);passwordStatus.setImageLevel(2);}
                    else setPassword.setImageLevel(1);
                    if(isMoblie) mobile_status.setImageLevel(1);
                    else mobile_status.setImageLevel(2);

                }



            }
        });


    }

    public static boolean isMobileNO(String mobiles) {

        String telRegex = "^\\({0,1}((0|\\+61)(2|4|3|7|8)){0,1}\\){0,1}(\\ |-){0,1}[0-9]{2}(\\ |-){0,1}[0-9]{2}(\\ |-){0,1}[0-9]{1}(\\ |-){0,1}[0-9]{3}$";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static boolean testMail(String strMail) {


        if(strMail.indexOf("@") == -1 || strMail.indexOf(".") == -1) {
           // System.out.println("1");
            return false;
        }

        if(strMail.indexOf("@") != strMail.lastIndexOf("@") || strMail.indexOf(".") != strMail.lastIndexOf(".")){
            //System.out.println("2");
            return false;
        }

        if(strMail.indexOf("@")>strMail.indexOf(".")) {
           // System.out.println("3");
            return false;
        }

        for(int i = 0; i<strMail.indexOf("@"); i++) {
            if(!((strMail.charAt(i) > 'A' && strMail.charAt(i) < 'Z') || (strMail.charAt(i) > 'a' && strMail.charAt(i) < 'z'))) {
              //  System.out.println("4");
                return false;
            }
        }

        return true;
    }



}
