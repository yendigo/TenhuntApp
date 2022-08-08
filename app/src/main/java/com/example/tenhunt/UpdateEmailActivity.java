package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private TextInputEditText editTextNewEmail, editTextPwd, oldEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Change Email");

        progressBar = findViewById(R.id.progressBar);
        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //Set old email id into text view
        userOldEmail = firebaseUser.getEmail();
        oldEmail = findViewById(R.id.textView_update_email_old);
        oldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(firebaseUser);
        }

    }
    //Verify user before updating new email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtain the user password
                userPwd = editTextPwd.getText().toString();

                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed", Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Please enter your password");
                    editTextPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.GONE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified", Toast.LENGTH_LONG).show();

                                //Set text View to user authenticated
                                textViewAuthenticated.setText("You can now enter a new email");

                                //Disable
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                //Change color of update email
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this, R.color.dark_green));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = editTextNewEmail.getText().toString();
                                        if(TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this, "New email is required", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter new Email");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(UpdateEmailActivity.this, "New enter a valid email", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter valid email");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this, "New email cannot be the same as your current email", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter new email");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    //Verify Email
                    firebaseUser.sendEmailVerification();

                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated, Please verify new email", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}