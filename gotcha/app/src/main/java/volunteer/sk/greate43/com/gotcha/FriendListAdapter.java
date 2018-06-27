package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    private final LayoutInflater layoutInflater;
    ArrayList<FriendList> mFriendLists;

    public ArrayList<FriendList> getFriendLists() {
        return mFriendLists;
    }

    public FriendListAdapter(Activity activity) {
        mActivity = activity;
        layoutInflater = activity.getLayoutInflater();
        mFriendLists = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_friend_list, parent, false);

        return new FriendListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        if (mFriendLists == null || mFriendLists.size() == 0) {

        } else {
            FriendList list = mFriendLists.get(position);
            mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.userId).equalTo(list.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    showData(dataSnapshot);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

            ViewHolder.no.setOnClickListener(v -> {

                mDatabaseReference.child(Constants.FRIEND_LIST).child(list.getPushId()).updateChildren(update(list,false,true));

            });

            ViewHolder.no.setOnClickListener(v -> {
                mDatabaseReference.child(Constants.FRIEND_LIST).child(list.getPushId()).updateChildren(update(list,true,false));
            });
        }
    }

    private Map<String, Object> update(FriendList list, Boolean isAccepted, Boolean isRejected) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.isFriendRequestAccepted, isAccepted);
        hashMap.put(Constants.isFriendRequestRejected, isAccepted);

        return hashMap;
    }

    private void showData(DataSnapshot dataSnapshot) {

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectProfile((Map<String, Object>) ds.getValue());
            }
        }

    }

    private void collectProfile(Map<String, Object> value) {
        Log.d(TAG, "collectProfile: " + value);

        String firstName = null;
        if (value.get(Constants.firstName) != null)
            firstName = String.valueOf(value.get(Constants.firstName));
        String lastName = null;
        if (value.get(Constants.lastName) != null)
            lastName = String.valueOf(value.get(Constants.lastName));

        String email = null;
        if (value.get(Constants.email) != null)
            email = String.valueOf(value.get(Constants.email));


        Log.d(TAG, "collectProfile: " + email);
        String profileUrl = null;
        if (value.get(Constants.profilePicUrl) != null)
            profileUrl = String.valueOf(value.get(Constants.profilePicUrl));

        ViewHolder.email.setText(email);
        ViewHolder.name.setText(firstName);

        setProfileImage(Uri.parse(profileUrl));

    }

    private void setProfileImage(Uri imgUri) {
        Picasso.with(mActivity)
                .load(imgUri)
                .fit()
                .centerCrop()
                .into(ViewHolder.pic, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


    private static final String TAG = "FriendListAdapter";

    @Override
    public int getItemCount() {
        if (mFriendLists != null
                && !mFriendLists.isEmpty()

                ) {
            return mFriendLists.size();
        } else {
            return 0;
        }
    }

    public void clear() {
        int size = mFriendLists.size();
        if (size > 0) {
            mFriendLists.clear();
            notifyItemRangeRemoved(0, size);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        static TextView name;
        static TextView email;
        static CircleImageView pic;
        static Button yes;
        static Button no;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_friend_name);
            email = itemView.findViewById(R.id.list_friend_email);
            pic = itemView.findViewById(R.id.list_friend_profile_pic);
            yes = itemView.findViewById(R.id.yes);
            no = itemView.findViewById(R.id.no);


        }
    }


}