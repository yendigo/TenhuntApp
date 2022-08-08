package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private TextInputEditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private String textFullName, textDoB, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show Profile Data
        showProfile(firebaseUser);

        //Date picker for the date of birth input
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Extracting saved date
                String textSADoB[] = textDoB.split("/");


                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //Date picker dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //Update Profile Button
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {
        //Validate the mobile number
        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(UpdateProfileActivity.this, "PLease enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        }  else if (TextUtils.isEmpty(textDoB)) {
            Toast.makeText(UpdateProfileActivity.this, "Enter your date of birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of birth is required");
            editTextUpdateDoB.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Enter a mobile number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile number is required");
            editTextUpdateMobile.requestFocus();
        } else if (textMobile.length() != 11) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter a mobile number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile number should be 11 digits");
            editTextUpdateMobile.requestFocus();
        } else if(!mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter a mobile number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile number is not valid");
            editTextUpdateMobile.requestFocus();
        }  else {
            textFullName = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            //Enter user data into firebase database
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textMobile);

            //Extract User reference from database sunder "Registered Users"
            DatabaseReference referenceProfile  = FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Setting new display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                                setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "update Successful", Toast.LENGTH_LONG).show();

                        //Stop user from returning to updateProfile activity
                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    //Fetch data from firebase and display on change profile
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting User reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readUserDetails.doB;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}