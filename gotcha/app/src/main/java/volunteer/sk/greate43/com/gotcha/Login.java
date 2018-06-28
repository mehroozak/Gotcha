package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Aspire v5-573G on 12/14/2017.
 */

public class Login extends FragmentActivity {
    private static final int RC_SIGN_IN = 111;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        FirebaseApp.initializeApp(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        if (user != null) {
            // already signed in

            queryFirebaseProfile();


        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                           //         new AuthUI.IdpConfig.PhoneBuilder().build(),
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

    private void queryFirebaseProfile() {
        databaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            loadFragment();
        }
        if (dataSnapshot.getValue() != null) {

            collectProfile((Map<String, Object>) dataSnapshot.getValue());
        }
    }

    private void collectProfile(@NonNull Map<String, Object> value) {
        String firstName = null;
        if (value.get(Constants.firstName) != null)
            firstName = String.valueOf(value.get(Constants.firstName));

        if (firstName != null) {
            startActivity(new Intent(Login.this, home2.class));
            finish();
        } else {
            loadFragment();
        }

    }


    private void loadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.login_container, ProfileFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }



    private static final String TAG = "Login";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                user = mAuth.getCurrentUser();
                if (user != null) {
                    queryFirebaseProfile();
                }
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