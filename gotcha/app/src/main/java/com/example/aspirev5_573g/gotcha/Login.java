package com.example.aspirev5_573g.gotcha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aspire v5-573G on 12/14/2017.
 */

public class Login extends Activity {
    Button signgo, login;
    EditText logname, logpass;
    String chek;
    networkController nc= new networkController();
    private static final String url = "http://172.20.10.3/ci/Users/validate/?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        //to go to Signup Screen
        signgo = (Button) findViewById(R.id.signupgo);
        signgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });
        //to login and go to Home
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final EditText mail = (EditText) findViewById(R.id.logemail);
               final EditText pass = (EditText) findViewById(R.id.logpass);

                final String email = mail.getText().toString().trim();
                final String password = pass.getText().toString().trim();
                if (email==null || password==null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login.this,"Feilds Required",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                final HashMap<String,String> params=new HashMap<>();
                params.put("email",email);
                params.put("password",password);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        chek = nc.performPostCall(url,params);
                        if(Integer.parseInt(chek) != 404 && Integer.parseInt(chek) > 0){
                            Intent intent = new Intent(Login.this, home2.class);
                            startActivity(intent);

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Login.this,"Invalid Username or password",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        Log.i("res",chek);
                    }
                });
                thread.start();
             }
            }
        });
    }
}