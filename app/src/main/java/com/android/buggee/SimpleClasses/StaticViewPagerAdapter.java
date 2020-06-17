package com.android.buggee.SimpleClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.buggee.Discover.Discover_F;
import com.android.buggee.Home.Home_F;
import com.android.buggee.Inbox.Inbox_F;
import com.android.buggee.Profile.Profile_Tab_F;

public class StaticViewPagerAdapter extends FragmentPagerAdapter {

    public StaticViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            return new Home_F();
        }
        else if (position==1) {
            return new Discover_F();
        }
        else if (position==2) {
            return new Inbox_F();
        }
        else if (position==3) {
            return new Profile_Tab_F();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
