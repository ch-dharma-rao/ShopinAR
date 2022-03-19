package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dharma.shopinar.models.UserInfoModel;
import com.dharma.shopinar.networksync.CheckInternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    private EditText edtname, edtemail, edtpass, edtcnfpass, edtnumber;
    private String check, name, email, password, phone, profile;
//    CircleImageView image;
    ImageView upload;
//    RequestQueue requestQueue;
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;
    public static final String TAG = "MyTag";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    CheckInternetConnection cic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Check Internet Connection
//        new CheckInternetConnection(this).checkConnection();

//        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
//        TextView appname = findViewById(R.id.appname);
//        appname.setTypeface(typeface);

        // upload=findViewById(R.id.uploadpic);
        //image=findViewById(R.id.profilepic);
        edtname = findViewById(R.id.name);
        edtemail = findViewById(R.id.email);
        edtpass = findViewById(R.id.password);
        edtcnfpass = findViewById(R.id.confirmpassword);
        edtnumber = findViewById(R.id.number);

        edtname.addTextChangedListener(nameWatcher);
        edtemail.addTextChangedListener(emailWatcher);
        edtpass.addTextChangedListener(passWatcher);
        edtcnfpass.addTextChangedListener(cnfpassWatcher);
        edtnumber.addTextChangedListener(numberWatcher);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//            finish();
//        }


//        requestQueue = Volley.newRequestQueue(RegisterActivity.this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Validating login details
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkConnection();

                //TODO AFTER VALDATION  validateProfile() &&
                if (validateName() && validateEmail() && validateNumber() && validatePass() && validateCnfPass() ) {

                    name = edtname.getText().toString();
                    email = edtemail.getText().toString();
                    password = edtcnfpass.getText().toString();
                    phone = edtnumber.getText().toString();
                    profile = "https://rb.gy/r3pcsn";



                    // register the user in firebase

                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                // send verification link

                                FirebaseUser fuser = fAuth.getCurrentUser();
                                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                    }
                                });

                                Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();

                                UserInfoModel userInfo = new UserInfoModel(
                                        userID,
                                        name,
                                        email,
                                        phone,
                                        profile);

                                DocumentReference docRefUser = fStore.collection("users").document(userID);
                                docRefUser.set(userInfo);


//                                Map<String,Object> user = new HashMap<>();
//                                user.put("name",name);
//                                user.put("email",email);
//                                user.put("phone",phone);
//                                user.put("cart items",0);
//                                user.put("wishlist items",0);
//                                user.put("photo url","https://rb.gy/r3pcsn");
//                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d(TAG, "onFailure: " + e.toString());
//                                    }
//                                });

                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                            }else {
                                Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }

                /*
                Intent loginSuccess = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(loginSuccess);
                finish();

                */
            }
        });

/*
        //validate user details and register user

        Button button=findViewById(R.id.registerButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO AFTER VALDATION
                if (validateProfile() && validateName() && validateEmail() && validatePass() && validateCnfPass() && validateNumber()){

                    name=edtname.getText().toString();
                    email=edtemail.getText().toString();
                    password=edtcnfpass.getText().toString();
                    mobile=edtnumber.getText().toString();


                    final KProgressHUD progressDialog=  KProgressHUD.create(RegisterActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();


                    //Validation Success
                    convertBitmapToString(profilePicture);
                    RegisterRequest registerRequest = new RegisterRequest(name, password, mobile, email, profile, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            Log.e("Rsponse from server", response);

                            try {
                                if (new JSONObject(response).getBoolean("success")) {

                                    Toasty.success(RegisterActivity.this,"Registered Succesfully", Toast.LENGTH_SHORT,true).show();

                                    sendRegistrationEmail(name,email);


                                } else
                                    Toasty.error(RegisterActivity.this,"User Already Exist",Toast.LENGTH_SHORT,true).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toasty.error(RegisterActivity.this,"Failed to Register",Toast.LENGTH_LONG,true).show();
                            }
                        }
                    });
                    requestQueue.add(registerRequest);
                }
            }
        });
*/

        //Take already registered user to login page

        final TextView loginuser = findViewById(R.id.login_now);
        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Take already registered user back to login page

        final ImageView back_to_login = findViewById(R.id.back_to_login);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //take user to reset password
/*
        final TextView forgotpass=findViewById(R.id.forgot_pass);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,ForgotPassword.class));
                finish();
            }
        });

*/

/*
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Dexter.withActivity(RegisterActivity.this)
                        .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    // do you work now
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 1000);
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // permission is denied permenantly, navigate user to app settings
                                    Snackbar.make(view, "Kindly grant Required Permission", Snackbar.LENGTH_LONG)
                                            .setAction("Allow", null).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();



                //result will be available in onActivityResult which is overridden
            }
        });*/
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
//
//    private void sendRegistrationEmail(final String name, final String emails) {
//
//
//        BackgroundMail.newBuilder(RegisterActivity.this)
//                .withSendingMessage("Sending Welcome Greetings to Your Email !")
//                .withSendingMessageSuccess("Kindly Check Your Email now !")
//                .withSendingMessageError("Failed to send password ! Try Again !")
//                .withUsername("beingdevofficial@gmail.com")
//                .withPassword("Singh@30")
//                .withMailto(emails)
//                .withType(BackgroundMail.TYPE_PLAIN)
//                .withSubject("Greetings from Magic Print")
//                .withBody("Hello Mr/Miss, " + name + "\n " + getString(R.string.registermail1))
//                .send();
//
//    }
//
//    private void convertBitmapToString(Bitmap profilePicture) {
//
////                Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
////                so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        profilePicture.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//        byte[] array = byteArrayOutputStream.toByteArray();
//        profile = Base64.encodeToString(array, Base64.DEFAULT);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
//            //Image Successfully Selected
//            try {
//                //parsing the Intent data and displaying it in the imageview
//                Uri imageUri = data.getData();//Geting uri of the data
//                InputStream imageStream = getContentResolver().openInputStream(imageUri);//creating an imputstrea
//                profilePicture = BitmapFactory.decodeStream(imageStream);//decoding the input stream to bitmap
//                // image.setImageBitmap(profilePicture);
//                IMAGE_STATUS = true;//setting the flag
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private boolean validateProfile() {
//        if (!IMAGE_STATUS)
//            Toasty.info(RegisterActivity.this, "Select A Profile Picture", Toast.LENGTH_LONG).show();
//        return IMAGE_STATUS;
//    }

    private boolean validateNumber() {

        check = edtnumber.getText().toString();
        Log.e("inside number", check.length() + " ");
        if (check.length() > 10) {
            edtnumber.setError("Number cannot be grated than 10 digits");
            return false;
        } else if (check.length() < 10) {
            edtnumber.setError("Number should be 10 digits");
            return false;
        }

        return true;
    }

    private boolean validateCnfPass() {

        check = edtcnfpass.getText().toString();
        if(!check.equals(edtpass.getText().toString())) {
            edtcnfpass.setError("Both the passwords do not match");
            return false;
        }
        return true;
    }

    private boolean validatePass() {


        check = edtpass.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
            edtpass.setError("Password Must consist of 4 to 20 characters");
            return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
            edtemail.setError("Only @ special character allowed");
            return false;
        }

        return true;
    }

    private boolean validateEmail() {

        check = edtemail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            edtemail.setError("Email Must consist of 4 to 20 characters");
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            edtemail.setError("Only . and @ characters allowed");
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
            edtemail.setError("Enter Valid Email");
            return false;
        }


        return true;
    }

    private boolean validateName() {

        check = edtname.getText().toString();
        if (check.length() < 4 || check.length() > 20 || !check.matches("[ a-zA-Z]+"))  {
            edtname.setError("Name Must consist of 4 to 20 alphabet characters");
            return false;
        }
        return true;

    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20 || !check.matches("[ a-zA-Z]+"))  {
                edtname.setError("Name Must consist of 4 to 20 alphabet characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edtemail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edtemail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edtemail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for pass -----------------------------------------------------

    TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtpass.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                edtemail.setError("Only @ special character allowed");
            }
        }

    };

    //TextWatcher for repeat Password -----------------------------------------------------

    TextWatcher cnfpassWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (!check.equals(edtpass.getText().toString())) {
                edtcnfpass.setError("Both the passwords do not match");
            }
        }

    };


    //TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() > 10) {
                edtnumber.setError("Number cannot be grated than 10 digits");
            } else if (check.length() < 10) {
                edtnumber.setError("Number should be 10 digits");
            }
        }

    };

//
    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        checkConnection();
    }


    protected void onDestroy() {
        unregisterReceiver(cic);
        super.onDestroy();
    }


    void checkConnection(){
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        CheckInternetConnection cic;
        cic = new CheckInternetConnection();
        this.cic = cic ;
        registerReceiver(cic, in);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}