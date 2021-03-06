package volunteer.sk.greate43.com.gotcha;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */

public class Map_fragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {
    private static final String TAG = "Map_fragment";
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 111;



    public Map_fragment() {
    }

   static FirebaseAuth mAuth;
   static FirebaseUser user;
   static DatabaseReference mDatabaseReference;
   static FirebaseDatabase database;
   static FirebaseStorage mStorage;
  static   StorageReference storageRef;
    private static GoogleMap mMap;
    private Location mLastLocation;
    private boolean mLocationUpdateState;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_CHECK_SETTINGS = 2;
    double latitude,longitude;
    Profile profile;
    static int ID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.home_fragment, container, false);
        ID=container.getId();

       // Button add_memories=(Button)myview.findViewById(R.id.add_memories_btn);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();

        if (mGoogleApiClient == null) {
            if (getActivity() != null)
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
        }
        createLocationRequest();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        detectMyFriend();
        return myview;
    }

    private static void detectMyFriend() {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();

        if (friendId!=null) {
            mDatabaseReference.child(Constants.LIVE_LOCATION_UPDATES).orderByChild(Constants.userId).equalTo(friendId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {

                                collectProfile((Map<String, Object>) dataSnapshot.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
        }

    }

    private static void collectProfile(@NonNull Map<String, Object> value) {
        double Latitude = 0;
        if (value.get(Constants.lat) != null)
            Latitude = Double.parseDouble(String.valueOf(value.get(Constants.lat)));
        double Longitude = 0;
        if (value.get(Constants.lon) != null)
            Latitude = Double.parseDouble(String.valueOf(value.get(Constants.lon)));


//            Map_fragment mp=new Map_fragment();
//            mp.Friend_location_Marker(Latitude,Longitude,Name);
        if (mMap!=null) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).title("dd"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupGoogleMapScreenSettings(googleMap);
        //positionCamera(mMap);

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

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.d(TAG, "onLocationChanged: " + location.getLatitude());
        latitude=mLastLocation.getLatitude();
        longitude=mLastLocation.getLongitude();

        LatLng currentLocation = new LatLng(latitude,longitude);

        positionCamera(mMap,currentLocation);
        updateServerLocation(user.getUid(),mLastLocation.getLatitude(),mLastLocation.getLongitude());
    }

    private void updateServerLocation(String uid, double latitude, double longitude) {
       if(uid!=null) {
//           profile = new Profile();
//           profile.setUserId(uid);
//           profile.setLat(latitude);
//           profile.setLon(longitude);
           mDatabaseReference.child(Constants.LIVE_LOCATION_UPDATES).child(uid).updateChildren(uplocation(uid,latitude,longitude));
       }
    }

    private Map<String, Object> uplocation(String uid, double latitude, double longitude) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Constants.userId,uid);
        hashMap.put(Constants.lat,latitude);
        hashMap.put(Constants.lon,longitude);
        return  hashMap;

    }

    private void positionCamera(GoogleMap mMap, LatLng currentLocation) {
        try {
            if (currentLocation != null)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16));
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        if (mLocationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void setUpMap() {
        if (getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability != null && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                positionCamera(mMap,currentLocation);

            }
        }
    }

    protected void startLocationUpdates() {
        if (getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        // 2
        mLocationRequest.setInterval(10000);
        // 3
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    mLocationUpdateState = true;
                    startLocationUpdates();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocationUpdateState = true;
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: ");
                    setUpMap();

                }
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mLocationUpdateState) {
            startLocationUpdates();
        }
    }

   static String friendId = null;
    public static void Friend_location_Marker(String friendId_for_locatoion) {
         friendId = friendId_for_locatoion;

         detectMyFriend();
    }
}
