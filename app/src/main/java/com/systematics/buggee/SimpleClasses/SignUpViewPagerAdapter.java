package com.systematics.buggee.SimpleClasses;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.systematics.buggee.Accounts.EmailSignUpFragment;
import com.systematics.buggee.Accounts.PhoneSignUpFragment;

public class SignUpViewPagerAdapter extends FragmentPagerAdapter {

    public SignUpViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            return new EmailSignUpFragment();
        }
        else if (position==1) {
            return new PhoneSignUpFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        super.getPageTitle(position);
        if (position==0) {
            return "Email";
        }
        else if (position==1) {
            return "Phone";
        }
        return null;
    }
}
