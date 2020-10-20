package com.android.buggee.Home;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.android.buggee.Accounts.PageActivity;
import com.android.buggee.Accounts.PageProfileDialog;
import com.android.buggee.Comments.LiveCommentAdapter;
import com.android.buggee.Comments.LiveCommentData;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.GlWatermarkFilterCustom;
import com.android.buggee.SimpleClasses.WebViewActivity;
import com.android.buggee.SoundLists.VideoSound_A;
import com.android.buggee.Video_Recording.StoryModeChooser;
import com.android.buggee.Video_Recording.Video_Recoder_A;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.egl.filter.GlWatermarkFilter;
import com.android.buggee.Comments.Comment_F;
import com.android.buggee.Main_Menu.MainMenuActivity;
import com.android.buggee.Main_Menu.MainMenuFragment;
import com.android.buggee.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.android.buggee.Profile.Profile_F;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.API_CallBack;
import com.android.buggee.SimpleClasses.Fragment_Callback;
import com.android.buggee.SimpleClasses.Fragment_Data_Send;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.Taged.Taged_Videos_F;
import com.android.buggee.VideoAction.VideoAction_F;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class Home_F extends RootFragment implements Player.EventListener, Fragment_Data_Send {

    View view;
    Context context;
    private RtcEngine mRtcEngine;
    ImageView addStoryButton;
    RecyclerView recyclerView, liveRecycler, liveComentRecycler;
    ArrayList<Home_Get_Set> data_list = new ArrayList<>();
    ArrayList<Home_Get_Set> stories = new ArrayList<>();
    ArrayList<ImageStoryData> imageStory = new ArrayList<>();
    ArrayList<LiveData> liveData = new ArrayList<>();
    int currentPage = -1, liveCurrentPage = -1;
    LinearLayoutManager layoutManager, liveLayoutManager;
    LiveAdapter liveAdapter;
    //  ProgressBar p_bar, live_progress;
    LottieAnimationView p_bar, live_progress;
    TextView storiesT;
    TextView noItem;
    SwipeRefreshLayout swiperefresh, liveSwipeRefresh;
    int active = 1;
    private TabLayout homeTab;
    boolean is_user_stop_video = false;
    EditText message_edit;
    ImageButton send_btn;
    ProgressBar send_progress;
    CardView liveComment;
    RecyclerView storyRecycler;
    StoryAdapter storyAdapter;

    public Home_F() {
        // Required empty public constructor
    }

    int swipe_count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {

            view = inflater.inflate(R.layout.fragment_home, container, false);
            context = getContext();

            initializeEngine();
            addStoryButton = view.findViewById(R.id.addStoryButton);
            addStoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                        StoryModeChooser storyModeChooser = new StoryModeChooser();
                        storyModeChooser.show(getChildFragmentManager(), "story_chooser");

                    } else {
                        Functions.showToast(getActivity(), "You have to login First");
                    }
                }
            });
            p_bar = view.findViewById(R.id.video_loader);
            live_progress = view.findViewById(R.id.video_loader);
            storiesT = view.findViewById(R.id.stories);
            liveComment = view.findViewById(R.id.liveComment);
            storyRecycler = view.findViewById(R.id.storyRecycler);
            LinearLayoutManager storyLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            storyRecycler.setLayoutManager(storyLayout);
            storyAdapter = new StoryAdapter(context, stories, imageStory);
            storyRecycler.setAdapter(storyAdapter);
            liveComentRecycler = view.findViewById(R.id.liveComentRecycler);
            message_edit = view.findViewById(R.id.message_edit);
            send_btn = view.findViewById(R.id.send_btn);
            send_progress = view.findViewById(R.id.send_progress);
            liveRecycler = view.findViewById(R.id.liveRecycler);
            final LinearLayout swipeDetector = view.findViewById(R.id.swipeDetector);
            ConstraintLayout home = view.findViewById(R.id.home);
            final Animation animRightToLeft = AnimationUtils.loadAnimation(context, R.anim.in_from_right);
            final Animation outAnime = AnimationUtils.loadAnimation(context, R.anim.out_to_right);
            swipeDetector.setOnTouchListener(new OnSwipeTouchListener(context) {
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    liveComment.setVisibility(View.VISIBLE);
                    liveComment.startAnimation(animRightToLeft);
                }
            });

            liveComentRecycler.setOnTouchListener(new OnSwipeTouchListener(context) {
                @Override
                public void onSwipeRight() {
                    super.onSwipeRight();
                    liveComment.setVisibility(View.GONE);
                    liveComment.startAnimation(outAnime);
                }
            });
            noItem = view.findViewById(R.id.noItem);
            homeTab = view.findViewById(R.id.homeTab);
            homeTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                        swiperefresh.setVisibility(View.VISIBLE);
                        liveSwipeRefresh.setVisibility(View.GONE);
                        noItem.setVisibility(View.GONE);
                        liveComment.setVisibility(View.GONE);
                        swipeDetector.setVisibility(View.GONE);
                        active = 1;
                        removePreviousRemoteView();
                    } else if (tab.getPosition() == 2) {
                        swiperefresh.setVisibility(View.GONE);
                        liveSwipeRefresh.setVisibility(View.VISIBLE);
                        swipeDetector.setVisibility(View.VISIBLE);
                        active = 3;
                        Release_Privious_Player();
                        removePreviousRemoteView();
                        getAllLiveVideo();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                        swiperefresh.setVisibility(View.VISIBLE);
                        liveSwipeRefresh.setVisibility(View.GONE);
                        swipeDetector.setVisibility(View.GONE);
                        noItem.setVisibility(View.GONE);
                        liveComment.setVisibility(View.GONE);
                        active = 1;
                        removePreviousRemoteView();
                    } else if (tab.getPosition() == 2) {
                        swiperefresh.setVisibility(View.GONE);
                        liveSwipeRefresh.setVisibility(View.VISIBLE);
                        swipeDetector.setVisibility(View.VISIBLE);
                        active = 3;
                        Release_Privious_Player();
                        getAllLiveVideo();
                    }

                }
            });
            recyclerView = view.findViewById(R.id.recylerview);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);


            liveLayoutManager = new LinearLayoutManager(context);
            liveRecycler.setLayoutManager(liveLayoutManager);
            liveRecycler.setHasFixedSize(false);

            SnapHelper snapHelper1 = new PagerSnapHelper();
            snapHelper1.attachToRecyclerView(liveRecycler);



            // this is the scroll listener of recycler view which will tell the current item number
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //here we find the current item number
                    final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                    final int height = recyclerView.getHeight();
                    int page_no = scrollOffset / height;

                    if (page_no != currentPage) {
                        currentPage = page_no;

                        Release_Privious_Player();
                        if (data_list.size() > 0) {
                            Set_Player(currentPage);
                        }

                    }
                }
            });


            swiperefresh = view.findViewById(R.id.swiperefresh);
            swiperefresh.setProgressViewOffset(false, 0, 200);

            swiperefresh.setColorSchemeResources(R.color.black);
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    currentPage = -1;
                    Call_Api_For_get_Allvideos();
                    getAllStory();

                }
            });

            liveSwipeRefresh = view.findViewById(R.id.liveSwipeRefresh);
            liveSwipeRefresh.setProgressViewOffset(false, 0, 200);

            liveSwipeRefresh.setColorSchemeResources(R.color.black);
            liveSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    liveCurrentPage = -1;
                    getAllLiveVideo();

                }
            });

//            Call_Api_For_get_Allvideos();
//            getAllStory();
            getAllLiveVideo();
            Load_add();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getAllLiveVideo() {
        live_progress.setVisibility(View.VISIBLE);
        ApiRequest.Call_Api(context, Variables.getAllLive, null, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.d("liveResponse", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);

                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        liveData.clear();
                        liveCurrentPage = -1;
                        JSONArray msgArray = jsonObject.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            LiveData item = new LiveData();
                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            JSONObject video_info = itemdata.optJSONObject("video_info");
                            item.live_id = video_info.optString("live_id");
                            item.username = user_info.optString("username");
                            item.fb_id = user_info.optString("fb_id");
                            item.verified = user_info.optString("verified");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");
                            item.block = user_info.optString("block");
                            item.account_type = user_info.optString("account_type");
                            item.comment_privacy = user_info.optString("comment_privacy");
                            item.live_privacy = user_info.optString("live_privacy");
                            item.live_name = video_info.optString("live_name");
                            item.live_details = video_info.optString("live_details");
                            liveData.add(item);


                        }

                        liveSwipeRefresh.setRefreshing(false);
                        liveAdapter = new LiveAdapter(context, liveData, new LiveAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, LiveData item, View view) {
                                switch (view.getId()) {
                                    case R.id.comment_layout:
                                        openLiveComment(item);
                                        break;
                                    case R.id.user_pic:
                                    case R.id.user_pic2:
                                        OpenLiveProfile(item, true);
                                        break;

                                }
                            }
                        });
                        liveAdapter.setHasStableIds(true);
                        liveRecycler.setAdapter(liveAdapter);
                        liveRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                                final int height = recyclerView.getHeight();
                                int page_no = scrollOffset / height;

                                if (page_no != liveCurrentPage) {
                                    liveCurrentPage = page_no;
                                    removePreviousRemoteView();
                                    Log.d("livePageOnScrolled", liveCurrentPage + "");
                                    joinChannel(liveData.get(liveCurrentPage).live_id, liveCurrentPage);
                                    setupLiveComment(liveData.get(liveCurrentPage).live_id);

                                }

                            }
                        });
                        if (active == 3) {

                            if (liveData.size() > 0) {
                                noItem.setVisibility(View.GONE);

                            } else {
                                live_progress.setVisibility(View.GONE);
                                noItem.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private ArrayList<LiveCommentData> liveCommentData = new ArrayList<>();

    private void setupLiveComment(String live_id) {
        liveComment.setVisibility(View.GONE);
        liveCommentData.clear();
        final String id = live_id.replace(".", "");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        liveComentRecycler.setLayoutManager(layoutManager);
        liveComentRecycler.setHasFixedSize(false);
        final LiveCommentAdapter liveCommentAdapter = new LiveCommentAdapter(context, liveCommentData);
        liveComentRecycler.setAdapter(liveCommentAdapter);
        DatabaseReference liveRef = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);


        liveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                liveCommentData.clear();
                liveComment.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        LiveCommentData temp = dataSnapshot1.getValue(LiveCommentData.class);
                        if (temp != null) {
                            Log.d("comment", temp.getComment_text());
                            liveCommentData.add(temp);
                            liveCommentAdapter.notifyDataSetChanged();
                            liveComentRecycler.scrollToPosition(liveCommentData.size() - 1);
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


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                    String message = message_edit.getText().toString();
                    if (!TextUtils.isEmpty(message)) {
                        send_progress.setVisibility(View.VISIBLE);
                        send_btn.setVisibility(View.GONE);
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);
                        String commentId = databaseReference.push().getKey();
                        final LiveCommentData liveComment = new LiveCommentData(Variables.user_id, message, sharedPreferences.getString(Variables.f_name, null), sharedPreferences.getString(Variables.l_name, null), commentId, Variables.user_pic);
                        databaseReference.child(commentId).setValue(liveComment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                send_progress.setVisibility(View.GONE);
                                send_btn.setVisibility(View.VISIBLE);

                                if (!task.isSuccessful()) {
                                    Functions.showToast(getActivity(), "Failed To Post Comment!");
                                } else {
                                    message_edit.setText("");
                                    recyclerView.scrollToPosition(liveCommentData.size() - 1);
                                }
                            }
                        });
                    }
                } else {
                    Functions.showToast(getActivity(), "You Have To Login!");
                }
            }
        });
    }


    private void openLiveComment(LiveData item) {

        if (!item.comment_privacy.equals("only me") && item.comment_privacy.equals("everyone")) {

            Comment_F comment_f = new Comment_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("video_type", "live");
            args.putString("video_id", item.live_id);
            args.putString("user_id", item.fb_id);
            comment_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, comment_f).commit();
        } else if (item.comment_privacy.equals("friend")) {
            Comment_F comment_f = new Comment_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("video_type", "live");
            args.putString("video_id", item.live_id);
            args.putString("user_id", item.live_id);
            comment_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, comment_f).commit();
        }


    }


    InterstitialAd mInterstitialAd;

    public void Load_add() {

        // this is test app id you will get the actual id when you add app in your
        //add mob account
        MobileAds.initialize(context,
                getResources().getString(R.string.ad_app_id));


        //code for intertial add
        mInterstitialAd = new InterstitialAd(context);

        //here we will get the add id keep in mind above id is app id and below Id is add Id
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.my_Interstitial_Add));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


    }



    boolean is_add_show=false;
    Home_Adapter adapter;
    public void Set_Adapter(){

         adapter=new Home_Adapter(context, data_list, new Home_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, final Home_Get_Set item, View view) {

                switch(view.getId()) {
                    case R.id.smallPlusButton:
                        if (item.account_type.equals("private")) {
                            sentFriendRequest(item);
                        } else {
                            Follow_unFollow_User(item, postion);
                        }
                        break;

                    case R.id.pageVisit:
                        Intent web = new Intent(context, WebViewActivity.class);
                        web.putExtra("url", item.url);
                        context.startActivity(web);
                        break;

                    case R.id.user_pic:
                        onPause();
                        OpenProfile(item, false);
                        break;

                    case R.id.username:
                        onPause();
                        OpenProfile(item, false);
                        break;

                    case R.id.like_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                        Like_Video(postion, item);
                        }else {
                            Functions.showToast(getActivity(), "Please Login.");
                        }
                        break;

                    case R.id.comment_layout:
                        OpenComment(item);
                        break;

                    case R.id.shared_layout:
                            if (!Variables.is_secure_info) {
                                final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
                                    @Override
                                    public void Responce(Bundle bundle) {

                                        if (bundle.getString("action").equals("save")) {
                                            Save_Video(item);
                                        } else if (bundle.getString("action").equals("delete")) {
                                            Functions.Show_loader(context, false, false);
                                            Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
                                                @Override
                                                public void ArrayData(ArrayList arrayList) {

                                                }

                                                @Override
                                                public void OnSuccess(String responce) {
                                                    data_list.remove(currentPage);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void OnFail(String responce) {

                                                }
                                            });

                                        }
                                    }
                                });

                                Bundle bundle = new Bundle();
                                bundle.putString("video_id", item.video_id);
                                bundle.putString("user_id", item.fb_id);
                                fragment.setArguments(bundle);
                                fragment.show(getChildFragmentManager(), "");
                            }
                        break;


                    case R.id.sound_image_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                            if(check_permissions()) {
                                Intent intent = new Intent(context, VideoSound_A.class);
                                intent.putExtra("data", item);
                                startActivity(intent);
                            }
                        } else {
                            Functions.showToast(getActivity(), "Please Login.");
                        }

                        break;
                }

            }
         });

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }

    private void sentFriendRequest(Home_Get_Set item) {

    }

    private void getAllImageStory() {

        Log.d(Variables.tag, MainMenuActivity.token);

        ApiRequest.Call_Api(context, Variables.getImageStory, null, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Log.d("storyLoadResoponse", resp);
                Parse_data_image_story(resp);
            }
        });
    }

    public void Parse_data_image_story(String responce) {

        imageStory = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    ImageStoryData item = new ImageStoryData();
                    item.url = itemdata.optString("pic_url");
                    item.owner_id = itemdata.optString("uploaded_by");
                    imageStory.add(item);
                    storyAdapter.notifyDataSetChanged();

                }
                storyAdapter = new StoryAdapter(context, stories, imageStory);
                storyRecycler.setAdapter(storyAdapter);
                storyAdapter.notifyDataSetChanged();
                // Set_Adapter();

            } else {
                Functions.showToast(getActivity(), "" + jsonObject.optString("msg"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getAllStory() {


        Log.d(Variables.tag, MainMenuActivity.token);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", MainMenuActivity.token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showAllStory, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Log.d("storyLoadResoponse", resp);
                Parse_data_story(resp);
            }
        });


    }

    public void Parse_data_story(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.fb_id = itemdata.optString("fb_id");
                    item.upload_from = itemdata.optString("upload_from");
                    item.url = itemdata.optString("url");
                    item.page_name = itemdata.optString("page_name");
                    item.page_pic = itemdata.optString("page_pic");

                    JSONObject user_info = itemdata.optJSONObject("user_info");
                    item.account_type = user_info.optString("account_type");
                    item.isFriend = user_info.optBoolean("isFriend");
                    item.message_privacy = user_info.optString("message_privacy");
                    item.comment_privacy = user_info.optString("comment_privacy");
                    item.live_privacy = user_info.optString("live_privacy");
                    item.username = user_info.optString("username");
                    item.verified = user_info.optString("verified");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                    item.last_name = user_info.optString("last_name", "User");
                    item.profile_pic = user_info.optString("profile_pic", "null");

                    JSONObject sound_data = itemdata.optJSONObject("sound");
                    item.sound_id = sound_data.optString("id");
                    item.sound_name = sound_data.optString("sound_name");
                    item.sound_pic = sound_data.optString("thum");

                    JSONObject count = itemdata.optJSONObject("count");
                    item.like_count = count.optString("like_count");
                    item.video_comment_count = count.optString("video_comment_count");
                    item.video_id = itemdata.optString("id");
                    item.liked = itemdata.optString("liked");
                    item.video_url = itemdata.optString("video");
                    item.video_description = itemdata.optString("description");

                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");
                    item.type = "story";
                    stories.add(item);
                    storyAdapter.notifyDataSetChanged();

                }
                getAllImageStory();

                // Set_Adapter();

            } else {
                Functions.showToast(getActivity(), "" + jsonObject.optString("msg"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean alreadyLoading = true;
    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void Call_Api_For_get_Allvideos() {
        alreadyLoading = true;

        Log.d(Variables.tag, MainMenuActivity.token);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token",MainMenuActivity.token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Log.d("videoLoadResoponse", resp);
                Parse_data(resp);
            }
        });



    }

    public void Parse_data(String responce){

        data_list=new ArrayList<>();

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.fb_id = itemdata.optString("fb_id");
                    item.upload_from = itemdata.optString("upload_from");
                    item.url = itemdata.optString("url");
                    item.page_name = itemdata.optString("page_name");
                    item.page_pic = itemdata.optString("page_pic");

                    JSONObject user_info = itemdata.optJSONObject("user_info");
                    item.account_type = user_info.optString("account_type");
                    item.isFriend = user_info.optBoolean("isFriend");
                    item.message_privacy = user_info.optString("message_privacy");
                    item.comment_privacy = user_info.optString("comment_privacy");
                    item.live_privacy = user_info.optString("live_privacy");
                    item.username = user_info.optString("username");
                    item.verified = user_info.optString("verified");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                    item.last_name = user_info.optString("last_name", "User");
                    item.profile_pic = user_info.optString("profile_pic", "null");

                    JSONObject sound_data = itemdata.optJSONObject("sound");
                    item.sound_id = sound_data.optString("id");
                    item.sound_name = sound_data.optString("sound_name");
                    item.sound_pic = sound_data.optString("thum");

                    JSONObject count = itemdata.optJSONObject("count");
                    item.like_count = count.optString("like_count");
                    item.video_comment_count = count.optString("video_comment_count");
                    item.video_id = itemdata.optString("id");
                    item.liked = itemdata.optString("liked");
                    item.video_url = itemdata.optString("video");
                    item.video_description = itemdata.optString("description");
                    if (item.upload_from.equals("page")) {
                        storyRecycler.setVisibility(View.INVISIBLE);
                        addStoryButton.setVisibility(View.INVISIBLE);
                        storiesT.setVisibility(View.INVISIBLE);
                        homeTab.setVisibility(View.INVISIBLE);
                        MainMenuFragment.showHidePlus(false);

                    } else {
                        storyRecycler.setVisibility(View.VISIBLE);
                        addStoryButton.setVisibility(View.VISIBLE);
                        storiesT.setVisibility(View.VISIBLE);
                        homeTab.setVisibility(View.VISIBLE);
                        MainMenuFragment.showHidePlus(true);
                    }
                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");

                    data_list.add(item);
                }

                Set_Adapter();

            }else {
                Functions.showToast(getActivity(), "" + jsonObject.optString("msg"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void Call_Api_For_Singlevideos(final int postion) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token",Variables.sharedPreferences.getString(Variables.device_token,"Null"));
            parameters.put("video_id",data_list.get(postion).video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Singal_Video_Parse_data(postion,resp);
            }
        });


    }

    public void Singal_Video_Parse_data(int pos,String responce){

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++) {
                    try {
                        JSONObject itemdata = msgArray.optJSONObject(i);
                        Home_Get_Set item = new Home_Get_Set();
                        item.fb_id = itemdata.optString("fb_id");
                        item.upload_from = itemdata.optString("upload_from");
                        item.url = itemdata.optString("url");
                        item.page_name = itemdata.optString("page_name");
                        item.page_pic = itemdata.optString("page_pic");
                        item.category = itemdata.optString("category");
                        item.button = itemdata.optString("button");
                        JSONObject user_info = itemdata.optJSONObject("user_info");
                        item.account_type = user_info.optString("account_type");
                        item.isFriend = user_info.optBoolean("isFriend");
                        item.message_privacy = user_info.optString("message_privacy");
                        item.comment_privacy = user_info.optString("comment_privacy");
                        item.live_privacy = user_info.optString("live_privacy");
                        item.username = user_info.optString("username");
                        item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                        item.last_name = user_info.optString("last_name", "User");
                        item.profile_pic = user_info.optString("profile_pic", "null");
                        item.verified = user_info.optString("verified");
                        JSONObject sound_data = itemdata.optJSONObject("sound");
                        item.sound_id = sound_data.optString("id");
                        item.sound_name = sound_data.optString("sound_name");
                        item.sound_pic = sound_data.optString("thum");


                        JSONObject count = itemdata.optJSONObject("count");
                        item.like_count = count.optString("like_count");
                        item.video_comment_count = count.optString("video_comment_count");


                        item.video_id = itemdata.optString("id");
                        item.liked = itemdata.optString("liked");
                        item.video_url = itemdata.optString("video");
                        item.video_description = itemdata.optString("description");

                        item.thum = itemdata.optString("thum");
                        item.created_date = itemdata.optString("created");

                        data_list.remove(pos);
                        data_list.add(pos, item);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



            }else {
                Functions.showToast(getActivity(), "" + jsonObject.optString("msg"));

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }




    // this will call when swipe for another video and
    // this function will set the player to the current video
    public void Set_Player(final int currentPage) {
        Log.d("exoplayerCurrentPage", currentPage + "");
        final Home_Get_Set item = data_list.get(currentPage);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl();
        DefaultLoadControl.Builder builder = new DefaultLoadControl.Builder();
        final int loadControlStartBufferMs = 1500;
        builder.setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, loadControlStartBufferMs, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        /* Build the actual DefaultLoadControl instance */
        DefaultLoadControl loadControl = builder.createDefaultLoadControl();
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(context);
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector, loadControl);
        if (item.upload_from.equals("page")) {
            storyRecycler.setVisibility(View.INVISIBLE);
            addStoryButton.setVisibility(View.INVISIBLE);
            storiesT.setVisibility(View.INVISIBLE);
            homeTab.setVisibility(View.INVISIBLE);
            MainMenuFragment.showHidePlus(false);

        } else {
            storyRecycler.setVisibility(View.VISIBLE);
            addStoryButton.setVisibility(View.VISIBLE);
            storiesT.setVisibility(View.VISIBLE);
            homeTab.setVisibility(View.VISIBLE);
            MainMenuFragment.showHidePlus(true);
        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "TikTok"));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(item.video_url));

        Log.d("resp", item.video_url);


        player.prepare(videoSource);
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        player.addListener(this);


         View layout=layoutManager.findViewByPosition(currentPage);
         final PlayerView playerView=layout.findViewById(R.id.playerview);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setPlayWhenReady(is_visible_to_user);
        privious_player=player;




        final ConstraintLayout mainlayout = layout.findViewById(R.id.mainlayout);
        playerView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                     super.onFling(e1, e2, velocityX, velocityY);
                    float deltaX = e1.getX() - e2.getX();
                    float deltaXAbs = Math.abs(deltaX);
                    // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                    if((deltaXAbs > 100) && (deltaXAbs < 1000)) {
                        if(deltaX > 0)
                        {
                            OpenProfile(item,true);
                        }
                    }


                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);
                    if(!player.getPlayWhenReady()){
                        is_user_stop_video=false;
                        privious_player.setPlayWhenReady(true);
                    }else{
                        is_user_stop_video=true;
                        privious_player.setPlayWhenReady(false);
                    }


                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    Show_video_option(item);

                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if(!player.getPlayWhenReady()){
                        is_user_stop_video=false;
                        privious_player.setPlayWhenReady(true);
                    }


                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                        Show_heart_on_DoubleTap(item, mainlayout, e);
                        Like_Video(currentPage, item);
                    }else {
                        Functions.showToast(getActivity(), "" + "Please Login into app");
                    }
                    return super.onDoubleTap(e);

                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        TextView desc_txt=layout.findViewById(R.id.desc_txt);
        HashTagHelper.Creator.create(context.getResources().getColor(R.color.maincolor), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {

                onPause();
                OpenHashtag(hashTag);

            }
        }).handle(desc_txt);


//        LinearLayout soundimage = (LinearLayout)layout.findViewById(R.id.sound_image_layout);
//        Animation sound_animation = AnimationUtils.loadAnimation(context,R.anim.d_clockwise_rotation);
//        soundimage.startAnimation(sound_animation);

        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
            Functions.Call_Api_For_update_view(getActivity(), item.video_id);


        swipe_count++;
        if (swipe_count > 4) {
            Show_add();
            swipe_count = 0;
        }



        Call_Api_For_Singlevideos(currentPage);

    }


    public void Show_heart_on_DoubleTap(Home_Get_Set item,final ConstraintLayout mainlayout,MotionEvent e){

        int x = (int) e.getX()-100;
        int y = (int) e.getY()-100;
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        final ImageView iv = new ImageView(getApplicationContext());
        lp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(lp);
        if(item.liked.equals("1"))
        iv.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_like));
        else
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_like_fill));

        mainlayout.addView(iv);
        Animation fadeoutani = AnimationUtils.loadAnimation(context,R.anim.fade_out);

        fadeoutani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainlayout.removeView(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(fadeoutani);

    }



    public void Show_add(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    @Override
    public void onDataSent(String yourData) {
        int comment_count =Integer.parseInt(yourData);
        Home_Get_Set item=data_list.get(currentPage);
        item.video_comment_count=""+comment_count;
        data_list.remove(currentPage);
        data_list.add(currentPage,item);
        adapter.notifyDataSetChanged();
    }



    // this will call when go to the home tab From other tab.
    // this is very importent when for video play and pause when the focus is changes
    boolean is_visible_to_user;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        is_visible_to_user=isVisibleToUser;

        if(privious_player!=null && (isVisibleToUser && !is_user_stop_video)){
            privious_player.setPlayWhenReady(true);
        }else if(privious_player!=null && !isVisibleToUser){
            privious_player.setPlayWhenReady(false);
        }
    }



   // when we swipe for another video this will relaese the privious player
    SimpleExoPlayer privious_player;
    public void Release_Privious_Player(){
        if(privious_player!=null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }




    // this function will call for like the video and Call an Api for like the video
    public void Like_Video(final int position, final Home_Get_Set home_get_set){
        String action=home_get_set.liked;

        if(action.equals("1")){
            action="0";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) -1);
        }else {
            action="1";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) +1);
        }


        data_list.remove(position);
        home_get_set.liked=action;
        data_list.add(position,home_get_set);
        adapter.notifyDataSetChanged();

        Functions.Call_Api_For_like_video(getActivity(), home_get_set.video_id, action,new API_CallBack() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });

    }



    // this will open the comment screen
    private void OpenComment(Home_Get_Set item) {

        if (!item.comment_privacy.equals("only me") && item.comment_privacy.equals("everyone")) {
            int comment_counnt = Integer.parseInt(item.video_comment_count);

            Fragment_Data_Send fragment_data_send = this;

            Comment_F comment_f = new Comment_F(comment_counnt, fragment_data_send);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("video_type", "recorded");
            args.putString("video_id", item.video_id);
            args.putString("user_id", item.fb_id);
            comment_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, comment_f).commit();
        } else if (item.comment_privacy.equals("friend")) {
            if (item.isFriend) {
                int comment_counnt = Integer.parseInt(item.video_comment_count);

                Fragment_Data_Send fragment_data_send = this;

                Comment_F comment_f = new Comment_F(comment_counnt, fragment_data_send);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
                Bundle args = new Bundle();
                args.putString("video_type", "recorded");
                args.putString("video_id", item.video_id);
                args.putString("user_id", item.fb_id);
                comment_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, comment_f).commit();
            } else {
                Functions.showToast(getActivity(), "You cannot view comment!");
            }
        }


    }

    private void OpenLiveProfile(LiveData item, boolean from_right_to_left) {
        if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.fb_id)) {
//            TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(4);
//            profile.select();

        } else {
            Profile_F profile_f = new Profile_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if (from_right_to_left)
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            else
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

            Bundle args = new Bundle();

            args.putString("user_id", item.fb_id);
            args.putBoolean("isFriend", true);
            args.putString("account_type", item.account_type);
            args.putString("message_privacy", item.account_type);
            args.putString("comment_privacy", item.comment_privacy);
            args.putString("live_privacy", item.live_privacy);
            args.putString("user_name", item.first_name + " " + item.last_name);
            args.putString("user_pic", item.profile_pic);
            args.putString("verified", item.verified);
            profile_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, profile_f).commit();
        }

    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Home_Get_Set item,boolean from_right_to_left) {
        if (item.upload_from.equals("profile")) {
            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.fb_id)) {
//            TabLayout.Tab profile= MainMenuFragment.tabLayout.getTabAt(4);
//            profile.select();

            } else {
                Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        Call_Api_For_Singlevideos(currentPage);
                    }
                });
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                if (from_right_to_left)
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                else
                    transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

                Bundle args = new Bundle();

                args.putString("user_id", item.fb_id);
                args.putBoolean("isFriend", item.isFriend);
                args.putString("account_type", item.account_type);
                args.putString("message_privacy", item.account_type);
                args.putString("comment_privacy", item.comment_privacy);
                args.putString("live_privacy", item.live_privacy);
                args.putString("user_name", item.first_name + " " + item.last_name);
                args.putString("user_pic", item.profile_pic);
                args.putString("verified", item.verified);
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, profile_f).commit();
            }
        } else {

            Intent intent = new Intent(context, PageActivity.class);
            intent.putExtra("id", item.fb_id);
            context.startActivity(intent);
        }

    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenHashtag(String tag) {

            Taged_Videos_F taged_videos_f = new Taged_Videos_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("tag", tag);
            taged_videos_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, taged_videos_f).commit();


    }



    private void Show_video_option(final Home_Get_Set home_get_set) {

        final CharSequence[] options = {"Save Video", "Report Video", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Save Video")) {
                    if (Functions.Checkstoragepermision(getActivity())) {
                        Save_Video(home_get_set);
                    }
                } else if (options[item].equals("Report Video")) {
                    //dialog.dismiss();
                    reportVideo(home_get_set);
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    private void reportVideo(final Home_Get_Set home_get_set) {
        new AlertDialog.Builder(context)
                .setTitle("Are You Sure?")
                .setMessage("Action May Take Within 24 Hour!")
                .setPositiveButton("Report!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JSONObject parameters = new JSONObject();
                        try {
                            parameters.put("content_id", home_get_set.video_id);
                            parameters.put("reported_by", Variables.sharedPreferences.getString(Variables.u_id, ""));
                            parameters.put("type", "video");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Functions.Show_loader(context, false, false);
                        ApiRequest.Call_Api(context, Variables.report, parameters, new Callback() {
                            @Override
                            public void Responce(String resp) {
                                Functions.cancel_loader();
                                try {
                                    JSONObject jsonObject = new JSONObject(resp);
                                    boolean success = jsonObject.optBoolean("success");
                                    if (success) {
                                        Functions.showToast(getActivity(), "Reported!");
                                    } else {
                                        String message = jsonObject.optString("msg");
                                        Functions.showToast(getActivity(), message);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }

    public void Save_Video(final Home_Get_Set item) {
        privious_player.setPlayWhenReady(false);
        Functions.Show_determinent_loader(context, false, false);
        PRDownloader.initialize(getActivity().getApplicationContext());
        DownloadRequest prDownloader = PRDownloader.download(item.video_url, Environment.getExternalStorageDirectory() + "/Buggee/", item.video_id + "no_watermark" + ".mp4")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        int prog=(int)((progress.currentBytes*100)/progress.totalBytes);
                        Functions.Show_loading_progress(prog/2);

                    }
                });


              prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Functions.cancel_determinent_loader();
                    Applywatermark(item);

                }

                @Override
                public void onError(Error error) {
                    Delete_file_no_watermark(item);
                    Functions.showToast(getActivity(), "Error");
                    Functions.cancel_determinent_loader();
                }


            });




    }

    public void Applywatermark(final Home_Get_Set item) {

        Bitmap myLogo = getBitmapFromVectorDrawable(context, R.drawable.ic_buggee_watermark);
        Bitmap bitmap_resize = Bitmap.createScaledBitmap(myLogo, 337, 78, false);
        GlWatermarkFilterCustom filter = new GlWatermarkFilterCustom(bitmap_resize, GlWatermarkFilter.Position.RIGHT_BOTTOM);
        new GPUMp4Composer(Environment.getExternalStorageDirectory() + "/Buggee/" + item.video_id + "no_watermark" + ".mp4",
                Environment.getExternalStorageDirectory() + "/Buggee/" + item.video_id + ".mp4")
                .filter(filter)
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d("resp", "" + (int) (progress * 100));
                        Functions.Show_loading_progress((int) ((progress * 100) / 2) + 50);

                    }

                    @Override
                    public void onCompleted() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Functions.cancel_determinent_loader();
                                Delete_file_no_watermark(item);


                            }
                        });


                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp",exception.toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Delete_file_no_watermark(item);
                                    Functions.cancel_determinent_loader();
                                    Functions.showToast(getActivity(), "Try Again");

                                }catch (Exception e){
                                    Functions.showToast(getActivity(), e.getLocalizedMessage());
                                }
                            }
                        });

                    }
                })
                .start();
    }


    public void Delete_file_no_watermark(Home_Get_Set item){
        Functions.showToast(getActivity(), "Video Saved!");
        File file = new File(Environment.getExternalStorageDirectory() + "/Buggee/" + item.video_id + "no_watermark" + ".mp4");
        if(file.exists()){
            file.delete();
        }
        Scan_file(item);
    }

    public void Scan_file(Home_Get_Set item){
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{Environment.getExternalStorageDirectory() + "/Buggee/" + item.video_id + ".mp4"},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }



    public boolean is_fragment_exits(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(fm.getBackStackEntryCount()==0){
            return false;
        }else {
            return true;
        }

    }

    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onResume() {
        super.onResume();
        if ((privious_player != null && (is_visible_to_user && !is_user_stop_video)) && !is_fragment_exits()) {
            privious_player.setPlayWhenReady(true);
        }
        currentPage = -1;
        Call_Api_For_get_Allvideos();
        getAllStory();

    }


    @Override
    public void onPause() {
        super.onPause();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (privious_player != null) {
            privious_player.release();
        }
        if (mRtcEngine != null) {
            try {
                mRtcEngine.leaveChannel();
                RtcEngine.destroy();
                mRtcEngine = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



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





    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }


    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if(playbackState==Player.STATE_BUFFERING){
            p_bar.setVisibility(View.VISIBLE);
        }
        else if (playbackState == Player.STATE_READY) {
            p_bar.setVisibility(View.GONE);
        } else if (playbackState == Player.STATE_ENDED) {

            if (data_list.size() > currentPage + 1) {
                Log.d("play ended", "scrolled");
                if (currentPage < 0) {
                    currentPage = 0;
                }
                recyclerView.scrollToPosition(currentPage + 1);
                // Set_Player(currentPage+1);
            }
        }


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {

    }


    String follow_status = "0";

    public void Follow_unFollow_User(Home_Get_Set home_get_set, final int position) {

        final String send_status;
        if (follow_status.equals("0")) {
            send_status = "1";
        } else {
            send_status = "0";
        }

        Functions.Call_Api_For_Follow_or_unFollow(getActivity(),
                Variables.sharedPreferences.getString(Variables.u_id, ""),
                home_get_set.fb_id,
                send_status,
                new API_CallBack() {
                    @Override
                    public void ArrayData(ArrayList arrayList) {


                    }

                    @Override
                    public void OnSuccess(String responce) {

                        if (send_status.equals("1")) {
                            data_list.get(position).followed = 1;
                        } else if (send_status.equals("0")) {
                            data_list.get(position).followed = 0;
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void OnFail(String responce) {

                    }

                });


    }
    //Live Video Section

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            activity.runOnUiThread(new Runnable() {
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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    live_progress.setVisibility(View.GONE);
                    Log.i("agora", "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    //setupRemoteVideo(uid);
                    if (liveCurrentPage < 0) {
                        setupRemoteVideo(uid, 0);
                    } else {
                        setupRemoteVideo(uid, liveCurrentPage);
                    }
                }
            });
        }

        @Override
        // Listen for the onUserOffline callback.
        // This callback occurs when the broadcaster leaves the channel or drops offline.
        public void onUserOffline(final int uid, int reason) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Functions.showToast(getActivity(), "Broadcaster Gone Offline!");
                    //  mRtcEngine.leaveChannel();
                    Log.i("agora", "User offline, uid: " + (uid & 0xFFFFFFFFL));
                    getAllLiveVideo();
                    //onRemoteUserLeft();
                }
            });
        }
    };

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(activity.getBaseContext(), getString(R.string.private_app_id), mRtcEventHandler);
            setChannelProfile();
        } catch (Exception e) {
            Log.e("initEngine", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setChannelProfile() {
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        //joinChannel();
    }

    private void setupLocalVideo(final int currentPage) {
        Log.d("liveCurrentPage", currentPage + "");
        // Enable the video module.
        mRtcEngine.enableVideo();
        View layout = liveLayoutManager.findViewByPosition(currentPage);
        // Create a SurfaceView object.
        RecyclerView.ViewHolder vie = liveRecycler.findViewHolderForAdapterPosition(currentPage);
        FrameLayout mLocalContainer = layout.findViewById(R.id.videoholder);
        SurfaceView mLocalView;
        localFrame = mLocalContainer;
        mLocalView = RtcEngine.CreateRendererView(activity.getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Set the local video view.
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);

        mRtcEngine.setupLocalVideo(localVideoCanvas);

    }

    private void removePreviousRemoteView() {
        if (previousFrame != null) {
            try {
                mRtcEngine.leaveChannel();
                previousFrame.removeAllViews();
                localFrame.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private FrameLayout previousFrame, localFrame;

    private void setupRemoteVideo(int uid, final int currentPage) {
        Log.d("videoSetup", "start");
        //Toast.makeText(context, uid + "", Toast.LENGTH_SHORT).show();

        // Create a SurfaceView object.
        View layout = liveLayoutManager.findViewByPosition(currentPage);
        FrameLayout mRemoteContainer = layout.findViewById(R.id.videoholder);
        SurfaceView mRemoteView;

        mRemoteView = RtcEngine.CreateRendererView(activity.getBaseContext());
        mRemoteView.setZOrderMediaOverlay(true);
        mRemoteContainer.addView(mRemoteView);
        previousFrame = mRemoteContainer;

        // Set the remote video view.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_FILL, uid));

    }

    private void joinChannel(String liveId, int currentPage) {
        mRtcEngine.enableVideo();
        Log.d("ChannelJoin", "start");
        // For SDKs earlier than v3.0.0, call this method to enable interoperability between the Native SDK and the Web SDK if the Web SDK is in the channel. As of v3.0.0, the Native SDK enables the interoperability with the Web SDK by default.
        mRtcEngine.enableWebSdkInteroperability(true);
        // Join a channel with a token.
        String mRoomName = Variables.user_id;
        mRtcEngine.joinChannel(null, liveId, "Extra Data", 0);
        setupLocalVideo(currentPage);
    }

    private Activity activity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 1;
            private static final int SWIPE_VELOCITY_THRESHOLD = 1;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
