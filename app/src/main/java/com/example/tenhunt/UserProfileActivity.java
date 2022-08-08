package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewFullName, textViewEmail, textViewDoB, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeToRefresh();

        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progressBar);

        //Set OnClickListener on imageView to open uploadProfileActivity
        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            //This is the logic to fetch the data to the database... watch
            showUserProfile(firebaseUser);
        }

        authProfile = FirebaseAuth.getInstance();
        //FirebaseUser firebaseUser = authProfile.getCurrentUser();
        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.with(this).load(uri).into(imageView);

    }
    //Refresh swipe
    private void swipeToRefresh() {
        //Look for swipe container
        swipeContainer = findViewById(R.id.swipeContainer);

        //Setup refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);
            }
        });

        //Configure refresh color
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting user reference from database under the "registered users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    //this is the fetching from database
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    mobile = readUserDetails.mobile;

                    //This is passing the data to the frontend
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewMobile.setText(mobile);

                    //Set User Profile Pic
                    Uri uri = firebaseUser.getPhotoUrl();

                    //using Picasso
                    //Picasso.with(this).load(uri).into(imageView);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    //Create ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // This is the menu tab
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(UserProfileActivity.this);
        } else if (id == R.id.idProfile){
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_profile){
            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_email){
            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password){
            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.idAboutUs){
            Toast.makeText(this, "About Us", Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(UserProfileActivity.this,AboutUsActivity.class);
            startActivity(intent1);
        } else if (id == R.id.idLogOut){
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);

            //CLear stack
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}