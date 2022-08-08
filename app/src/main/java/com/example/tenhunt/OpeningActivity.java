package com.example.tenhunt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OpeningActivity extends AppCompatActivity {
    private Button tenantBtn, landlordBtn;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        tenantBtn = findViewById(R.id.btn_tenant);
        landlordBtn = findViewById(R.id.btn_landlord);
        loginText = findViewById(R.id.btn_login_main);

        tenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        landlordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}