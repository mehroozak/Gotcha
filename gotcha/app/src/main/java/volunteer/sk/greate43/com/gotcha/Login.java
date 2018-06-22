package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aspire v5-573G on 12/14/2017.
 */

public class Login extends Activity {
    private static final int RC_SIGN_IN = 111;
    Button signgo, login;
    EditText logname, logpass;
    String chek;
    networkController nc = new networkController();
    private static final String url = "http://172.20.10.3/ci/Users/validate/?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(Login.this, home2.class));
            finish();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()
                                    //    new AuthUI.IdpConfig.FacebookBuilder().build(),
                                    //  new AuthUI.IdpConfig.TwitterBuilder().build()
                            ))
                            .build(),
                    RC_SIGN_IN);
        }


//        //to go to Signup Screen
//        signgo = (Button) findViewById(R.id.signupgo);
//        signgo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Login.this, Signup.class);
//                startActivity(intent);
//            }
//        });


    }

    private static final String TAG = "Login";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(Login.this, home2.class));
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    // showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //  showSnackbar(R.string.no_internet_connection);
                    return;
                }

                // showSnackbar(R.string.unknown_error);
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

}