package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dharma.shopinar.models.UserInfoModel;
import com.dharma.shopinar.networksync.CheckInternetConnection;
import com.dharma.shopinar.usersession.UserSession;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public static int PICK_IMAGE_REQUEST = 1;
    boolean isKitKat = false;
    private String TAG = "ProfileActivity";

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    private TextView tvemail, tvphone;

    private TextView namebutton;
    private CircleImageView profilemageView;
    private TextView updateDetails;
    private LinearLayout addressview;
//    private ProgressBar mProgressBar;

    CheckInternetConnection cic;

    //to get user session data
    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, photo, mobile;
    private SliderLayout sliderShow;

    private FirebaseAuth mAuth;

    FirebaseUser mUser;
    FirebaseFirestore mStore;

    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;
    private String mUserID;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // check Internet Connection
        checkConnection();

        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("users/profile_images/" + mUserID);
        mFirestore = FirebaseFirestore.getInstance();


        initialize();


        //retrieve session values and display on listviews
        getUserInformation();

        //ImageSLider
        inflateImageSlider();

    }

    private void initialize() {

        addressview = findViewById(R.id.addressview);
        profilemageView = findViewById(R.id.profilepic);
        tvemail = findViewById(R.id.emailview);
        tvphone = findViewById(R.id.mobileview);
        namebutton = findViewById(R.id.name_button);
        updateDetails = findViewById(R.id.updatedetails);
//        mProgressBar = findViewById(R.id.progress_bar);


        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    updateProfile();

//                startActivity(new Intent(ProfileActivity.this,UpdateData.class));
//                startActivity(new Intent(ProfileActivity.this,Wishlist.class));
//                finish();
            }
        });

        addressview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, Wishlist.class));
            }
        });

        profilemageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateProfile() {

        if (mImageUri != null) {

            final StorageReference fileReference
                    = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "then: " + downloadUri.toString());
                        Toast.makeText(ProfileActivity.this, "Update Successful: " , Toast.LENGTH_SHORT).show();

//                        session.setKeyProfile(downloadUri.toString());

                        DocumentReference docRefUser = mFirestore.collection("users").document(mUserID);
                        docRefUser.update("profile", downloadUri.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    } else {
                        Toast.makeText(ProfileActivity.this, "Update Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(ProfileActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
        }





//        if (mImageUri != null) {
//            StorageReference fileReference
//                    = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
//            fileReference.putFile(mImageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setProgress(0);
//
//                                }
//                            },5000);
//
//                            Toast.makeText(ProfileActivity.this,"Updated Successfully",Toast.LENGTH_SHORT).show();
//
//                            // Update new profile photo link in Firestore
//                            taskSnapshot.getr
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            mProgressBar.setProgress((int) progress);
//                        }
//                    });
//
//        } else {
//            Toast.makeText(ProfileActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
//        }



    }

    private void openFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            isKitKat = true;
            startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
        } else {
            isKitKat = false;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profilemageView);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

//    private void getValues() {
//
//        //create new session object by passing application context
//        session = new UserSession(getApplicationContext());
//
//        //validating session
//        session.isLoggedIn();
//
//        //get User details if logged in
//        user = session.getUserDetails();
//
//        name = user.get(UserSession.KEY_NAME);
//        email = user.get(UserSession.KEY_EMAIL);
//        mobile = user.get(UserSession.KEY_MOBiLE);
//        photo = user.get(UserSession.KEY_PROFILE);
//
//        //setting values
//        tvemail.setText(email);
//        tvphone.setText(mobile);
//        namebutton.setText(name);
//
//        Picasso.get().load(photo).into(profilemageView);
////        primage.setImageDrawable(R.drawable.default_profile);
//
//    }

    private void getUserInformation() {
        mStore = FirebaseFirestore.getInstance();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();

        DocumentReference docRef = mStore.collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserInfoModel userInfo = documentSnapshot.toObject(UserInfoModel.class);

                name = userInfo.getName();
                email = userInfo.getEmail();
                mobile = userInfo.getPhone();
                photo = userInfo.getProfile();


                //setting values
                tvemail.setText(email);
                tvphone.setText(mobile);
                namebutton.setText(name);

                Picasso.get().load(photo).into(profilemageView);

                Log.e(TAG, name
                        + " " + email
                        + " " + mobile
                        + " " + photo);
            }
        });
    }

    private void inflateImageSlider() {

        // Using Image Slider -----------------------------------------------------------------------
        sliderShow = findViewById(R.id.slider);

        //populating Image slider
        ArrayList<String> sliderImages = new ArrayList<>();
        sliderImages.add("https://rb.gy/w3jsoi");
        sliderImages.add("https://rb.gy/puovbr");
        sliderImages.add("https://rb.gy/jolflh");
        sliderImages.add("https://rb.gy/gjl9qq");

        for (String s : sliderImages) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.image(s);
            sliderShow.addSlider(sliderView);
        }

        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewCart(View view) {
//        startActivity(new Intent(ProfileActivity.this,Cart.class));
        startActivity(new Intent(ProfileActivity.this, Cart.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        checkConnection();
    }

    public void Notifications(View view) {
//        startActivity(new Intent(ProfileActivity.this,NotificationActivity.class));
        startActivity(new Intent(ProfileActivity.this, NotificationActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();

    }

    void checkConnection() {
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        CheckInternetConnection cic;
        cic = new CheckInternetConnection();
        this.cic = cic;
        registerReceiver(cic, in);
    }

}