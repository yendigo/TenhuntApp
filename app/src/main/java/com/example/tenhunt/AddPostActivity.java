package com.example.tenhunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddPostActivity extends AppCompatActivity {

    //for the image output and input
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private ImageView mImageView;
    private Uri mImageUri;

    //Uploading image to the firebase database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    //for text inputs
    private TextInputEditText postTitleEdt, postPriceEdt, postDescEdt, postContactEdt, postEmailEdt;
    private Button addPostBtn;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uploadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Add Post");

        setContentView(R.layout.activity_add_post);
        //Call all variables
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mImageView = findViewById(R.id.image_add_view);
        //all text find ID
        postTitleEdt = findViewById(R.id.idEdtTitle);
        postPriceEdt = findViewById(R.id.idEdtPostPrice);
        postDescEdt = findViewById(R.id.idEdtPostDesc);
        postContactEdt = findViewById(R.id.idEdtContactNumber);
        postEmailEdt = findViewById(R.id.idEdtPostEmail);
        addPostBtn = findViewById(R.id.idBtnAddPost);
        progressBar = findViewById(R.id.progressBar);

        //Uploading image to firebase
        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(AddPostActivity.this, "Upload still in progress..", Toast.LENGTH_LONG).show();
                } else {
                    uploadFile();
                }
            }
        });


    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            }, 500);

                            Toast.makeText(AddPostActivity.this, "Upload success", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(postTitleEdt.getText().toString().trim(),
                                    taskSnapshot.getStorage().getDownloadUrl().toString(),
                                    postDescEdt.getText().toString(),
                                    postPriceEdt.getText().toString(),
                                    postContactEdt.getText().toString(),
                                    postEmailEdt.getText().toString());

                            String uploadID = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadID).setValue(upload);

                            progressBar.setVisibility(View.GONE);
                            openImagesActivity();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
            } else {
                Toast.makeText(this, "You haven't selected any file", Toast.LENGTH_LONG).show();
            }
        }
        private void openImagesActivity(){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

    }

}