package com.systematics.buggee.Main_Menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.ApiRequest;
import com.systematics.buggee.SimpleClasses.Callback;
import com.systematics.buggee.SimpleClasses.Functions;
import com.systematics.buggee.SimpleClasses.Variables;
import com.systematics.buggee.Video_Recording.LiveBroadcasterActivity;
import com.systematics.buggee.WatchVideos.WatchVideos_F;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainMenuActivity extends AppCompatActivity {
    public static MainMenuActivity mainMenuActivity;
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;

    public static String token;

    public static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        PackageInfo info;
        try {

            info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hash_key = new String(Base64.encode(md.digest(), 0));
                Log.d("signingKey", hash_key);
            }

        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.live_file), Context.MODE_PRIVATE);
        boolean liveExist = preferences.getBoolean(getString(R.string.live_exist), false);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("tokenUpdating", token);
                        updateToken(token);
                        // Log and toast
                    }
                });

        mainMenuActivity = this;

        intent = getIntent();

        setIntent(null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Variables.screen_height= displayMetrics.heightPixels;
        Variables.screen_width= displayMetrics.widthPixels;

        Variables.sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        Variables.user_id=Variables.sharedPreferences.getString(Variables.u_id,"");
        Variables.user_name=Variables.sharedPreferences.getString(Variables.u_name,"");
        Variables.user_pic=Variables.sharedPreferences.getString(Variables.u_pic,"");


        token= FirebaseInstanceId.getInstance().getToken();
        if(token==null || (token.equals("")||token.equals("null")))
            token = Variables.sharedPreferences.getString(Variables.device_token, "null");


        if (savedInstanceState == null) {

            initScreen();

        } else {
            mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
        }
        try {
            Bundle bundle = intent.getExtras();
            String action_type = bundle.getString("action_type");
            if (action_type != null) {
                if (action_type.toLowerCase().equals("comment")) {
                    Intent intent = new Intent(this, WatchVideos_F.class);
                    bundle.putString("video_id", bundle.getString("tag"));
                    bundle.putBoolean("openComment", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else if (action_type.toLowerCase().equals("video_like")) {
                    Intent intent = new Intent(this, WatchVideos_F.class);
                    bundle.putString("video_id", bundle.getString("tag"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    mainMenuFragment.moveToNotification();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (liveExist) {
            Intent intent = new Intent(this, LiveBroadcasterActivity.class);
            intent.putExtra("liveName", preferences.getString(getString(R.string.live_name), "No"));
            intent.putExtra("liveDetails", preferences.getString(getString(R.string.live_details), "No"));
            startActivity(intent);
            finish();
        }


    }

    private void updateToken(String s) {
        Log.d("updatingToken", s);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.user_id);
            parameters.put("token", s);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(getBaseContext(), Variables.updateToken, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    Log.d("tokenUpdated", resp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void initScreen() {

        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }






    @Override
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Functions.showToast(this, "Tap Again To Exit");
                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }








}
