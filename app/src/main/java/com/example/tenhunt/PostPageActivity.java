package com.example.tenhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PostPageActivity extends AppCompatActivity {

    private TextView postTitleDetailEdt, postPriceDetailEdt, postDescDetailEdt, postContactDetailEdt, postEmailDetailEdt;
    private ImageView uploadDetailImageView;

    private void initializeWidgets(){
        postTitleDetailEdt = findViewById(R.id.idTVPostDetailTitle);
        postPriceDetailEdt = findViewById(R.id.idTVDetailPrice);
        postDescDetailEdt = findViewById(R.id.idTVPostDetailDesc);
        postContactDetailEdt = findViewById(R.id.idTVPostDetailContact);
        postEmailDetailEdt = findViewById(R.id.idTVPostDetailEmail);
        uploadDetailImageView = findViewById(R.id.image_view_upload_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        initializeWidgets();

        //receive data from main activity by intent
        Intent i = this.getIntent();
        String title = i.getExtras().getString("NAME_KEY");
        String price = i.getExtras().getString("PRICE_KEY");
        String desc = i.getExtras().getString("DESC_KEY");
        String contact = i.getExtras().getString("CONTACT_KEY");
        String email = i.getExtras().getString("EMAIL_KEY");
        String imageURL = i.getExtras().getString("IMAGE_KEY");


        //set the received data to textViews and imageViews
        postTitleDetailEdt.setText(title);
        postPriceDetailEdt.setText(price);
        postDescDetailEdt.setText(desc);
        postContactDetailEdt.setText(contact);
        postEmailDetailEdt.setText(email);
        Picasso.with(this).setLoggingEnabled(true);
        Picasso.with(this)
                .load(imageURL).skipMemoryCache()
                .placeholder(R.drawable.dorm_hunt)
                .fit()
                .centerCrop()
                .into(uploadDetailImageView);
    }
}