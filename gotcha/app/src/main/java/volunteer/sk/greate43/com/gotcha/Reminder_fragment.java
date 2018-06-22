package volunteer.sk.greate43.com.gotcha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */

public class Reminder_fragment extends Fragment {
    private static final String Tag = "reminder_fragment";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        return view;
    }

    private void queryFirebaseProfile() {
        mDatabaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
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

            collectReminder((Map<String, Object>) dataSnapshot.getValue());
        }
    }

    private void collectReminder(@NonNull Map<String, Object> value) {
        String firstName = null;
        if (value.get(Constants.firstName) != null)
            firstName = String.valueOf(value.get(Constants.firstName));



    }



    public void addReminder(View view) {

    }

}

