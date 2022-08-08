package com.example.tenhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import java.util.Calendar;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about_us);

        Element adsElement = new Element();
        adsElement.setTitle("SyeTech");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //.setCustomFont(String) // or Typeface
                .setImage(R.drawable.tenhunt)
                .setDescription("Tenhunt, a product of SyeTech and based in City of Manila, Philippines, was founded in May 2022 with the goal of creating a sustainable community marketplace where users can search for, find, post, and rent for living space online or from a smartphone or tablet.\n" +
                        "\n" +
                        "Tenhunt connects people to experience new areas within reach of their comfort, whether they are staying in an apartment, dorm, bedspace, or condominium. Tenhunt provides the most simple way for individuals to profit from their extra space and makes it easier to discover someone who can live on it thanks to its various features and a growing community of users.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("sye.tech7@gmail.com")
                .addFacebook("SyeTech")
                .addPlayStore("Playstore")
                .addGitHub("git")
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element createCopyright() {
        Element copyright = new Element();
        String copyrightString = String.format("Copyright %d by SyeTech", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIconDrawable(R.drawable.syi);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutUsActivity.this, copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}