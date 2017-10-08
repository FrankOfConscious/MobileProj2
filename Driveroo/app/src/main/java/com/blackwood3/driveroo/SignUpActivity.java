package com.blackwood3.driveroo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    Boolean responded;
    Boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        View v=(View)findViewById(R.id.bg_signup);
        Date dt=new Date();
        String hour=dt.toString().split(" ")[3].split(":")[0];
        int hourInt=Integer.parseInt(hour);
        String[] morningPic={"morning"};
        String[] afternoonPic={"afternoon2","afternoon_meitu_4"};
        String[] eveningPic={"evening_meitu_5","night2","road1_meitu_2_meitu_3"};
        String backg;
        TextView sign_up_intro1=(TextView) findViewById(R.id.sign_up_intro1);
        ImageView sign_up_logo=(ImageView)findViewById(R.id.imageView11);

        /**
         * Randomly set this activity's background based on different time of the day.
         */
        if (hourInt>=4 && hourInt<12) {
            int random=(int)(Math.random()*(morningPic.length));
            backg=morningPic[random];
            sign_up_intro1.setTextColor(Color.BLACK);
        }
        else {
            if (hourInt >= 12 && hourInt < 18){
                int random=(int)(Math.random()*(afternoonPic.length));
                backg=afternoonPic[random];
            }
            else {
                int random=(int)(Math.random()*(eveningPic.length));
                backg=eveningPic[random];
            }
        }
        int id=getResources().getIdentifier(backg,"drawable",getPackageName());
        v.setBackground(getDrawable(id));

        /**
         * Set a Click Listener on TextView "sign_in", press this will give up sign up and
         * directly jump to login page. After that, this sign up activity will be finished.
         */
        TextView sign_in=(TextView) findViewById(R.id.signup_signin);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(signIn);
                finish();
            }
        });

        /**
         * Set a Click Listener on Button "click_to_sign", press this button will send all
         * the information that user typed in and request the server to sign up.
         */
        Button click_to_sign=(Button)findViewById(R.id.click_to_sign);
        click_to_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responded=false;
                EditText emailET= (EditText)findViewById(R.id.emailET);
                String email=emailET.getText().toString();

                EditText passwordET= (EditText)findViewById(R.id.passwordET);
                String password1=passwordET.getText().toString();

                EditText passwordET2= (EditText)findViewById(R.id.passwordET2);
                String password2=passwordET2.getText().toString();

                EditText mobileET=(EditText)findViewById(R.id.phoneET);
                String mobileNo=mobileET.getText().toString();

                EditText userNameET=(EditText) findViewById(R.id.userNameET);
                String userName=userNameET.getText().toString();

                ImageView emailStatus=(ImageView) findViewById(R.id.status1);
                ImageView passwordStatus=(ImageView)findViewById(R.id.status2);
                ImageView setPassword=(ImageView) findViewById(R.id.set_password);
                ImageView mobile_status=(ImageView)findViewById(R.id.mobile);
                ImageView userNameStatus=(ImageView)findViewById(R.id.statusUser);

                Boolean isMoblie=isMobileNO(mobileNo);
                Boolean isMatch= password1.equals(password2);
                Boolean nullpassword=password1.equals("");
                Boolean isEmailValid=false;
                Boolean isValidUserName=!userName.equals("");

                if(testMail(email) && !nullpassword && isMatch && isMoblie && isValidUserName){
                    // Send them to server to check if the username has already been signed
                    responded=false;
                    success=false;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", userName);
                    params.put("email",email);
                    params.put("mobile",mobileNo);
                    params.put("password", password1);
                    params.put("ifWarning","false");
                    params.put("ifRecovery","false");
                    params.put("ifStart","false");
                    params.put("car","2");
                    params.put("face","");
                    params.put("index","1");
                    signup(params);

                    //change UI if the all the typed in are invalid.
                    userNameStatus.setImageLevel(1);
                    emailStatus.setImageLevel(1);
                    setPassword.setImageLevel(1);
                    mobile_status.setImageLevel(1);
                    passwordStatus.setImageLevel(1);
                    long timeFlag=System.currentTimeMillis()+5000;

                    //wait for the server's response, the timeout is 5000ms.
                    while(true){
                        if(responded==true ||
                               System.currentTimeMillis()>=timeFlag ) break;
                    }
                    isEmailValid=success;
                    if(success){
                        Toast.makeText(SignUpActivity.this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                        Intent jumpToLogin= new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(jumpToLogin);
                        finish();
                    }else{
                        //change UI based on the server's response.

                        if(responded) {
                            //change UI based on the server's response.
                            userNameStatus.setImageLevel(2);
                            userNameET.setHint("Account already exists");
                            Toast.makeText(SignUpActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //change UI based on the server's response.
                            userNameET.setHint("The server is not responding");
                            userNameStatus.setImageLevel(1);
                            Toast.makeText(SignUpActivity.this, "Server time-out, try later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    if(!testMail(email)) {
                        //change UI based on user's type
                        emailStatus.setImageLevel(2);
                        emailET.setHint("Invalid Email address");
                    }else emailStatus.setImageLevel(1);

                    if(!isMatch) {
                        //change UI based on user's type
                        passwordStatus.setImageLevel(2);
                        passwordET2.setHint("Not consistent");
                    }else passwordStatus.setImageLevel(1);

                    if(nullpassword) {
                        //change UI based on user's type
                        setPassword.setImageLevel(2);
                        passwordStatus.setImageLevel(2);
                    } else setPassword.setImageLevel(1);
                    //change UI based on user's type
                    if(isMoblie) mobile_status.setImageLevel(1);
                    else mobile_status.setImageLevel(2);
                    //change UI based on user's type
                    if(isValidUserName) userNameStatus.setImageLevel(1);
                    else userNameStatus.setImageLevel(2);
                }

            }
        });


    }

    /**
     * This work thread will send a request to server to sign up. And then get the server's response.
     * @param params
     */
    private void signup(final Map<String, String> params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // need to be deleted
                JSONObject post_result = null;

                try {
                    post_result = HttpUtils.submitPostData(params, "utf-8", "signup");
                    try {
                        String loginStatus = post_result.getString("signup_status");
                        responded=true;
                        success=Boolean.valueOf(loginStatus);
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
     * Return if the mobile number is an Australian number.
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "^\\({0,1}((0|\\+61)(2|4|3|7|8)){0,1}\\){0,1}(\\ |-){0,1}[0-9]{2}(\\ |-){0,1}[0-9]{2}(\\ |-){0,1}[0-9]{1}(\\ |-){0,1}[0-9]{3}$";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * Return if the given email address is valid.
     * @param strMail
     * @return
     */
    public static boolean testMail(String strMail) {
        if(strMail.indexOf("@") == -1 || strMail.indexOf(".") == -1) {
            System.out.println("1");
            return false;
        }
        if(strMail.indexOf("@") != strMail.lastIndexOf("@") ){
            System.out.println("2");
            return false;
        }
        for(int i = 0; i<strMail.indexOf("@"); i++) {
            if(!((strMail.charAt(i) >= 'A' && strMail.charAt(i) <= 'Z') ||
                    (strMail.charAt(i)>=48 && strMail.charAt(i)<=57)||
                    (strMail.charAt(i)=='.' ||(strMail.charAt(i)=='_')) ||
                    (strMail.charAt(i) >= 'a' && strMail.charAt(i) <= 'z'))) {
                System.out.println("4");
                return false;
            }
        }
        return true;
    }


}
