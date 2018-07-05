package volunteer.sk.greate43.com.gotcha;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Objects;

public class memories_show_buttons extends DialogFragment  {

   static memories_show_buttons newInstance()
   {
       memories_show_buttons msb = new  memories_show_buttons();
       return msb;
   }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public memories_show_buttons(){}
    static int ID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.memories_show_button,container,false);
        Button show_mem=view.findViewById(R.id.btn_show_memories);
        Button show_loc=view.findViewById(R.id.btn_show_Location);


        //memory on map button
        show_mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });
        //locationn show button
        show_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


              /*  FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.my_view, Friend_location_map.newInstance())
                        .addToBackStack(null)
                        .commit();*/

                dismiss();
            }
        });

        return view;
    }



}
