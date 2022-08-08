package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile,
            editTextRegisterPwd, editTextRegisterConfirmPwd;
    private Button registerBtn;
    private ProgressBar progressBar;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";
    private TextView loginTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Signup");

        Toast.makeText(RegisterActivity.this, "Signup now", Toast.LENGTH_LONG).show();

        //this is to direct to login if user already has an account
        loginTV = findViewById(R.id.idTVLogin);

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);

            }
        });

        //Text inputs and progress bar
        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        //Date picker for the date of birth input
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get the text input data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();

                //Validate the mobile number
                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "PLease enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "PLease enter an Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "PLease re-enter an Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Enter a mobile number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile number is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter a mobile number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile number should be 11 digits");
                    editTextRegisterMobile.requestFocus();
                } else if(!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter a mobile number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile number is not valid");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Enter your a password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                 } else if (textPwd.length()<6 ) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password too weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Please confirm password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Password don't match", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Re-enter password");
                    editTextRegisterConfirmPwd.requestFocus();

                    //Clear the entered password
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textMobile, textPwd);
                }
            }
        });
    }

    //Register user on the firebase
    private void registerUser(String textFullName, String textEmail, String textDoB, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create the User Profile
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Update Display Name of User
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);


                    //Enter the user data to the firebase database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textMobile);

                    //extraction user reference form database for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    //get the user UID so the information will be directed to the user
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_LONG).show();

                                //Open the main feed after successful register
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                //to prevent user to go back the the signup page
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish(); //close the signup page
                            } else {
                                Toast.makeText(RegisterActivity.this, "User registration failed", Toast.LENGTH_LONG).show();

                            }
                            //Hide the progress bar
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                } else {
                    //This is the exception handling to avoid input errors
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextRegisterPwd.setError("Password is too weak. use a mix of Uppercase and lowercase , numbers and special characters");
                        editTextRegisterPwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextRegisterEmail.setError("Your email is invalid or already in use.");
                        editTextRegisterEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterEmail.setError("A user has already used this email");
                        editTextRegisterEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}