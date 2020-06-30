package com.android.buggee.Video_Recording;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.Comments.Comment_F;
import com.android.buggee.Comments.LiveCommentAdapter;
import com.android.buggee.Comments.LiveCommentData;
import com.android.buggee.Main_Menu.MainMenuActivity;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


public class LiveBroadcasterActivity extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID = 22;
    private RtcEngine mRtcEngine;
    IOSDialog iosDialog;
    DatabaseReference liveRef;

    private LiveCommentAdapter liveCommentAdapter;
    private ArrayList<LiveCommentData> liveCommentData = new ArrayList<>();
    private RecyclerView liveRecycler;
    // Ask for Android device permissions at runtime.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_broadcast);
        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            Log.d("init", "start");
            initializeEngine();
        }
        liveRecycler = findViewById(R.id.liveRecycler);
        final String id = Variables.user_id.replace(".", "");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        liveRecycler.setLayoutManager(layoutManager);
        liveRecycler.setHasFixedSize(false);
        liveCommentAdapter = new LiveCommentAdapter(this, liveCommentData);
        liveRecycler.setAdapter(liveCommentAdapter);
        liveRef = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);


        liveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                liveCommentData.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {

                        LiveCommentData temp = dataSnapshot1.getValue(LiveCommentData.class);
                        if (temp != null) {
                            Log.d("comment", temp.getComment_text());
                            liveCommentData.add(temp);
                            liveCommentAdapter.notifyDataSetChanged();
                            liveRecycler.scrollToPosition(liveCommentData.size() - 1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Button endLive = findViewById(R.id.endLive);
        TextView name = findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("liveName"));
        TextView details = findViewById(R.id.details);
        details.setText(getIntent().getStringExtra("liveDetails"));
        TextView blackScreen = findViewById(R.id.blackScreen);
        blackScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        endLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishLive();
            }
        });
    }

    private void finishLive() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        iosDialog.show();
        ApiRequest.Call_Api(LiveBroadcasterActivity.this, Variables.deleteLive, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                iosDialog.cancel();
                Log.d("liveResponse", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        liveRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.live_file), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear().apply();
                                    mRtcEngine.leaveChannel();
                                    RtcEngine.destroy();
                                    mRtcEngine = null;
                                    finish();
                                    startActivity(new Intent(LiveBroadcasterActivity.this, MainMenuActivity.class));
                                } else {
                                    Toast.makeText(LiveBroadcasterActivity.this, "Check Your Network!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(LiveBroadcasterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


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
                    Log.i("agora", "First remote video decoded, uid: " + uid);
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
                    // mRtcEngine.leaveChannel();
                    Log.i("agora", "User offline, uid: " + uid);
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
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        setupLocalVideo();
        joinChannel();
    }

    private void setupLocalVideo() {
        Log.d("videoSetup", "start");
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

    private void joinChannel() {
        Log.d("ChannelJoin", "start");
        // For SDKs earlier than v3.0.0, call this method to enable interoperability between the Native SDK and the Web SDK if the Web SDK is in the channel. As of v3.0.0, the Native SDK enables the interoperability with the Web SDK by default.
        mRtcEngine.enableWebSdkInteroperability(true);
        // Join a channel with a token.
        String mRoomName = Variables.user_id;
        mRtcEngine.joinChannel(null, Variables.user_id, "Extra Data", 0);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null)
            mRtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    @Override
    public void finish() {
        super.finish();
        if (mRtcEngine != null)
            mRtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }


}