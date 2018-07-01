package volunteer.sk.greate43.com.gotcha;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class memories_add_fragment extends DialogFragment implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {
    public static final String ALLOW_TO_CHECK_USER_TYPE = "ALLOW_TO_CHECK_USER_TYPE";
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 11;
    private static final int CAMERA_RESULT = 12;
    private static final int REQUEST_CAMERA_AND_WRITE_PERMISSION = 13;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 231;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    Memories memories;
    Boolean allowToCheckUserType;
    View viewSnackBar;
    private CircleImageView imgMemoriesPicture;
    private Uri imgUri;
    private ProgressDialog mProgressDialog;
    private View view;
    private int GALLERY_RESULT =14;

    static memories_add_fragment newInstance() {
        memories_add_fragment f=new memories_add_fragment();

        Bundle args = new Bundle();

        f.setArguments(args);
        return f;
        // Supply num input as an argument.
//        Bundle args = new Bundle();
//
//        f.setArguments(args);

    }

    private Location mLastLocation;
    private boolean mLocationUpdateState;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_CHECK_SETTINGS = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = 0;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        setStyle(DialogFragment.STYLE_NORMAL, theme);

    }

    public memories_add_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.memories_add_fragment, container, false);
        EditText editText = view.findViewById(R.id.etMemoryName);
        Button button = view.findViewById(R.id.button_save_memory);
        imgMemoriesPicture=view.findViewById(R.id.upload_memory_picture);
        if (mGoogleApiClient == null) {
            if (getActivity() != null)
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
        }
        createLocationRequest();

        imgMemoriesPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    selectPictureFromGalleryOrCameraDialog();
                }else{
                    Snackbar.make(viewSnackBar, "null exception ", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        button.setOnClickListener(v -> {
            String pushId = String.valueOf(user.getUid());

            String memories = editText.getText().toString();

            viewSnackBar = v ;

            if (mLastLocation != null && memories != null && imgUri != null) {

                saveMyMemory(pushId,mLastLocation,memories,imgUri,v);
                dismiss();
            }else if (imgUri == null){
                Snackbar.make(viewSnackBar, "Please Select the image ", Snackbar.LENGTH_SHORT).show();
            }else if (mLastLocation == null){
                Snackbar.make(viewSnackBar, "Not getting Location ", Snackbar.LENGTH_SHORT).show();
            }else if (memories == null){
                Snackbar.make(viewSnackBar, "Please Select the Memory Name ", Snackbar.LENGTH_SHORT).show();
            }

        });
        return view;
    }

    private void saveMyMemory(String pushId, Location mLastLocation, String memories, Uri imgUri, View v) {

        imgMemoriesPicture.setDrawingCacheEnabled(true);
        imgMemoriesPicture.buildDrawingCache();
        Bitmap bitmap = imgMemoriesPicture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference ref = storageRef.child(Constants.PHOTOS).child(pushId).child(pushId);

        UploadTask uploadTask = ref.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {


            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Memories mem = new Memories();
                mem.setMemoryName(memories);
                mem.setLat(mLastLocation.getLatitude());
                mem.setLon(mLastLocation.getLongitude());
                mem.setMemoryId(pushId);
                mem.setMemoryPicUrl(String.valueOf(downloadUri));
                mDatabaseReference.child(Constants.MEEMORIES).child(pushId).setValue(mem);



            }
        });
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

    private void setUpMap() {
        if (getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }


        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability != null && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

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
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_RESULT) {
            if (data != null) {
                imgUri = data.getData();
                setProfileImage(imgUri);
            }

        } else if (requestCode == CAMERA_RESULT) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            assert getActivity() != null;
//            String pathOfBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
//
//            imgUri = Uri.parse(pathOfBmp);
            setProfileImage(imgUri);
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
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: 1");
                    choosePhotoFromGallery();
                }
                break;
            case REQUEST_CAMERA_AND_WRITE_PERMISSION:

                if (grantResults.length > 0) {
                    Log.d(TAG, "onRequestPermissionsResult: 0");
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d(TAG, "onRequestPermissionsResult: 2 ");
                    if (cameraPermission && readExternalStorage) {
                        takePhotoFromCamera();
                    }
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
    private static final String TAG = "memories_add_fragment";

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void selectPictureFromGalleryOrCameraDialog() {
        assert getActivity() != null;
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallery();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        assert getActivity() != null;
        int HasReadPermission = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        if (HasReadPermission != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_RESULT);

    }

    private void takePhotoFromCamera() {
        assert getActivity() != null;
        int HasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int HasWriteExternalStoragePermPermission = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

        if (HasCameraPermission != PackageManager.PERMISSION_GRANTED || HasWriteExternalStoragePermPermission != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, REQUEST_CAMERA_AND_WRITE_PERMISSION);
            return;
        }

        try {


            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Profile Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                imgUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                imgUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
                Log.d(TAG, "takePhotoFromCamera: unMounted");
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip =
                        ClipData.newUri(getActivity().getContentResolver(), "", imgUri);

                intent.setClipData(clip);
                getActivity().grantUriPermission(getActivity().getPackageName(), imgUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                List<ResolveInfo> resInfoList =
                        getActivity().getPackageManager()
                                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, imgUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }


            startActivityForResult(intent, CAMERA_RESULT);
        } catch (Exception ex) {
            Snackbar.make(view, ex.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
    private void setProfileImage(Uri imgUri) {
        Picasso.with(getActivity())
                .load(imgUri)
                .fit()
                .centerCrop()
                .into(imgMemoriesPicture, new Callback() {
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