package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    ArrayList<Profile> mProfiles;
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;

    public ArrayList<Profile> getReminders() {
        return mProfiles;
    }

    public SearchAdapter(Activity activity) {
        mActivity = activity;
        layoutInflater = activity.getLayoutInflater();
        mProfiles = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_search, parent, false);

        return new SearchAdapter.ViewHolder(view);
    }

    private static final String TAG = "SearchAdapter";


    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        if (mProfiles == null || mProfiles.size() == 0) {
        } else {
            Profile profile = mProfiles.get(position);
            holder.email.setText(profile.getEmail());

            holder.name.setText(profile.getFirstName());
            holder.send.setOnClickListener(v -> {
                String pushId = mDatabaseReference.push().getKey();

                FriendList friendList = new FriendList();
                friendList.setFriendId(profile.getUserId());
                friendList.setPushId(pushId);
                friendList.setUserId(user.getUid());
                friendList.setRequestAlreadySent(true);
                friendList.setFriendRequestAccepted(false);


                mDatabaseReference.child(Constants.FRIEND_LIST).child(Objects.requireNonNull(pushId)).setValue(friendList);
                holder.send.setEnabled(false);
                holder.send.setText("Request Sent");


            });

            if (profile.getProfilePicUrl() != null && !profile.getProfilePicUrl().isEmpty()) {
                Picasso.with(mActivity)
                        .load(profile.getProfilePicUrl())
                        .fit()
                        .centerCrop()
                        .into(holder.pic, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }


        }

    }


    @Override
    public int getItemCount() {
        if (mProfiles != null
                && !mProfiles.isEmpty()

                ) {
            return mProfiles.size();
        } else {
            return 0;
        }
    }

    public void clear() {
        int size = mProfiles.size();
        if (size > 0) {
            mProfiles.clear();
            notifyItemRangeRemoved(0, size);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        CircleImageView pic;
        Button send;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_friend_name);
            email = itemView.findViewById(R.id.list_email);
            pic = itemView.findViewById(R.id.list_friend_profile_pic);
            send = itemView.findViewById(R.id.yes);

        }


    }


}
