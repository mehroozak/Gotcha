package volunteer.sk.greate43.com.gotcha;

import android.support.v4.app.DialogFragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Marker;

public class Friend_location_map extends Fragment implements  OnMapReadyCallback {

     GoogleMap mMap;
    private Location mLastLocation;
    private boolean mLocationUpdateState;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_CHECK_SETTINGS = 2;


    public Friend_location_map(){

    }
    static Friend_location_map newInstance(){
        Friend_location_map FLM=new Friend_location_map();
        Bundle args = new Bundle();

        FLM.setArguments(args);
        return FLM;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("laoded friend location map");
        View myview = inflater.inflate(R.layout.home_fragment, null, false);

        if(mMap!=null) {
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);


                mapFragment.getMapAsync(this);
        }
        return myview;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        setupGoogleMapScreenSettings(googleMap);
    }
    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }
}
