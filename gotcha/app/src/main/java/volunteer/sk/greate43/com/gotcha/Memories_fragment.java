package volunteer.sk.greate43.com.gotcha;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.zip.Inflater;

public class Memories_fragment extends android.support.v4.app.Fragment {

    private static final String Tag = "memories_fragment";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    private RecyclerView recyclerView;
    MemoriesAdapter mMemoriesAdapter;
    private MemoriesAdapter adaptor;
    private ArrayList<Memories> mMemories;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.memories_fragment,container,false);

        Button add=(Button)view.findViewById(R.id.addMemories);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        recyclerView = view.findViewById(R.id.Memories_recycler_view);

        adaptor = new MemoriesAdapter(Objects.requireNonNull(getActivity()));

        mMemories = adaptor.getMemories();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        add.setOnClickListener(v -> {
            showDialog();

        });
        queryFirebaseReminder();

        return view;

    }

    private void queryFirebaseReminder() {
        mDatabaseReference.child(Constants.MEEMORIES).addValueEventListener(new ValueEventListener() {
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
                collectMemory((Map<String, Object>) ds.getValue());
            }
        }

        adaptor.notifyDataSetChanged();
    }
    private void collectMemory(@NonNull Map<String, Object> value) {
        Memories memories = new Memories();
        String memoryName = null;
        if (value.get(Constants.reminderName) != null)
            memoryName = String.valueOf(value.get(Constants.memoriesName));

        memories.setMemoryName(memoryName);


        mMemories.add(memories);
    }
    void showDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        android.support.v4.app.Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = Reminder_add_fragment.newInstance();
        newFragment.show(ft, "dialog");
    }
}
