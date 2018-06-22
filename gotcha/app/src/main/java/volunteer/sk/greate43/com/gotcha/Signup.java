package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aspire v5-573G on 12/14/2017.
 */

public class Signup extends Activity {
    networkController nc=new networkController();
    Button signup;
    String chek;

    //private static final String url="http://10.0.2.2:80/gotchadb/signup.php?";
    private static final String url="http://172.20.10.10/ci/Users/add/?";

    private EditText uname;
    private EditText lname;
    private EditText mail;
    private EditText pas1;
    private EditText pas2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uname = (EditText) findViewById(R.id.fname);
                lname = (EditText) findViewById(R.id.lname);
                mail = (EditText) findViewById(R.id.email);
                pas1 = (EditText) findViewById(R.id.pass1);
                pas2 = (EditText) findViewById(R.id.pass2);

                final String firstname = uname.getText().toString();
                final String lastname = lname.getText().toString();
                final String email = mail.getText().toString();
                final String password = pas1.getText().toString();
                final String repassword =pas2.getText().toString();

                if (!password.equals(repassword)) {
                    Toast msg = (Toast.makeText(Signup.this, "Password Doesn't Match", Toast.LENGTH_SHORT));
                    msg.show();
                } else {
                    final HashMap<String,String> params=new HashMap<>();
                    params.put("firstName",firstname);
                    params.put("lastName",lastname);
                    params.put("password",password);
                    params.put("email",email);

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                           chek = nc.performPostCall(url,params);
                           if(Integer.parseInt(chek) >= 0 ){
                               Intent intent = new Intent(Signup.this, Login.class);
                               startActivity(intent);
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(Signup.this,"user Regitered",Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }else{
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(Signup.this,"User not Registered",Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }

                           Log.i("response",chek);
                        }
                    });
                    thread.start();

                }
            }
        });
    }

}

