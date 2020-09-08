package com.android.buggee.Main_Menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.android.buggee.Accounts.PageActivity;
import com.android.buggee.Chat.Chat_Activity;
import com.android.buggee.Notifications.Notification_F;
import com.android.buggee.SimpleClasses.StaticViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.Accounts.Login_A;
import com.android.buggee.Discover.Discover_F;
import com.android.buggee.Home.Home_F;
import com.android.buggee.Main_Menu.RelateToFragment_OnBack.OnBackPressListener;
import com.android.buggee.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.android.buggee.Profile.Profile_Tab_F;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.Video_Recording.Video_Recoder_A;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainMenuFragment extends RootFragment implements View.OnClickListener {

    public static TabLayout tabLayout;

    protected Custom_ViewPager pager;

    private ViewPagerAdapter adapter;
    private static BottomNavigationView bottomNav;
    Context context;
    private StaticViewPagerAdapter viewPagerAdapter;
    private static ImageView cameraButton;
    static int bottomL, bottomR, bottomU, bottomD;

    public MainMenuFragment() {

    }

    public static void showHidePlus(boolean show) {
        if (show) {
            bottomNav.setPadding(bottomL, bottomU, bottomR, bottomU);
            cameraButton.setVisibility(View.VISIBLE);
        } else {
            bottomNav.setPadding(bottomR, bottomU, bottomR, bottomU);
            cameraButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        context = getContext();
        float scale = getResources().getDisplayMetrics().density;
        bottomL = (int) (70 * scale + 0.5f);
        bottomR = (int) (2 * scale + 0.5f);
        bottomU = (int) (16 * scale + 0.5f);


//        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
        viewPagerAdapter = new StaticViewPagerAdapter(getChildFragmentManager());
        pager = view.findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        pager.setCurrentItem(0);
        bottomNav = view.findViewById(R.id.bottomNav);
        cameraButton = view.findViewById(R.id.cameraButton);


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_permissions()) {

                    RecordModeChoser recordModeChoser = new RecordModeChoser();
                    recordModeChoser.show(getChildFragmentManager(), "chooser");

                }
            }
        });
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) pager.getLayoutParams();
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.home)
                {
                    lp.bottomMargin=0;
                    pager.setCurrentItem(0);
                    return true;
                }
                else if (menuItem.getItemId()==R.id.discover) {
                  //  lp.setMargins(0,0,0,200);
                    pager.setCurrentItem(1);
                    return true;
                }
                else if (menuItem.getItemId()==R.id.inbox) {
                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)){
                       // lp.setMargins(0,0,0,200);
                        pager.setCurrentItem(2);
                        return true;

                    }else {
                        Intent intent = new Intent(getActivity(), Login_A.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        return false;
                    }
                }
                else if (menuItem.getItemId()==R.id.profile) {
                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                        // lp.setMargins(0,0,0,200);
                        if (Variables.sharedPreferences.getInt(Variables.id_page, 0) == 1) {
                            startActivity(new Intent(context, PageActivity.class));
                        } else {
                            pager.setCurrentItem(3);
                            return true;
                        }

                    }else {

                        //lp.setMargins(0,0,0,200);
                        Intent intent = new Intent(getActivity(), Login_A.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        return false;
                    }
                }
                return false;
            }
        });

//        view.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
        }

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Note that we are passing childFragmentManager, not FragmentManager

//        pager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(pager);
       // setupTabIcons();




    }



    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(pager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }


    // this function will set all the icon and text in
    // Bottom tabs when we open an activity
    private void setupTabIcons() {

        View view1 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView1= view1.findViewById(R.id.image);
        TextView  title1=view1.findViewById(R.id.text);
        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_white));
        title1.setText("Home");
        title1.setTextColor(context.getResources().getColor(R.color.white));
        tabLayout.getTabAt(1).setCustomView(view1);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView2= view2.findViewById(R.id.image);
        TextView  title2=view2.findViewById(R.id.text);
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_discovery_gray));
        imageView2.setColorFilter(ContextCompat.getColor(context, R.color.colorwhite_50), android.graphics.PorterDuff.Mode.SRC_IN);
        title2.setText("Discover");
        title2.setTextColor(context.getResources().getColor(R.color.colorwhite_50));
        tabLayout.getTabAt(2).setCustomView(view2);


        View view3 = LayoutInflater.from(context).inflate(R.layout.item_add_tab_layout, null);
        tabLayout.getTabAt(0).setCustomView(view3);

        View view4 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView4= view4.findViewById(R.id.image);
        TextView  title4=view4.findViewById(R.id.text);
        imageView4.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_gray));
        imageView4.setColorFilter(ContextCompat.getColor(context, R.color.colorwhite_50), android.graphics.PorterDuff.Mode.SRC_IN);
        title4.setText("Inbox");
        title4.setTextColor(context.getResources().getColor(R.color.colorwhite_50));
        tabLayout.getTabAt(3).setCustomView(view4);

        View view5 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView5= view5.findViewById(R.id.image);
        TextView  title5=view5.findViewById(R.id.text);
        imageView5.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_gray));
        imageView5.setColorFilter(ContextCompat.getColor(context, R.color.colorwhite_50), android.graphics.PorterDuff.Mode.SRC_IN);
        title5.setText("Profile");
        title5.setTextColor(context.getResources().getColor(R.color.colorwhite_50));
        tabLayout.getTabAt(4).setCustomView(view5);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v=tab.getCustomView();
                ImageView image=v.findViewById(R.id.image);
                TextView  title=v.findViewById(R.id.text);

                switch (tab.getPosition()){
                    case 1:
                        OnHome_Click();
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_white));
                        title.setTextColor(context.getResources().getColor(R.color.white));
                        break;

                    case 2:
                        Onother_Tab_Click();
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_discover_red));
                        image.setColorFilter(ContextCompat.getColor(context, R.color.app_color), android.graphics.PorterDuff.Mode.SRC_IN);
                        title.setTextColor(context.getResources().getColor(R.color.app_color));
                        break;


                    case 3:
                        Onother_Tab_Click();
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_red));
                        image.setColorFilter(ContextCompat.getColor(context, R.color.app_color), android.graphics.PorterDuff.Mode.SRC_IN);
                        title.setTextColor(context.getResources().getColor(R.color.app_color));
                        break;
                    case 4:
                        Onother_Tab_Click();
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_red));
                        image.setColorFilter(ContextCompat.getColor(context, R.color.app_color), android.graphics.PorterDuff.Mode.SRC_IN);
                        title.setTextColor(context.getResources().getColor(R.color.app_color));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v=tab.getCustomView();
                ImageView image=v.findViewById(R.id.image);
                TextView  title=v.findViewById(R.id.text);

                switch (tab.getPosition()){
                    case 1:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_gray));
                        title.setTextColor(context.getResources().getColor(R.color.darkgray));
                        break;
                    case 2:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_discovery_gray));
                        title.setTextColor(context.getResources().getColor(R.color.darkgray));
                        break;

                    case 3:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_gray));
                        title.setTextColor(context.getResources().getColor(R.color.darkgray));
                        break;
                    case 4:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_gray));
                        title.setTextColor(context.getResources().getColor(R.color.darkgray));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        final LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.setEnabled(false);

        tabStrip.getChildAt(2).setClickable(false);
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check_permissions()) {
                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {

                        Intent intent = new Intent(getActivity(), Video_Recoder_A.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                    }
                    else {
                        Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        tabStrip.getChildAt(3).setClickable(false);
        view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)){

                    TabLayout.Tab tab=tabLayout.getTabAt(3);
                    tab.select();

                }else {
                    Intent intent = new Intent(getActivity(), Login_A.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }

            }
        });

        tabStrip.getChildAt(4).setClickable(false);
        view5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)){

                    TabLayout.Tab tab=tabLayout.getTabAt(4);
                    tab.select();

                }else {

                    Intent intent = new Intent(getActivity(), Login_A.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }

            }
        });



        if(MainMenuActivity.intent!=null){

            if(MainMenuActivity.intent.hasExtra("action_type")) {


                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                    String action_type=MainMenuActivity.intent.getExtras().getString("action_type");

                    if(action_type.equals("message")){

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TabLayout.Tab  tab=tabLayout.getTabAt(3);
                                tab.select();
                            }
                        },1500);


                        String id=MainMenuActivity.intent.getExtras().getString("senderid");
                        String name=MainMenuActivity.intent.getExtras().getString("title");
                        String icon=MainMenuActivity.intent.getExtras().getString("icon");

                        chatFragment(id,name,icon);

                    }
                }

            }

        }




    }


    class ViewPagerAdapter extends FragmentPagerAdapter {


        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new Home_F();
                    break;

                case 1:
                    result = new Discover_F();
                    break;

                case 2:
                    result = new Notification_F();
                    break;

                case 3:
                    result = new Profile_Tab_F();
                    break;

                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 4;
        }


        @Override
        public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            registeredFragments.remove(position);

            super.destroyItem(container, position, object);

        }


        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public Fragment getRegisteredFragment(int position) {

            return registeredFragments.get(position);

        }
    }

    public void OnHome_Click (){

        TabLayout.Tab tab1=tabLayout.getTabAt(2);
        View view1=tab1.getCustomView();
        ImageView imageView1= view1.findViewById(R.id.image);
        imageView1.setColorFilter(ContextCompat.getColor(context, R.color.colorwhite_50), android.graphics.PorterDuff.Mode.SRC_IN);
        TextView tex1=view1.findViewById(R.id.text);
        tex1.setTextColor(context.getResources().getColor(R.color.colorwhite_50));
        tab1.setCustomView(view1);

        TabLayout.Tab tab2=tabLayout.getTabAt(0);
        View view2=tab2.getCustomView();
        ImageView image= view2.findViewById(R.id.image);
        image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_add_box_24));
        tab2.setCustomView(view2);

        TabLayout.Tab tab3=tabLayout.getTabAt(3);
        View view3=tab3.getCustomView();
        ImageView imageView3= view3.findViewById(R.id.image);
        imageView3.setColorFilter(ContextCompat.getColor(context, R.color.colorwhite_50), android.graphics.PorterDuff.Mode.SRC_IN);
        TextView tex3=view3.findViewById(R.id.text);
        tex3.setTextColor(context.getResources().getColor(R.color.colorwhite_50));
        tab3.setCustomView(view3);


        TabLayout.Tab tab4=tabLayout.getTabAt(4);
        View view4=tab4.getCustomView();
        ImageView imageView4= view4.findViewById(R.id.image);
        imageView4.setColorFilter(ContextCompat.getColor(context, R.color.colorwhite_50), android.graphics.PorterDuff.Mode.SRC_IN);
        TextView tex4=view4.findViewById(R.id.text);
        tex4.setTextColor(context.getResources().getColor(R.color.colorwhite_50));
        tab4.setCustomView(view4);


        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        pager.setLayoutParams(params);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.black));
    }

    public void Onother_Tab_Click(){



        TabLayout.Tab tab1=tabLayout.getTabAt(2);
        View view1=tab1.getCustomView();
        TextView tex1=view1.findViewById(R.id.text);
        ImageView imageView1= view1.findViewById(R.id.image);
        imageView1.setColorFilter(ContextCompat.getColor(context, R.color.darkgray), android.graphics.PorterDuff.Mode.SRC_IN);
        tex1.setTextColor(context.getResources().getColor(R.color.darkgray));
        tab1.setCustomView(view1);

        TabLayout.Tab tab2=tabLayout.getTabAt(0);
        View view2=tab2.getCustomView();
        ImageView image= view2.findViewById(R.id.image);
        image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_add_box_24));
        tab2.setCustomView(view2);

        TabLayout.Tab tab3=tabLayout.getTabAt(3);
        View view3=tab3.getCustomView();
        ImageView imageView3= view3.findViewById(R.id.image);
        imageView3.setColorFilter(ContextCompat.getColor(context, R.color.darkgray), android.graphics.PorterDuff.Mode.SRC_IN);
        TextView tex3=view3.findViewById(R.id.text);
        tex3.setTextColor(context.getResources().getColor(R.color.darkgray));
        tab3.setCustomView(view3);


        TabLayout.Tab tab4=tabLayout.getTabAt(4);
        View view4=tab4.getCustomView();
        ImageView imageView4= view4.findViewById(R.id.image);
        imageView4.setColorFilter(ContextCompat.getColor(context, R.color.darkgray), android.graphics.PorterDuff.Mode.SRC_IN);
        TextView tex4=view4.findViewById(R.id.text);
        tex4.setTextColor(context.getResources().getColor(R.color.darkgray));
        tab4.setCustomView(view4);





        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.tabs);
        pager.setLayoutParams(params);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.black));

    }




    // we need 4 permission during creating an video so we will get that permission
    // before start the video recording
    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    public void chatFragment(String receiverid,String name,String picture){
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

        Bundle args = new Bundle();
        args.putString("user_id", receiverid);
        args.putString("user_name",name);
        args.putString("user_pic",picture);

        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }



}