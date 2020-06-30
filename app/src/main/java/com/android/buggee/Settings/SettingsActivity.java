package com.android.buggee.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.buggee.Main_Menu.MainMenuActivity;
import com.android.buggee.Profile.Profile_F;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.Variables;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_privacy);
        TextView privacySettings = findViewById(R.id.privacySettings);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView profilePic = findViewById(R.id.profilePic);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        try {
            Picasso.with(this)
                    .load(Profile_F.pic_url)
                    .placeholder(getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .resize(200, 200).centerCrop().into(profilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        privacySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, PrivacySettingsActivity.class));
            }
        });
        TextView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
                editor.putString(Variables.u_id, "");
                editor.putString(Variables.u_name, "");
                editor.putString(Variables.u_pic, "");
                editor.putBoolean(Variables.islogin, false);
                editor.commit();
                finish();
                startActivity(new Intent(SettingsActivity.this, MainMenuActivity.class));
            }
        });
    }
}