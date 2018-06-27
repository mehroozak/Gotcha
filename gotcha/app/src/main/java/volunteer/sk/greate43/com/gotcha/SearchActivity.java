package volunteer.sk.greate43.com.gotcha;

import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    private RecyclerView recyclerView;
    private SearchAdapter adaptor;
    private ArrayList<Profile> mProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        recyclerView = findViewById(R.id.serach_recycler_view);

        adaptor = new SearchAdapter(this);

        mProfiles = adaptor.getReminders();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private static final String TAG = "SearchActivity";

    private void doMySearch(String query) {
        Log.d(TAG, "doMySearch: " + query);
        mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.email).startAt(query).endAt(query + Constants.MAX_UNI_CODE_LIMIT).addValueEventListener(new ValueEventListener() {
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
        if (adaptor != null) {
            adaptor.clear();
        }
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectSearch((Map<String, Object>) ds.getValue());
            }
        }

        adaptor.notifyDataSetChanged();
    }

    private void collectSearch(@NonNull Map<String, Object> value) {
        Log.d(TAG, "collectSearch: " + value);
        Profile profile = new Profile();
        if (value.get(Constants.firstName) != null)
            profile.setFirstName(String.valueOf(value.get(Constants.firstName)));

        if (value.get(Constants.lastName) != null)
            profile.setLastName(String.valueOf(value.get(Constants.lastName)));


        if (value.get(Constants.email) != null)
            profile.setEmail(String.valueOf(value.get(Constants.email)));


        if (value.get(Constants.profilePicUrl) != null)
            profile.setProfilePicUrl(String.valueOf(value.get(Constants.profilePicUrl)));

        if (value.get(Constants.userId) != null)
            profile.setUserId(String.valueOf(value.get(Constants.userId)));

        if (!profile.getUserId().equals(user.getUid())) {
            mProfiles.add(profile);
        }
    }

}


