package volunteer.sk.greate43.com.gotcha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    private RecyclerView recyclerView;
    private FriendListAdapter adaptor;
    private ArrayList<FriendList> mFriendLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        recyclerView = findViewById(R.id.notification_recycler_view);

        adaptor = new FriendListAdapter(this, true);

        mFriendLists = adaptor.getFriendLists();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        queryDatabase();

    }

    private void queryDatabase() {
        mDatabaseReference.child(Constants.FRIEND_LIST).orderByChild(Constants.friendId).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
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

    private static final String TAG = "NotificationActivity";

    private void collectSearch(@NonNull Map<String, Object> value) {
        Log.d(TAG, "collectSearch: " + value);
        FriendList friendList = new FriendList();
        if (value.get(Constants.userId) != null)
            friendList.setUserId(String.valueOf(value.get(Constants.userId)));

        if (value.get(Constants.pushId) != null)
            friendList.setPushId(String.valueOf(value.get(Constants.pushId)));


        if (value.get(Constants.friendId) != null)
            friendList.setFriendId(String.valueOf(value.get(Constants.friendId)));


        if (value.get(Constants.isFriendRequestAccepted) != null)
            friendList.setFriendRequestAccepted(Boolean.parseBoolean(String.valueOf(value.get(Constants.isFriendRequestAccepted))));

        if (value.get(Constants.isRequestAlreadySent) != null)
            friendList.setRequestAlreadySent(Boolean.parseBoolean(String.valueOf(value.get(Constants.isRequestAlreadySent))));

        if (value.get(Constants.isFriendRequestRejected) != null)
            friendList.setFriendRequestRejected(Boolean.parseBoolean(String.valueOf(value.get(Constants.isFriendRequestRejected))));

        if (!friendList.isFriendRequestAccepted() && !friendList.isFriendRequestRejected()) {
            mFriendLists.add(friendList);
        }
    }


}
