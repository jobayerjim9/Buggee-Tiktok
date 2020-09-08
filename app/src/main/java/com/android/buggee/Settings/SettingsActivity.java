package com.android.buggee.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.buggee.Main_Menu.MainMenuActivity;
import com.android.buggee.Profile.Profile_F;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.SimpleClasses.WebViewActivity;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {
    TextView createPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_privacy);
        TextView privacySettings = findViewById(R.id.privacySettings);
        TextView privacyPolicy = findViewById(R.id.privacyPolicy);
        TextView termsOfUse = findViewById(R.id.termsOfUse);
        TextView reportAProblem = findViewById(R.id.reportAProblem);
        Switch pushNotification = findViewById(R.id.pushNotification);
        boolean noti = Variables.sharedPreferences.getBoolean(Variables.push_on_off, true);
        pushNotification.setChecked(noti);
        pushNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("pushOnOff", b + "");
                Variables.sharedPreferences.edit().putBoolean(Variables.push_on_off, b).apply();
            }
        });
        createPage = findViewById(R.id.createPage);
        int pageHave = Variables.sharedPreferences.getInt(Variables.page_have, 0);
        if (pageHave == 1) {
            createPage.setVisibility(View.GONE);

        }
        createPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreatePageDialog createPageDialog = new CreatePageDialog();
                createPageDialog.setCancelable(false);
                createPageDialog.show(getSupportFragmentManager(), "createDialog");

            }
        });
        reportAProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportProblemDialog reportProblemDialog = new ReportProblemDialog();
                reportProblemDialog.show(getSupportFragmentManager(), "report");
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://www.buggee.app/privacy-policy/");
                startActivity(intent);
            }
        });
        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://www.buggee.app/terms-of-service/");
                startActivity(intent);
            }
        });
        TextView getVerified = findViewById(R.id.getVerified);
        TextView myAccount = findViewById(R.id.myAccount);
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
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, RequestVerificationActivity.class);
                intent.putExtra("type", "profile");
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        int pageHave = Variables.sharedPreferences.getInt(Variables.page_have, 0);
        if (pageHave == 1) {
            createPage.setVisibility(View.GONE);
        }
    }
}