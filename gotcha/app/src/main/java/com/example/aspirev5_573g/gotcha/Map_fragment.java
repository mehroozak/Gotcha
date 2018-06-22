package com.example.aspirev5_573g.gotcha;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.
        List;
import java.util.zip.Inflater;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.*;
public class Map_fragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Map_fragment";
    public Map_fragment() {
    }

    GoogleMap mMap;
    SupportMapFragment smf;
    MapView mymapView;
    View myview;
    protected LocationManager locationManager;
LocationServices mFusedLocationClient;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview = inflater.inflate(R.layout.home_fragment, container, false);
        /*mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
*/        return myview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mymapView = (MapView) myview.findViewById(R.id.map);
        if (mymapView != null) {
            mymapView.onCreate(null);
            mymapView.onResume();
            mymapView.getMapAsync(this);
        } else {
            smf = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            smf.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // request runtime permission
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
         mMap.setMyLocationEnabled(true);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener mLocListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1000, mLocListener);
        showCurrentLocation();
        //MarkerOptions options=new MarkerOptions().position(new LatLng(lat,lng));

        //gotolocationxoom(31.418715, 73.079109,15);
       /* googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(31.418715, 73.079109)).title("clock tower Fsd").snippet("hello from the other side"));
        CameraPosition campo= CameraPosition.builder().target(new LatLng(31.418715, 73.079109)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(campo));*/
    }

    public void gotolocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mMap.moveCamera(update);
    }

    public void gotolocationxoom(double lat, double lng,float xoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, xoom);
        mMap.moveCamera(update);
    }

    protected void showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            Log.d(TAG, "showCurrentLocation: "+location.getLatitude());
            gotolocationxoom(location.getLatitude(),location.getLongitude(),16);
            MarkerOptions options=new MarkerOptions().title("hhjghj").position(new LatLng(location.getLatitude(),location.getLongitude())).snippet("hello there");
            mMap.addMarker(options);


            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
        }else{
            Toast.makeText(getActivity(),"Faaack",Toast.LENGTH_LONG);
        }


    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            mMap.clear();
            MarkerOptions options=new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()));
            mMap.addMarker(options);
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(getActivity(), "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(getActivity(),
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(getActivity(),
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }
}
