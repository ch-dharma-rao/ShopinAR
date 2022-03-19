package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button button_reset_password;
    TextView text_email;
    ProgressBar progressBar;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        button_reset_password = findViewById(R.id.button_reset_password);
        text_email = findViewById(R.id.text_email);
        progressBar = findViewById(R.id.progressbar);

        button_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = text_email.getText().toString().trim();

                if(email.isEmpty()){
                    text_email.setError("Email Required");
                    text_email.requestFocus();
                }


                if (email.length() < 4 || email.length() > 30) {
                    text_email.setError("Email Must consist of 4 to 30 characters");
                } else if (!email.matches("^[A-Za-z0-9.@]+")) {
                    text_email.setError("Only . and @ characters allowed");
                } else if (!email.contains("@") || !email.contains(".")) {
                    text_email.setError("Email must contain @ and .");
                }

                progressBar.setVisibility(View.VISIBLE);

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ForgotPassword.this,"Check your Email",Toast.LENGTH_SHORT).show();
                                    Log.d("ForgotPassword", "Email sent.");
                                }

                                else {
                                    Toast.makeText(ForgotPassword.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });



    }
}