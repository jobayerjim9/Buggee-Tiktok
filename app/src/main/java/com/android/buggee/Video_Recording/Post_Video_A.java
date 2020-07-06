package com.android.buggee.Video_Recording;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.buggee.Main_Menu.MainMenuActivity;
import com.android.buggee.R;
import com.android.buggee.Services.ServiceCallback;
import com.android.buggee.Services.Upload_Service;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;

import java.io.File;

public class Post_Video_A extends AppCompatActivity implements ServiceCallback {


    ImageView video_thumbnail;

    String video_path;

    ProgressDialog progressDialog;

    ServiceCallback serviceCallback;

    LinearLayout pageSwitchLayout;
    Switch pageSwitch;
    EditText urlEditText;
    EditText description_edit;
    String uploadFrom = "profile";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);
        Log.d("isAudioSelected?", Variables.audio_selected + "");
        if (Variables.audio_selected) {
            video_path = Variables.output_filter_file_final;
        } else {
            video_path = Variables.output_filter_file;
        }
        pageSwitchLayout = findViewById(R.id.pageSwitchLayout);
        pageSwitch = findViewById(R.id.pageSwitch);
        urlEditText = findViewById(R.id.urlEditText);
        video_thumbnail = findViewById(R.id.video_thumbnail);
        int page_have = Variables.sharedPreferences.getInt(Variables.page_have, 0);
        if (page_have == 1) {
            pageSwitchLayout.setVisibility(View.VISIBLE);
        } else {
            pageSwitchLayout.setVisibility(View.GONE);
        }

        pageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("pageSwitch", b + "");
                if (b) {
                    uploadFrom = "page";
                    urlEditText.setVisibility(View.VISIBLE);
                } else {
                    urlEditText.setVisibility(View.GONE);
                    uploadFrom = "profile";
                }
            }
        });

        description_edit = findViewById(R.id.description_edit);

        // this will get the thumbnail of video and show them in imageview
        Bitmap bmThumbnail;
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        if (bmThumbnail != null) {
            video_thumbnail.setImageBitmap(bmThumbnail);
        } else {
        }




        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);




      findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
            onBackPressed();
        }
    });


     findViewById(R.id.post_btn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){

            progressDialog.show();
            Start_Service();

        }
    });



}



// this will start the service for uploading the video into database
    public void Start_Service() {
        Log.d("videoPath", video_path);
        serviceCallback = this;
        Log.d("videoPath", video_path);
        Upload_Service mService = new Upload_Service(serviceCallback);
        if (!Functions.isMyServiceRunning(this, mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("startservice");
            mServiceIntent.putExtra("uri", "" + Uri.fromFile(new File(video_path)));
            mServiceIntent.putExtra("desc", "" + description_edit.getText().toString());
            mServiceIntent.putExtra("uploadFrom", "" + uploadFrom);
            mServiceIntent.putExtra("url", "" + urlEditText.getText().toString());
            startService(mServiceIntent);


            Intent intent = new Intent(this, Upload_Service.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }
        else {
            Toast.makeText(this, "Please wait video already in uploading progress", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        Stop_Service();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }




    // when the video is uploading successfully it will restart the appliaction
    @Override
    public void ShowResponce(final String responce) {
        if (mConnection != null)
            unbindService(mConnection);


        Toast.makeText(Post_Video_A.this, responce, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();


        if (responce.equalsIgnoreCase("Your Video is uploaded Successfully")) {
            File output_filter_file_final = new File(Variables.output_filter_file_final);
            output_filter_file_final.delete();

            File output_filter_file = new File(Variables.output_filter_file);
            output_filter_file.delete();

            File outputfile = new File(Variables.outputfile);
            outputfile.delete();

            File outputfile2 = new File(Variables.outputfile2);
            outputfile2.delete();

            File SelectedAudio_AAC = new File(Variables.output_audio);
            SelectedAudio_AAC.delete();
            Variables.audio_selected = false;
            startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));
            finishAffinity();

        }
    }


    // this is importance for binding the service to the activity
    Upload_Service mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

           Upload_Service.LocalBinder binder = (Upload_Service.LocalBinder) service;
            mService = binder.getService();

            mService.setCallbacks(Post_Video_A.this);



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // this function will stop the the ruuning service
    public void Stop_Service(){

        serviceCallback=this;

        Upload_Service mService = new Upload_Service(serviceCallback);

        if (Functions.isMyServiceRunning(this,mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("stopservice");
            startService(mServiceIntent);

        }


    }



}
