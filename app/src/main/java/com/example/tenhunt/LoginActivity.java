package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextLoginEmail, editTextLoginPwd;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView registerTV, forgotPassword;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //direct to register page if user don't have an account yet
        registerTV = findViewById(R.id.idTVRegister);

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,OpeningActivity.class);
                startActivity(i);

            }
        });

        getSupportActionBar().setTitle("Login");
        
        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);
        
        authProfile = FirebaseAuth.getInstance();

        //Forgot password reset onclick
        forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Reset password", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        
        //Login user
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();
                
                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid email is required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });


    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //If successful login direct to feed
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);

                    Toast.makeText(LoginActivity.this, "User has successfully logged in", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextLoginEmail.setError("User does not exist.");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginEmail.setError("Email or password do not match");
                        editTextLoginPwd.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    //Check is user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() !=null){
            Toast.makeText(LoginActivity.this, "Already Logged in", Toast.LENGTH_SHORT).show();

            //Start the Main feed activity
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);

        } else {
            Toast.makeText(LoginActivity.this, "You can log in now", Toast.LENGTH_SHORT).show();
        }
    }
}