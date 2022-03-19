package com.dharma.shopinar;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.dharma.shopinar.models.UserInfoModel;
import com.dharma.shopinar.networksync.CheckInternetConnection;
import com.dharma.shopinar.usersession.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;


import android.app.AlertDialog;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LogInActivity";
    private static final int RC_SIGN_IN = 9001;
    public static final String USERID = "userID";
    FirebaseUser user;
    FirebaseFirestore mStore;
    String userId;
    CheckInternetConnection cic;
    private EditText edtemail, edtpass;
    private String email, password, sessionphone;
    private TextView appname, forgotpass, registernow;
    private ImageView create_account;
    private UserSession session;
    private int cartcount, wishlistcount;
    // Initialize Firebase Auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean isVerified , isGotInfo;

    UserInfoModel userInfoModel;
    Button mloginButton;
    ProgressBar loginProgressBar;
    //Getting reference to Firebase Database
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference mDatabaseReference = database.getReference();

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //it goes to background.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Log.e(TAG, "LoginActivity started");


        loginProgressBar = findViewById(R.id.loginProgress);
        loginProgressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        edtemail = findViewById(R.id.editTextEmail);
        edtpass = findViewById(R.id.editTextPassword);

        isVerified = false;
        isGotInfo =false;

        Bundle registerinfo = getIntent().getExtras();
        if (registerinfo != null) {
            edtemail.setText(registerinfo.getString("email"));
        }

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        session = new UserSession(getApplicationContext());
        if (session.isLoggedIn()) {

            Log.e(TAG, UserSession.name
                    + " " + UserSession.phone
                    + " " + UserSession.email
                    + " " + UserSession.photo);
            userId = session.getUserID();
            Intent loginSuccess = new Intent(LoginActivity.this, MainActivity.class);
            loginSuccess.putExtra(USERID,userId);
            startActivity(loginSuccess);
            finish();
        }

//
//        requestQueue = Volley.newRequestQueue(LoginActivity.this);//Creating the RequestQueue

        //if user wants to register
        registernow = findViewById(R.id.register_now);
        registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        create_account = findViewById(R.id.create_account);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //if user forgets password
        forgotpass = findViewById(R.id.forgot_pass);
        forgotpass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });


        //Validating login details
        mloginButton = findViewById(R.id.login_button);

        mloginButton
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            email = edtemail.getText().toString();
                                            password = edtpass.getText().toString();

                                            mloginButton.setVisibility(View.GONE);
                                            loginProgressBar.setVisibility(View.VISIBLE);

                                            // check Internet Connection
                                            checkConnection();

                                            if (validateUsername(email) && validatePassword(password)) {
                                                // Email and Password Validation
                                                 verifyEmailPassword(email, password);
                                                Log.e(TAG,"Login button CLicked");


                                            } else {
                                                mloginButton.setVisibility(View.VISIBLE);
                                                loginProgressBar.setVisibility(View.GONE);

                                                Toast.makeText(LoginActivity.this, "Error ! Verification Failed", Toast.LENGTH_SHORT).show();
                                            }

                                        }



                                    }
                );


/*
        //Validating login details
                if (validateUsername(email) && validatePassword(pass)) { //Username and Password Validation

                    //Progress Bar while connection establishes

                    final KProgressHUD progressDialog=  KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();
*/
    }


    private void verifyEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "Email and Password Verified");
                    // Getting Users Information
                    getUserInformation();
                    isVerified = true;
                } else {
                    Log.e(TAG, "Email and Password Verification Failed");
                    mloginButton.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.GONE);

                    isVerified = false;
                    Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getUserInformation() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        boolean gotInfo;

        DocumentReference docRef = mStore.collection("users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserInfoModel userInfo = documentSnapshot.toObject(UserInfoModel.class);

                String sessionUserID = userInfo.getUserID();
                String sessionName = userInfo.getName();
                String sessionPhone = userInfo.getPhone();
                String sessionEmail = userInfo.getEmail();
                String sessionProfile = userInfo.getProfile();

                // Create shared preference and store data
                session.createLoginSession(sessionUserID,sessionName, sessionEmail, sessionPhone, sessionProfile);
//                UserSession.name = userInfo.getName();
//                UserSession.phone = userInfo.getPhone();
//                UserSession.email = userInfo.getEmail();
//                UserSession.photo = userInfo.getProfile();



                Log.e(TAG, sessionUserID
                        + " " + sessionName
                        + " " + sessionPhone
                        + " " + sessionEmail
                        + " " + sessionProfile);

                isGotInfo = true;


                if(isVerified && isGotInfo)
                {

                    Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    Intent loginSuccess = new Intent(LoginActivity.this, MainActivity.class);
                    loginSuccess.putExtra(USERID,userId);
                    startActivity(loginSuccess);
                    finish();
                }
            }
        });

        // Count value of firebase cart and wishlist
        // countFirebaseValues();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Login CheckPoint", "LoginActivity stopped");
    }



    private boolean validatePassword(String password) {
        if (password.length() < 4 || password.length() > 20) {
            edtpass.setError("Password Must consist of 4 to 20 characters");
            return false;
        }
        return true;
    }

    private boolean validateUsername(String email) {
        if (email.length() < 4 || email.length() > 30) {
            edtemail.setError("Email Must consist of 4 to 30 characters");
            return false;
        } else if (!email.matches("^[A-Za-z0-9.@]+")) {
            edtemail.setError("Only . and @ characters allowed");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            edtemail.setError("Email must contain @ and .");
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "LoginActivity resumed");
        checkConnection();
    }

    protected void onDestroy() {
//        unregisterReceiver(cic);
        super.onDestroy();
    }


    void checkConnection() {
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        CheckInternetConnection cic;
        cic = new CheckInternetConnection();
        this.cic = cic;
        registerReceiver(cic, in);
    }

}