package volunteer.sk.greate43.com.gotcha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.zip.InflaterInputStream;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */

public class gallery_fragment extends Fragment {
    private static final String Tag ="gallery_fragment";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    private RecyclerView recyclerView;
    private FriendListAdapter adaptor;
    private ArrayList<FriendList> mFriendLists;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.gallery_fragment,container,false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        recyclerView = view.findViewById(R.id.gallery_recycler_view);

        adaptor = new FriendListAdapter(Objects.requireNonNull(getActivity()),false);

        mFriendLists = adaptor.getFriendLists();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());





        queryDatabase();

        return view;
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

        if (friendList.isFriendRequestAccepted() && !friendList.isFriendRequestRejected()) {
            mFriendLists.add(friendList);
        }
    }

}

