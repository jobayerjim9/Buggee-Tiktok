package com.systematics.buggee.Accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.systematics.buggee.Main_Menu.Custom_ViewPager;
import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.SignUpViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUi();
    }

    private void initUi() {
        TabLayout signUpTab=findViewById(R.id.signUpTab);
        final Custom_ViewPager signUpViewPager=findViewById(R.id.signUpViewPager);
        SignUpViewPagerAdapter adapter=new SignUpViewPagerAdapter(getSupportFragmentManager());
        signUpViewPager.setAdapter(adapter);
        signUpTab.setupWithViewPager(signUpViewPager);
    }
}