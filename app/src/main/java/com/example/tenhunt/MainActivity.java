package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener{
//For image upload
    private List<Upload> mUploads;

    private RecyclerView postRV;
    private RecyclerAdapter mAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton addFAB;
    private FirebaseStorage mStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView txtSearch;
    private ListView listData;
    private DrawerLayout drawer;

    private void openDetailActivity(String[] data){
        Intent intent = new Intent(this, PostPageActivity.class);

        intent.putExtra("NAME_KEY", data[0]);
        intent.putExtra("PRICE_KEY", data[3]);
        intent.putExtra("IMAGE_KEY", data[1]);
        intent.putExtra("DESC_KEY", data[2]);
        intent.putExtra("CONTACT_KEY", data[4]);
        intent.putExtra("EMAIL_KEY", data[5]);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Search posts

        //Recycler View
        postRV = findViewById(R.id.idRVPosts);
        postRV.setHasFixedSize(true);
        postRV.setLayoutManager(new LinearLayoutManager(this));
        addFAB = findViewById(R.id.idAddFAB);

        //progressBar.findViewById(R.id.progressBarMain);
        //progressBar.setVisibility(View.VISIBLE);

        mUploads = new ArrayList<>();
        mAdapter = new RecyclerAdapter(MainActivity.this, mUploads);
        postRV.setAdapter(mAdapter);
        mAdapter.setOnItemCLickListener(MainActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();

                for(DataSnapshot uploadSnapshot : snapshot.getChildren()){
                    Upload upload = uploadSnapshot.getValue(Upload.class);
                    upload.setKey(uploadSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                //progressBar.setVisibility(View.GONE);

                //Search on data change populate
                //populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });



        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddPostActivity.class));
            }
        });

    }

    //If the recycler view is clicked -- open the postPageActivity
    public void onItemClick(int position){
        Upload clickedUpload = mUploads.get(position);
        String[] uploadData = {clickedUpload.getPostTitle(), clickedUpload.getmImageUrl(), clickedUpload.getPostDesc(), clickedUpload.getPostPrice(), clickedUpload.getPostContact(), clickedUpload.getPostEmail()};
        openDetailActivity(uploadData);
    }



    // This is the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.idProfile:
                Toast.makeText(this, "User Profile", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.idAboutUs:
                Toast.makeText(this, "About Us", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(intent1);
            case R.id.idLogOut:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                this.finish();
            default:
            return super.onOptionsItemSelected(item);

        }


    }

}