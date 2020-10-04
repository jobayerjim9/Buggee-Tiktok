package com.android.buggee.Video_Recording;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.Home.Home_F;
import com.android.buggee.SimpleClasses.FileUtils;
import com.android.buggee.Video_Recording.GallerySelectedVideo.GallerySelectedVideo_A;
import com.android.buggee.Video_Recording.InfiniteScroller.InfiniteAdapter;
import com.android.buggee.customAudioViews.AudioTrimmerActivity;
import com.coremedia.iso.boxes.Container;
import com.android.buggee.R;
import com.android.buggee.SegmentProgress.ProgressBarListener;
import com.android.buggee.SegmentProgress.SegmentedProgressBar;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.SoundLists.SoundList_Main_A;
import com.android.buggee.Video_Recording.GalleryVideos.GalleryVideos_A;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.CameraResolutionPreset;
import ai.deepar.ar.DeepAR;

public class Video_Recoder_A extends AppCompatActivity implements View.OnClickListener, AREventListener, SurfaceHolder.Callback {

    TextView sec15, sec30, sec60;
    // CameraView cameraView;
    private CameraGrabber cameraGrabber;
    private int defaultCameraDevice = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int cameraDevice = defaultCameraDevice;
    private int screenOrientation;
    private int currentMask = 0;
    private int currentEffect = 0;
    private int currentFilter = 0;
    ArrayList<String> masks;
    ArrayList<String> effects;
    ArrayList<String> filters;
    private int activeFilterType = 0;
    int number = 0;
    Animation outAnime, inAnime;
    ArrayList<String> videopaths = new ArrayList<>();
    private LinearLayout effectLayout;
    private TextView effectHeading;

    ImageButton record_image;

    boolean is_recording = false;
    boolean is_flash_on = false;

    ImageButton flash_btn;

    SegmentedProgressBar video_progress;

    LinearLayout camera_options;

    ImageButton rotate_camera;

    public static int Sounds_list_Request_code = 50;
    TextView add_sound_txt;
    DeepAR deepAR;
    TextView textMasks, textEffects, textFilters, filterNameTv;
    int sec_passed = 0;
    ImageView audioTrim;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hide_navigation();
        deepAR = new DeepAR(this);
        deepAR.setLicenseKey(getString(R.string.deep_ar_key));
        deepAR.initialize(this, this);
        setContentView(R.layout.activity_video_recoder);
        setupCamera();
        View upDivider = findViewById(R.id.upDivider);
        View downDivider = findViewById(R.id.downDivider);
        textMasks = findViewById(R.id.textMasks);
        textEffects = findViewById(R.id.textEffects);
        textFilters = findViewById(R.id.textFilters);
        audioTrim = findViewById(R.id.audioTrim);
        effectHeading = findViewById(R.id.effectHeading);
        effectLayout = findViewById(R.id.effectLayout);
        filterNameTv = findViewById(R.id.filterName);
        outAnime = AnimationUtils.loadAnimation(this, R.anim.out_to_left);
        inAnime = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
        type = getIntent().getStringExtra("type");
        Variables.recordType = type;
        LinearLayout secLayout = findViewById(R.id.secLayout);
        if (type.equals("story")) {
            secLayout.setVisibility(View.GONE);
            upDivider.setVisibility(View.GONE);
            downDivider.setVisibility(View.GONE);
            Variables.recording_duration = 16000;
            Variables.max_recording_duration = 16000;
            initlize_Video_progress();
        }
        audioTrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Video_Recoder_A.this, AudioTrimmerActivity.class), 1);
            }
        });
        Variables.Selected_sound_id = "null";
        Variables.recording_duration = Variables.max_recording_duration;

        camera_options = findViewById(R.id.camera_options);
        // cameraView = findViewById(R.id.camera);
        sec15 = findViewById(R.id.sec15);
        sec30 = findViewById(R.id.sec30);
        sec60 = findViewById(R.id.sec60);
        final Typeface regular = ResourcesCompat.getFont(this, R.font.avenir_regular);
        final Typeface bold = ResourcesCompat.getFont(this, R.font.avenir_bold);
        sec15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_recording) {
                    Variables.recording_duration = 15000;
                    Variables.max_recording_duration = 15000;
                    initlize_Video_progress();
                    sec15.setTextColor(getResources().getColor(R.color.white));
                    sec15.setTypeface(bold);
                    sec30.setTypeface(regular);
                    sec30.setTextColor(getResources().getColor(R.color.gray));
                    sec60.setTypeface(regular);
                    sec60.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    Toast.makeText(Video_Recoder_A.this, "Already Recording!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        sec30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_recording) {
                    Variables.recording_duration = 30000;
                    Variables.max_recording_duration = 30000;
                    initlize_Video_progress();
                    sec30.setTextColor(getResources().getColor(R.color.white));
                    sec30.setTypeface(bold);
                    sec15.setTypeface(regular);
                    sec15.setTextColor(getResources().getColor(R.color.gray));
                    sec60.setTypeface(regular);
                    sec60.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    Toast.makeText(Video_Recoder_A.this, "Already Recording!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sec60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_recording) {
                    Variables.recording_duration = 60000;
                    Variables.max_recording_duration = 60000;
                    initlize_Video_progress();
                    sec60.setTextColor(getResources().getColor(R.color.white));
                    sec60.setTypeface(bold);
                    sec30.setTypeface(regular);
                    sec30.setTextColor(getResources().getColor(R.color.gray));
                    sec15.setTypeface(regular);
                    sec15.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    Toast.makeText(Video_Recoder_A.this, "Already Recording!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        final ArrayList<String> arrayList=new ArrayList<>();
//        arrayList.add("15s");
//        arrayList.add("30s");
//        arrayList.add("60s");
//        RecyclerView recyclerView=findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        final InfiniteAdapter infiniteAdapter=new InfiniteAdapter(this,arrayList);
//        recyclerView.setAdapter(infiniteAdapter);
//        camera_options = findViewById(R.id.camera_options);
//        PagerSnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
//        recyclerView.scrollToPosition(3);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE){
//                    infiniteAdapter.notifyDataSetChanged();
//                    int position=linearLayoutManager.findFirstVisibleItemPosition()%arrayList.size();
//                    if (position==0) {
//                        Variables.recording_duration = 30000;
//                        Variables.max_recording_duration = 30000;
//                        initlize_Video_progress();
//                    }
//                    else if (position==1) {
//                        Variables.recording_duration = 60000;
//                        Variables.max_recording_duration = 60000;
//                        initlize_Video_progress();
//                    }
//                    else if (position==2) {
//
//                        Variables.recording_duration = 15000;
//                        Variables.max_recording_duration = 15000;
//                        initlize_Video_progress();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//            }
//        });
//        cameraView.addCameraKitListener(new CameraKitEventListener() {
//            @Override
//            public void onEvent(CameraKitEvent cameraKitEvent) {
//            }
//
//            @Override
//            public void onError(CameraKitError cameraKitError) {
//            }
//
//            @Override
//            public void onImage(CameraKitImage cameraKitImage) {
//            }
//
//            @Override
//            public void onVideo(CameraKitVideo cameraKitVideo) {
//
//            }
//        });


        record_image = findViewById(R.id.record_image);


        findViewById(R.id.upload_layout).setOnClickListener(this);





        rotate_camera=findViewById(R.id.rotate_camera);
        rotate_camera.setOnClickListener(this);
        flash_btn = findViewById(R.id.flash_camera);
        flash_btn.setOnClickListener(this);

        findViewById(R.id.Goback).setOnClickListener(this);

        add_sound_txt = findViewById(R.id.add_sound_txt);
        add_sound_txt.setOnClickListener(this);


        Intent intent = getIntent();
        if (intent.hasExtra("sound_name")) {
            add_sound_txt.setText(intent.getStringExtra("sound_name"));
            Variables.Selected_sound_id = intent.getStringExtra("sound_id");
            PreparedAudio();
        }


        // this is code hold to record the video
//        final Timer[] timer = {new Timer()};
//        final long[] press_time = {0};

//        record_image.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                    timer[0] =new Timer();
//                    press_time[0] =System.currentTimeMillis();
//
//                    timer[0].schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(!is_recording) {
//                                        press_time[0] =System.currentTimeMillis();
//                                        Start_or_Stop_Recording();
//                                    }
//                                }
//                            });
//
//                        }
//                    }, 200);
//
//
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    timer[0].cancel();
//                    if(is_recording && (press_time[0] !=0 && (System.currentTimeMillis()- press_time[0])<2000)){
//                        Start_or_Stop_Recording();
//                    }
//                }
//                return false;
//            }
//
//        });
        record_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start_or_Stop_Recording();
            }
        });


        initlize_Video_progress();


    }

    private void initializeFilters() {
        masks = new ArrayList<>();
        masks.add("none");
        masks.add("alien");
        masks.add("aviators");
        masks.add("background_segmentation");
        masks.add("ball_face");
        masks.add("bigmouth");
        masks.add("beard");
        masks.add("beauty");
        masks.add("dalmatian");
        masks.add("flowers");
        masks.add("flower_crown");
        masks.add("fairy_lights");
        masks.add("frankenstein");
        masks.add("hair_segmentation");
        masks.add("koala");
        masks.add("lion");
        masks.add("manly_face");
        masks.add("plastic_ocean");
        masks.add("pumpkin");
        masks.add("scuba");
        masks.add("smallface");
        masks.add("teddycigar");
        // masks.add("kanye");
        masks.add("tripleface");
        masks.add("sleepingmask");
        masks.add("fatify");
        masks.add("obama");
        masks.add("mudmask");
        masks.add("pug");
        masks.add("slash");
        masks.add("tape_face");
        masks.add("tiny_sunglasses");
        masks.add("topology");
        masks.add("twistedface");
        masks.add("grumpycat");


        effects = new ArrayList<>();
        effects.add("none");
        // effects.add("fire");
        effects.add("rain");
        //effects.add("heart");
        effects.add("blizzard");

        filters = new ArrayList<>();
        filters.add("none");
        filters.add("filmcolorperfection");
        filters.add("tv80");
        filters.add("drawingmanga");
        filters.add("sepia");
        filters.add("bleachbypass");
    }

    RadioButton radioMasks, radioEffects, radioFilters;

    private void setupCamera() {
        cameraGrabber = new CameraGrabber(cameraDevice);
        screenOrientation = getScreenOrientation();
        switch (screenOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                cameraGrabber.setScreenOrientation(90);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                cameraGrabber.setScreenOrientation(270);
                break;
            default:
                cameraGrabber.setScreenOrientation(0);
                break;
        }
        cameraGrabber.setResolutionPreset(CameraResolutionPreset.P1280x720);

        final Activity context = this;
        cameraGrabber.initCamera(new CameraGrabberListener() {
            @Override
            public void onCameraInitialized() {
                cameraGrabber.setFrameReceiver(deepAR);
                cameraGrabber.startPreview();
            }

            @Override
            public void onCameraError(String errorMsg) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Camera error");
                builder.setMessage(errorMsg);
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        SurfaceView arCamera = findViewById(R.id.arCamera);
        arCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deepAR.onClick();
            }
        });
        arCamera.getHolder().addCallback(this);
        arCamera.setVisibility(View.GONE);
        arCamera.setVisibility(View.VISIBLE);
        deepAR.setKeyFrameRate(60);
        deepAR.setIFrameInterval(1);
        initializeFilters();
        radioMasks = findViewById(R.id.masks);
        radioEffects = findViewById(R.id.effects);
        radioFilters = findViewById(R.id.filters);
//        ImageButton previousMask = findViewById(R.id.previousMask);
//        ImageButton nextMask = findViewById(R.id.nextMask);

//        previousMask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoPrevious();
//            }
//        });
        arCamera.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (!is_recording) {
                    gotoNext();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (!is_recording) {
                    gotoPrevious();
                }
            }
        });
//        nextMask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoNext();
//            }
//        });

        radioMasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_recording) {
                    radioEffects.setChecked(false);
                    radioFilters.setChecked(false);
                    activeFilterType = 0;
                } else if (activeFilterType != 0) {
                    radioMasks.setChecked(false);
                }
            }
        });
        radioEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_recording) {
                    radioMasks.setChecked(false);
                    radioFilters.setChecked(false);
                    activeFilterType = 1;
                } else if (activeFilterType != 1) {
                    radioEffects.setChecked(false);
                }
            }
        });
        radioFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_recording) {
                    radioEffects.setChecked(false);
                    radioMasks.setChecked(false);
                    activeFilterType = 2;
                } else if (activeFilterType != 2) {
                    radioFilters.setChecked(false);
                }
            }
        });

    }

    String filterName;

    private void gotoNext() {
        if (activeFilterType == 0) {
            currentMask = (currentMask + 1) % masks.size();
            filterName = masks.get(currentMask).toUpperCase();
            deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
            // textMasks.setText(filterName);
            textMasks.setText(masks.get(currentMask).toUpperCase());
            if (masks.get(currentMask).toUpperCase().equals("NONE")) {
                textMasks.setText("Mask");
            }
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect + 1) % effects.size();
            filterName = effects.get(currentEffect).toUpperCase();
            deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
            // textEffects.setText(filterName);
            textEffects.setText(effects.get(currentEffect).toUpperCase());
            if (effects.get(currentEffect).toUpperCase().equals("NONE")) {
                textEffects.setText("Effect");
            }
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter + 1) % filters.size();
            filterName = filters.get(currentFilter).toUpperCase();
            deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
            // textFilters.setText(filterName);
            textFilters.setText(filters.get(currentFilter).toUpperCase());
            if (filters.get(currentFilter).toUpperCase().equals("NONE")) {
                textFilters.setText("Filter");
            }
        }
        filterNameTv.setText(filterName);
        filterNameTv.setVisibility(View.VISIBLE);
        filterNameTv.setAnimation(inAnime);
        new CountDownTimer(2000, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                filterNameTv.setAnimation(outAnime);
                filterNameTv.setVisibility(View.GONE);
            }
        }.start();

    }

    private void gotoPrevious() {
        if (activeFilterType == 0) {
            currentMask = (currentMask - 1) % masks.size();
            filterName = masks.get(currentMask).toUpperCase();
            deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
            textMasks.setText(masks.get(currentMask).toUpperCase());
            if (masks.get(currentMask).toUpperCase().equals("NONE")) {
                textMasks.setText("Mask");
            }
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect - 1) % effects.size();
            filterName = effects.get(currentEffect).toUpperCase();
            deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
            textEffects.setText(effects.get(currentEffect).toUpperCase());
            if (effects.get(currentEffect).toUpperCase().equals("NONE")) {
                textEffects.setText("Effect");
            }
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter - 1) % filters.size();
            filterName = filters.get(currentFilter).toUpperCase();
            deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
            textFilters.setText(filters.get(currentFilter).toUpperCase());
            if (filters.get(currentFilter).toUpperCase().equals("NONE")) {
                textFilters.setText("Filter");
            }
        }

        filterNameTv.setText(filterName);
        filterNameTv.setVisibility(View.VISIBLE);
        new CountDownTimer(2000, 100) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                filterNameTv.setVisibility(View.GONE);
            }
        }.start();

    }

    private String getFilterPath(String filterName) {
        if (filterName.equals("none")) {
            return null;
        }
        return "file:///android_asset/" + filterName;
    }

    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public void initlize_Video_progress() {
        sec_passed = 0;
        video_progress = findViewById(R.id.video_progress);
        video_progress.enableAutoProgressView(Variables.recording_duration);
        video_progress.setDividerColor(Color.WHITE);
        video_progress.setDividerEnabled(true);
        video_progress.setDividerWidth(4);
        video_progress.setShader(new int[]{getResources().getColor(R.color.text), getResources().getColor(R.color.text), getResources().getColor(R.color.text)});

        video_progress.SetListener(new ProgressBarListener() {
            @Override
            public void TimeinMill(long mills) {
                sec_passed = (int) (mills / 1000);

                if (sec_passed > (Variables.recording_duration / 1000) - 1) {
                    Start_or_Stop_Recording();
                }

            }
        });
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new OnSwipeTouchListener.GestureListener());
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

    // if the Recording is stop then it we start the recording
    // and if the mobile is recording the video then it will stop the recording
    public void Start_or_Stop_Recording(){

        if (!is_recording && sec_passed<(Variables.recording_duration/1000)-1) {
            number = number + 1;

            is_recording = true;
            String path = Variables.app_folder + "myvideo" + (number) + ".mp4";
            videopaths.add(path);
            //cameraView.captureVideo(file);


            if (audio != null) {
                audio.start();
                deepAR.setAudioMute(true);
            } else {
                deepAR.setAudioMute(false);
            }
            if (sec_passed < 50) {
                deepAR.startVideoRecording(path);
            } else {
                deepAR.resumeVideoRecording();
            }
            effectHeading.setVisibility(View.GONE);
            effectLayout.setVisibility(View.GONE);


            video_progress.resume();


            record_image.setImageDrawable(getResources().getDrawable(R.drawable.camera_recording));

            camera_options.setVisibility(View.GONE);
            add_sound_txt.setClickable(false);
            rotate_camera.setVisibility(View.GONE);

        }

        else if (is_recording) {

            is_recording = false;

            video_progress.pause();
            video_progress.addDivider();

            if (audio != null)
                audio.pause();


            //cameraView.stopVideo();
            if (sec_passed < (Variables.recording_duration / 1000) - 1) {
                deepAR.pauseVideoRecording();
            } else {
                effectHeading.setVisibility(View.VISIBLE);
                effectLayout.setVisibility(View.VISIBLE);
                deepAR.stopVideoRecording();
            }


        }

        else if(sec_passed>(Variables.recording_duration/1000)){
            Functions.Show_Alert(this,"Alert","Video only can be a "+(int)Variables.recording_duration/1000+" S");
        }



    }



    // this will apped all the videos parts in one  fullvideo
    private boolean append() {
       final ProgressDialog progressDialog=new ProgressDialog(Video_Recoder_A.this);
        new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    public void run() {

                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();
                    }
                });

                ArrayList<String> video_list=new ArrayList<>();
                for (int i=0;i<videopaths.size();i++){

                    File file=new File(videopaths.get(i));
                    if(file.exists()) {
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(Video_Recoder_A.this, Uri.fromFile(file));
                            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                            boolean isVideo = "yes".equals(hasVideo);

                            if (isVideo && file.length() > 3000) {
                                Log.d("resp", videopaths.get(i));
                                video_list.add(videopaths.get(i));
                            }
                        } catch (Exception e) {
                            Log.d(Variables.tag, e.toString());
                        }
                    }
                }



                try {

                    Movie[] inMovies = new Movie[video_list.size()];

                    for (int i=0;i<video_list.size();i++){

                        inMovies[i]= MovieCreator.build(video_list.get(i));
                    }


                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }
                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }

                    Container out = new DefaultMp4Builder().build(result);

                    String outputFilePath=null;
                            if(audio!=null){
                                outputFilePath=Variables.outputfile;
                            }else {
                                outputFilePath=Variables.outputfile2;
                            }

                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                            if(audio!=null)
                              Merge_withAudio();
                            else {
                                Go_To_preview_Activity();
                            }

                        }
                    });



                } catch (Exception e) {

                }
            }
        }).start();



        return true;
    }



    // this will add the select audio with the video
    public void Merge_withAudio(){


        String audio_file;
        audio_file = Variables.app_folder + Variables.SelectedAudio_AAC;


        Merge_Video_Audio merge_video_audio=new Merge_Video_Audio(Video_Recoder_A.this);
        merge_video_audio.doInBackground(audio_file, Variables.outputfile, Variables.outputfile2);

    }




    public void RotateCamera() {
        // cameraView.toggleFacing();
        cameraDevice = cameraGrabber.getCurrCameraDevice() == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
        cameraGrabber.changeCameraDevice(cameraDevice);
    }



    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rotate_camera:
                RotateCamera();
                break;

            case R.id.upload_layout:
                Pick_video_from_gallery();
//                Intent upload_intent=new Intent(this, GalleryVideos_A.class);
//                startActivity(upload_intent);
//                overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                break;

            case R.id.done:
                deepAR.stopVideoRecording();
                break;

           /* case R.id.record_image:
                Start_or_Stop_Recording();
                break;*/

            case R.id.flash_camera:

                if(is_flash_on){
                    is_flash_on=false;
                    // cameraView.setFlash(0);
                    flash_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));

                }else {
                    is_flash_on=true;
                    // cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
                    flash_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                }

                break;

            case R.id.Goback:
                onBackPressed();
                break;

            case R.id.add_sound_txt:
                Intent intent =new Intent(this,SoundList_Main_A.class);
                startActivityForResult(intent, Sounds_list_Request_code);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;

        }


    }


    public void Pick_video_from_gallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, Variables.Pick_video_from_gallery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Sounds_list_Request_code) {
                if (data != null) {

                    if (data.getStringExtra("isSelected").equals("yes")) {
                        add_sound_txt.setText(data.getStringExtra("sound_name"));
                        Variables.Selected_sound_id = data.getStringExtra("sound_id");
                        PreparedAudio();
                    }

                }

            } else if (requestCode == Variables.Pick_video_from_gallery) {
                Uri uri = data.getData();
                try {
                    File video_file = FileUtils.getFileFromUri(this, uri);

                    if (getfileduration(uri) < 60000) {
                        Chnage_Video_size(video_file.getAbsolutePath(), Variables.gallery_resize_video);

                    } else {
                        try {
                            startTrim(video_file, new File(Variables.gallery_trimed_video), 1000, 16000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (requestCode == 1) {
                Functions.cancel_loader();
                String path = data.getExtras().getString("INTENT_AUDIO_FILE");
                Log.d("trimmedAudio", path);
                File file = new File(Variables.app_folder + Variables.SelectedAudio_AAC);
                if (file.exists()) {
                    audio = new MediaPlayer();
                    try {
                        audio.setDataSource(Variables.app_folder + Variables.SelectedAudio_AAC);
                        audio.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(this, Uri.fromFile(file));
                    String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    final int file_duration = Integer.parseInt(durationStr);

                    if (file_duration < Variables.max_recording_duration) {
                        Variables.recording_duration = file_duration;
                        initlize_Video_progress();
                    }
                    // startActivityForResult(new Intent(Video_Recoder_A.this, AudioTrimmerActivity.class),1);
                }
            }
        }
    }

    public long getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            return file_duration;
        } catch (Exception e) {

        }
        return 0;
    }

    public void Chnage_Video_size(String src_path, String destination_path) {

        Functions.Show_determinent_loader(this, false, false);
        new GPUMp4Composer(src_path, destination_path)
                .size(720, 1280)
//                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .videoBitrate((int) (0.25 * 16 * 540 * 960))
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {

                        Log.d("resp", "" + (int) (progress * 100));
                        Functions.Show_loading_progress((int) (progress * 100));

                    }

                    @Override
                    public void onCompleted() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Functions.cancel_determinent_loader();
                                Intent intent = new Intent(Video_Recoder_A.this, GallerySelectedVideo_A.class);
                                intent.putExtra("video_path", Variables.gallery_resize_video);
                                startActivity(intent);

                            }
                        });


                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp", exception.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Functions.cancel_determinent_loader();

                                    Toast.makeText(Video_Recoder_A.this, "Try Again", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {

                                }
                            }
                        });

                    }
                })
                .start();

    }

    public void startTrim(final File src, final File dst, final int startMs, final int endMs) throws IOException {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {

                    FileDataSourceImpl file = new FileDataSourceImpl(src);
                    Movie movie = MovieCreator.build(file);
                    List<Track> tracks = movie.getTracks();
                    movie.setTracks(new LinkedList<Track>());
                    double startTime = startMs / 1000;
                    double endTime = endMs / 1000;
                    boolean timeCorrected = false;

                    for (Track track : tracks) {
                        if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                            if (timeCorrected) {
                                throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                            }
                            startTime = Functions.correctTimeToSyncSample(track, startTime, false);
                            endTime = Functions.correctTimeToSyncSample(track, endTime, true);
                            timeCorrected = true;
                        }
                    }
                    for (Track track : tracks) {
                        long currentSample = 0;
                        double currentTime = 0;
                        long startSample = -1;
                        long endSample = -1;

                        for (int i = 0; i < track.getSampleDurations().length; i++) {
                            if (currentTime <= startTime) {
                                startSample = currentSample;
                            }
                            if (currentTime <= endTime) {
                                endSample = currentSample;
                            } else {
                                break;
                            }
                            currentTime += (double) track.getSampleDurations()[i] / (double) track.getTrackMetaData().getTimescale();
                            currentSample++;
                        }
                        movie.addTrack(new CroppedTrack(track, startSample, endSample));
                    }

                    Container out = new DefaultMp4Builder().build(movie);
                    MovieHeaderBox mvhd = Path.getPath(out, "moov/mvhd");
                    mvhd.setMatrix(Matrix.ROTATE_180);
                    if (!dst.exists()) {
                        dst.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(dst);
                    WritableByteChannel fc = fos.getChannel();
                    try {
                        out.writeContainer(fc);
                    } finally {
                        fc.close();
                        fos.close();
                        file.close();
                    }

                    file.close();
                    return "Ok";
                } catch (IOException e) {
                    Log.d(Variables.tag, e.toString());
                    return "error";
                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Functions.Show_indeterminent_loader(Video_Recoder_A.this, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.equals("error")) {
                    Toast.makeText(Video_Recoder_A.this, "Try Again", Toast.LENGTH_SHORT).show();
                } else {
                    Functions.cancel_indeterminent_loader();
                    Chnage_Video_size(Variables.gallery_trimed_video, Variables.gallery_resize_video);
                }
            }


        }.execute();

    }
    // this will play the sound with the video when we select the audio
    MediaPlayer audio;
    public  void PreparedAudio(){
        Log.d("audioFile", Variables.SelectedAudio_AAC);
        File file=new File(Variables.app_folder+ Variables.SelectedAudio_AAC);
        if(file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Variables.app_folder + Variables.SelectedAudio_AAC);
                audio.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, Uri.fromFile(file));
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            if(file_duration<Variables.max_recording_duration){
                Variables.recording_duration=file_duration;
                initlize_Video_progress();
            }
            audioTrim.setVisibility(View.VISIBLE);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        // cameraView.start();
        if (is_recording) {
            deepAR.resumeVideoRecording();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (is_recording) {
            deepAR.pauseVideoRecording();
        }
        //  cameraView.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (cameraGrabber == null) {
//            return;
//        }
//        cameraGrabber.setFrameReceiver(null);
//        cameraGrabber.stopPreview();
//        cameraGrabber.releaseCamera();
//        cameraGrabber = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deepAR.release();
        try {

            if (audio != null) {
                audio.stop();
                audio.reset();
                audio.release();
            }
            // cameraView.stop();
        
        }catch (Exception e){

        }
    }



    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Are you Sure? if you Go back you can't undo this action")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        DeleteFile();
                        finish();
                        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

                    }
                }).show();

    }


    public void Go_To_preview_Activity() {
        RecordCompleteFragment recordCompleteFragment = new RecordCompleteFragment();
        recordCompleteFragment.show(getSupportFragmentManager(), "RecordComplete");
    }


    // this will delete all the video parts that is create during priviously created video
    int delete_count = 0;

    public void DeleteFile() {
        delete_count++;
        File output = new File(Variables.outputfile);
        File output2 = new File(Variables.outputfile2);
        File output_filter_file = new File(Variables.output_filter_file);

        if(output.exists()){
            output.delete();
        }
        if(output2.exists()){

            output2.delete();
        }
        if(output_filter_file.exists()){
            output_filter_file.delete();
        }

        File file = new File(Variables.app_folder + "myvideo" + delete_count + ".mp4");
        if(file.exists()){
            file.delete();
            DeleteFile();
        }

    }


    // this will hide the bottom mobile navigation controll
    public void Hide_navigation(){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

    }


    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    public void screenshotTaken(Bitmap bitmap) {

    }

    @Override
    public void videoRecordingStarted() {

    }

    @Override
    public void videoRecordingFinished() {
        append();
        record_image.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));
        camera_options.setVisibility(View.VISIBLE);
    }

    @Override
    public void videoRecordingFailed() {

    }

    @Override
    public void videoRecordingPrepared() {

    }

    @Override
    public void shutdownFinished() {

    }

    @Override
    public void initialized() {

    }

    @Override
    public void faceVisibilityChanged(boolean b) {

    }

    @Override
    public void imageVisibilityChanged(String s, boolean b) {

    }

    @Override
    public void frameAvailable(Image image) {

    }

    @Override
    public void error(ARErrorType arErrorType, String s) {

    }

    @Override
    public void effectSwitched(String s) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        deepAR.setRenderSurface(surfaceHolder.getSurface(), i1, i2);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if (deepAR != null) {
            deepAR.setRenderSurface(null, 0, 0);
        }
    }
}
