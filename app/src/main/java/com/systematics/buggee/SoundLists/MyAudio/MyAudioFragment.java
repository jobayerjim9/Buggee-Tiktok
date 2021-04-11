package com.systematics.buggee.SoundLists.MyAudio;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.systematics.buggee.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.ApiRequest;
import com.systematics.buggee.SimpleClasses.Callback;
import com.systematics.buggee.SimpleClasses.Functions;
import com.systematics.buggee.SimpleClasses.Variables;
import com.systematics.buggee.SoundLists.FavouriteSounds.Favourite_Sound_Adapter;
import com.systematics.buggee.SoundLists.Sounds_GetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.systematics.buggee.SimpleClasses.Variables.getMySounds;

public class MyAudioFragment extends RootFragment implements Player.EventListener {

    Context context;
    View view;
    CardView chooseThumb, chooseAudio;
    TextView chooseThumbText, chooseAudioText;
    EditText descriptionAudio, audioName;
    String encodedImage = null;
    Boolean audioViewState = false;
    ArrayList<Sounds_GetSet> datalist;
    Favourite_Sound_Adapter adapter;
    RecyclerView recyclerView;
    DownloadRequest prDownloader;
    public static String running_sound_id;
    SwipeRefreshLayout swiperefresh;
    View previous_view;
    Thread thread;
    SimpleExoPlayer player;
    String previous_url = "none";
    ImageView closeUpload;

    public MyAudioFragment() {

    }

    LinearLayout uploadAudioView;
    final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_audio, container, false);
        context = getContext();
        final CardView uploadAudio = view.findViewById(R.id.uploadAudio);
        chooseThumb = view.findViewById(R.id.chooseThumb);
        chooseThumbText = view.findViewById(R.id.chooseThumbText);
        chooseAudio = view.findViewById(R.id.chooseAudio);
        chooseAudioText = view.findViewById(R.id.chooseAudioText);
        descriptionAudio = view.findViewById(R.id.descriptionAudio);
        uploadAudioView = view.findViewById(R.id.uploadAudioView);
        closeUpload = view.findViewById(R.id.closeUpload);
        audioName = view.findViewById(R.id.audioName);
        recyclerView = view.findViewById(R.id.myAudioRecycler);
        uploadAudioView.setVisibility(View.GONE);
        closeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudioView.setVisibility(View.GONE);
                audioViewState = false;
            }
        });
        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioViewState) {
                    uploadAudio();
                } else {
                    swiperefresh.setVisibility(View.GONE);
                    uploadAudioView.setVisibility(View.VISIBLE);
                    audioViewState = true;
                }

            }
        });
        chooseThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(), permissions, 1);
                } else {
                    pickImage();
                }
            }
        });
        chooseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(), permissions, 1);
                } else {
                    pickAudio();
                }
            }
        });
        running_sound_id = "none";
        PRDownloader.initialize(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMySounds();
            }
        });

        getMySounds();
        return view;
    }

    private void getMySounds() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.getMySounds, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Parse_data(resp);
            }
        });
    }

    public void Parse_data(String responce) {

        datalist = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);

                    Sounds_GetSet item = new Sounds_GetSet();

                    item.id = itemdata.optString("id");

                    JSONObject audio_path = itemdata.optJSONObject("audio_path");

                    item.acc_path = audio_path.optString("acc");
                    item.sound_name = itemdata.optString("sound_name");
                    item.description = itemdata.optString("description");
                    item.thum = itemdata.optString("thum");
                    item.date_created = itemdata.optString("created");
                    item.type = "my";
                    datalist.add(item);
                }

                Set_adapter();


            } else {
                Functions.showToast(getActivity(), "" + jsonObject.optString("msg"));
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    public void Set_adapter() {

        adapter = new Favourite_Sound_Adapter(context, datalist, new Favourite_Sound_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, Sounds_GetSet item) {

                if (view.getId() == R.id.done) {
                    StopPlaying();
                    Down_load_mp3(item.id, item.sound_name, item.acc_path);
                } else {
                    if (thread != null && !thread.isAlive()) {
                        StopPlaying();
                        playaudio(view, item);
                    } else if (thread == null) {
                        StopPlaying();
                        playaudio(view, item);
                    }
                }

            }
        });

        recyclerView.setAdapter(adapter);


    }

    public void StopPlaying() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }

        show_Stop_state();

    }

    public void playaudio(View view, final Sounds_GetSet item) {
        previous_view = view;

        if (previous_url.equals(item.acc_path)) {

            previous_url = "none";
            running_sound_id = "none";
        } else {

            previous_url = item.acc_path;
            running_sound_id = item.id;

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "TikTok"));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(item.acc_path));


            player.prepare(videoSource);
            player.addListener(this);


            player.setPlayWhenReady(true);


        }

    }

    @Override
    public void onStop() {
        super.onStop();

        running_sound_id = "null";

        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }

        show_Stop_state();

    }

    public void Show_Run_State() {

        if (previous_view != null) {
            previous_view.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            previous_view.findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
            previous_view.findViewById(R.id.done).setVisibility(View.VISIBLE);
        }

    }


    public void Show_loading_state() {
        previous_view.findViewById(R.id.play_btn).setVisibility(View.GONE);
        previous_view.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
    }


    public void show_Stop_state() {

        if (previous_view != null) {
            previous_view.findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
            previous_view.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            previous_view.findViewById(R.id.pause_btn).setVisibility(View.GONE);
            previous_view.findViewById(R.id.done).setVisibility(View.GONE);
        }

        running_sound_id = "none";

    }

    public void Down_load_mp3(final String id, final String sound_name, String url) {

        Functions.Show_loader(getActivity(), false, false);

        prDownloader = PRDownloader.download(url, Variables.app_folder, Variables.SelectedAudio_AAC)
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
                        Log.d("downloadProgress", progress.currentBytes + " " + progress.totalBytes + " " + progress.toString());
                    }
                });

        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Functions.cancel_loader();
                Intent output = new Intent();
                output.putExtra("isSelected", "yes");
                output.putExtra("sound_name", sound_name);
                output.putExtra("sound_id", id);
                getActivity().setResult(RESULT_OK, output);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
            }

            @Override
            public void onError(Error error) {
                Functions.cancel_loader();
            }
        });

    }

    @Override
    public boolean onBackPressed() {
        getActivity().onBackPressed();
        return super.onBackPressed();
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void pickAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(intent, 2);
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        try {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
            return mimeType;
        } catch (Exception e) {
            return "image/jpeg";
        }
    }

    private void uploadAudio() {
        String name = audioName.getText().toString().trim();
        String description = descriptionAudio.getText().toString().trim();
        if (name.isEmpty() || description.isEmpty() || audioBase64 == null || encodedImage == null) {
            Functions.showToast(getActivity(), "Check All Field!");
        } else {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("audioFile", audioBase64);
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
                parameters.put("sound_name", name);
                parameters.put("description", description);
                parameters.put("thum", encodedImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Functions.Show_loader(context, false, false);
            ApiRequest.Call_Api(context, Variables.uploadMyAudio, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    try {
                        Functions.cancel_loader();
                        JSONObject jsonObject = new JSONObject(resp);
                        Log.d("audioUploadResp", resp);
                        String code = jsonObject.optString("code");
                        if (code.equals("201")) {
                            uploadAudioView.setVisibility(View.GONE);
                            swiperefresh.setVisibility(View.VISIBLE);
                            audioViewState = false;
                            audioBase64 = null;
                            audioName.setText("");
                            descriptionAudio.setText("");
                            encodedImage = null;
                            Functions.showToast(getActivity(), "Audio Uploaded Successfully");
                            swiperefresh.setRefreshing(true);
                            getMySounds();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }


    }

    byte[] audioBytes;
    String audioBase64;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri uri = data.getData();
            String fileType = getMimeType(uri);
            String[] splitedType = fileType.split("/", 2);
            Log.d("SplitedType", splitedType[0] + " " + splitedType[1]);
            storeImage(uri, splitedType[1]);

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri uri = data.getData();
            if (uri != null) {
                storeAudio(uri);
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private void storeAudio(Uri uri) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream fis = context.getContentResolver().openInputStream(uri);
            Log.d("audioFIlePath", fis.toString());
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            audioBytes = baos.toByteArray();
            // Here goes the Base64 string
            audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);
            chooseAudioText.setText("Choosed");
            chooseAudioText.setTextColor(context.getResources().getColor(R.color.green));
            Log.d("audioBase64", audioBase64);
        } catch (Exception e) {
            Log.e("ExceptionOccured", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void storeImage(Uri uri, String type) {
        try {
            // get uri from Intent
            // get bitmap from uri
            final InputStream imageStream = context.getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            chooseThumbText.setText("Choosed");
            chooseThumbText.setTextColor(context.getResources().getColor(R.color.green));
            Log.d("imageType", type);
            if (type.equals("jpeg") || type.equals("jpg")) {
                encodedImage = encodeJpgImage(selectedImage);
            } else if (type.equals("png")) {
                encodedImage = encodepngImage(selectedImage);
            }
            if (encodedImage != null) {
                Log.d("encodedImage", encodedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private String encodeJpgImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private String encodepngImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

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
        if (playbackState == Player.STATE_BUFFERING) {
            Show_loading_state();
        } else if (playbackState == Player.STATE_READY) {
            Show_Run_State();
        } else if (playbackState == Player.STATE_ENDED) {
            show_Stop_state();
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
}