package volunteer.sk.greate43.com.gotcha;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

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


public class ProfileFragment extends Fragment {
    public static final String ALLOW_TO_CHECK_USER_TYPE = "ALLOW_TO_CHECK_USER_TYPE";
    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 11;
    private static final int CAMERA_RESULT = 12;
    private static final int REQUEST_CAMERA_AND_WRITE_PERMISSION = 13;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    Profile profile;
    Boolean allowToCheckUserType;
    View viewSnackBar;
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private CircleImageView imgProfilePicture;
    private Spinner spinnerUserType;
    private Button btnSaveProfile;
    private Uri imgUri;
    private ProgressDialog mProgressDialog;
    private View view;
    private int GALLERY_RESULT =14;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        etFirstName = view.findViewById(R.id.fragment_profile_edit_text_first_name);
        etLastName = view.findViewById(R.id.fragment_profile_edit_text_last_name);
        etEmail = view.findViewById(R.id.fragment_profile_edit_text_email_address);
        imgProfilePicture = view.findViewById(R.id.fragment_profile_image_view_profile_picture);
        btnSaveProfile = view.findViewById(R.id.fragment_profile_button_save_profile);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPictureFromGalleryOrCameraDialog();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSnackBar = v;
                if (
                        !TextUtils.isEmpty(etFirstName.getText())
                                && !TextUtils.isEmpty(etLastName.getText())
                                && imgUri != null
                                && isEmailValid(etEmail.getText().toString())
                        ) {

                    saveUserProfile(user.getUid()
                            , etFirstName.getText().toString()
                            , etLastName.getText().toString()
                            , imgUri

                            , etEmail.getText().toString()
                            , v
                    );


                } else if (imgUri == null) {
                    Snackbar.make(viewSnackBar, "Please Select the image ", Snackbar.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(etFirstName.getText())) {
                    etFirstName.setError("First Name is Empty ");
                } else if (TextUtils.isEmpty(etLastName.getText())) {
                    etLastName.setError("Last Name is Empty ");
                } else if (!isEmailValid(etEmail.getText().toString())) {
                    etEmail.setError("Email Address Should Contain @ , . and com");
                }
            }
        });

        if (profile != null) {
            imgUri = Uri.parse(profile.getProfilePicUrl());
            etFirstName.setText(profile.getFirstName());
            etLastName.setText(profile.getLastName());

            if (profile.getEmail() != null && !TextUtils.isEmpty(profile.getEmail())) {
                etEmail.setText(profile.getEmail());
            } else {
                etEmail.setText("");
            }


            if (imgUri != null)
                setProfileImage(imgUri);


        }

        return view;
    }
    @NonNull
    private Boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            return true;
        } else if (email.contains("@") && email.contains(".") && email.contains("com")) {
            return true;
        }
        return false;
    }

    private void saveUserProfile(final String userId, final String firstName, final String lastName, Uri imgUri, final String email, final View view) {
        showProgressDialog();

        imgProfilePicture.setDrawingCacheEnabled(true);
        imgProfilePicture.buildDrawingCache();
        Bitmap bitmap = imgProfilePicture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference ref = storageRef.child(Constants.PHOTOS).child(userId).child(userId);

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
                profile = new Profile();
                profile.setUserId(userId);
                profile.setFirstName(firstName);
                profile.setLastName(lastName);
                profile.setProfilePicUrl(String.valueOf(downloadUri));
                if (!email.isEmpty())
                    profile.setEmail(email);

                mDatabaseReference.child(Constants.PROFILE).child(userId).setValue(profile);
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), home2.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();

                }


                hideProgressDialog();


            } else {
                // Handle failures
                profile = new Profile();
                profile.setUserId(userId);
                profile.setFirstName(firstName);
                profile.setLastName(lastName);
                profile.setProfilePicUrl("");

                mDatabaseReference.child(Constants.PROFILE).child(userId).setValue(profile);


//                if (allowToCheckUserType) {
//                    if (profile.getUserType().equalsIgnoreCase(Constants.TYPE_SELLER)) {
//                        if (getActivity() != null) {
//                            Intent intent = new Intent(getActivity(), SellerActivity.class);
//                            getActivity().startActivity(intent);
//                            getActivity().finish();
//                        }
//
//                    } else if (profile.getUserType().equalsIgnoreCase(Constants.TYPE_BUYER)) {
//                        if (getActivity() != null) {
//                            Intent intent = new Intent(getActivity(), BuyerActivity.class);
//                            getActivity().startActivity(intent);
//                            getActivity().finish();
//                        }
//                    }
//                } else {
//                    Snackbar.make(view, "Profile was Successfully Updated But Image wasn't uploaded", Snackbar.LENGTH_LONG).show();
//                }


                hideProgressDialog();
            }
        });

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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


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


    private void setProfileImage(Uri imgUri) {
        Picasso.with(getActivity())
                .load(imgUri)
                .fit()
                .centerCrop()
                .into(imgProfilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
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


}
