package com.android.buggee.WatchVideos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.Variables;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class LiveWatchActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;
    private RtcEngine mRtcEngine;
    String id;
    // Ask for Android device permissions at runtime.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broadcaster);

        id = getIntent().getStringExtra("id");
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        Log.d("idForWatch", id);
        initializeEngine();
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                    //  setupRemoteVideo(uid);
                }
            });
        }

        @Override
        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of the broadcaster is received and decoded after the broadcaster successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    //setupRemoteVideo(uid);
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        // Listen for the onUserOffline callback.
        // This callback occurs when the broadcaster leaves the channel or drops offline.
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //  mRtcEngine.leaveChannel();
                    Log.i("agora", "User offline, uid: " + (uid & 0xFFFFFFFFL));
                    //onRemoteUserLeft();
                }
            });
        }
    };

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.private_app_id), mRtcEventHandler);
            setChannelProfile();
        } catch (Exception e) {
            Log.e("initEngine", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setChannelProfile() {
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        joinChannel();
    }

    private void setupLocalVideo() {

        // Enable the video module.
        mRtcEngine.enableVideo();

        // Create a SurfaceView object.
        FrameLayout mLocalContainer = findViewById(R.id.liveFrame);
        SurfaceView mLocalView;

        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Set the local video view.
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(localVideoCanvas);

    }

    private void setupRemoteVideo(int uid) {
        Log.d("videoSetup", "start");
        Toast.makeText(this, uid + "", Toast.LENGTH_SHORT).show();
        // Create a SurfaceView object.
        FrameLayout mRemoteContainer = findViewById(R.id.liveFrame);
        SurfaceView mRemoteView;

        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteView.setZOrderMediaOverlay(true);
        mRemoteContainer.addView(mRemoteView);
        // Set the remote video view.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_FILL, uid));

    }

    private void joinChannel() {
        Log.d("ChannelJoin", "start");
        // For SDKs earlier than v3.0.0, call this method to enable interoperability between the Native SDK and the Web SDK if the Web SDK is in the channel. As of v3.0.0, the Native SDK enables the interoperability with the Web SDK by default.
        mRtcEngine.enableWebSdkInteroperability(true);
        // Join a channel with a token.
        String mRoomName = Variables.user_id;
        mRtcEngine.joinChannel(null, id, "Extra Data", 0);
        setupLocalVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}