package volunteer.sk.greate43.com.gotcha;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */

public class profile_fragment extends Fragment {
    private static final String Tag = "profle_fragment";
    Button logout;
    Intent intent;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView mImg;
    private AppCompatAutoCompleteTextView mFirstName;
    private AppCompatAutoCompleteTextView mLastName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        logout = (Button) view.findViewById(R.id.logout);
        mFirstName = view.findViewById(R.id.firstName);
        mLastName = view.findViewById(R.id.lastName);
        mImg = view.findViewById(R.id.profile_pic);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity() != null) {
                    intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if (auth.getCurrentUser() != null) {
                        auth.signOut();
                    }
                    getActivity().finish();
                }
            }
        });

        queryFirebaseProfile();
        return view;
    }

    private static final String TAG = "profile_fragment";
    private void setProfileImage(Uri imgUri) {
        Picasso.with(getActivity())
                .load(imgUri)
                .fit()
                .centerCrop()
                .into(mImg, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onError() {

                    }
                });
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
        }
        if (dataSnapshot.getValue() != null) {

            collectProfile((Map<String, Object>) dataSnapshot.getValue());
        }
    }

    private void collectProfile(@NonNull Map<String, Object> value) {
        String firstName = null;
        if (value.get(Constants.firstName) != null)
            firstName = String.valueOf(value.get(Constants.firstName));
        String lastName = null;
        if (value.get(Constants.lastName) != null)
            lastName = String.valueOf(value.get(Constants.lastName));
        String profileUrl = null;
        if (value.get(Constants.profilePicUrl) != null)
            profileUrl = String.valueOf(value.get(Constants.profilePicUrl));

        mFirstName.setText(firstName );
        mLastName.setText( lastName);

        setProfileImage(Uri.parse(profileUrl));

    }
}

