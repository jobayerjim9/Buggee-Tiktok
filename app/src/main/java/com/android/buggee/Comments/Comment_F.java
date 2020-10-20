package com.android.buggee.Comments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.API_CallBack;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Fragment_Data_Send;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment_F extends RootFragment {

    View view;
    Context context;

    RecyclerView recyclerView;

    Comments_Adapter adapter;
    LiveCommentAdapter liveCommentAdapter;
    ArrayList<Comment_Get_Set> data_list;
    ArrayList<LiveCommentData> liveCommentData = new ArrayList<>();
    String video_id;
    String user_id;
    String video_type;

    EditText message_edit;
    ImageButton send_btn;
    ProgressBar send_progress;

    TextView comment_count_txt;

    FrameLayout comment_screen;

    public static int comment_count=0;
    public Comment_F() {

    }

    Fragment_Data_Send fragment_data_send;

    public Comment_F(int count, Fragment_Data_Send fragment_data_send){
        comment_count=count;
        this.fragment_data_send=fragment_data_send;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_comment, container, false);
        context=getContext();


        comment_screen=view.findViewById(R.id.comment_screen);
        comment_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();

            }
        });

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            video_id = bundle.getString("video_id");
            user_id = bundle.getString("user_id");
            video_type = bundle.getString("video_type");

        }


        comment_count_txt = view.findViewById(R.id.comment_count);

        recyclerView = view.findViewById(R.id.recylerview);
        message_edit = view.findViewById(R.id.message_edit);
        send_progress = view.findViewById(R.id.send_progress);
        send_btn = view.findViewById(R.id.send_btn);

        if (video_type.equals("recorded")) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);


            data_list = new ArrayList<>();
            adapter = new Comments_Adapter(context, data_list, new Comments_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int postion, Comment_Get_Set item, View view) {
                    if (view.getId() == R.id.mainlayout) {
                        reportComment(item);
                    }

                }
            });

            recyclerView.setAdapter(adapter);


            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String message = message_edit.getText().toString();
                    if (!TextUtils.isEmpty(message)) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, message);
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                        } else {
                            Functions.showToast(getActivity(), "Please Login into the app");
                        }
                    }

                }
            });


            Get_All_Comments();

        } else if (video_type.equals("live")) {
            final String id = user_id.replace(".", "");
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);
            liveCommentAdapter = new LiveCommentAdapter(context, liveCommentData);
            recyclerView.setAdapter(liveCommentAdapter);
            DatabaseReference liveRef = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);


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
                                recyclerView.scrollToPosition(liveCommentData.size() - 1);
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
        return view;
    }

    private void reportComment(Comment_Get_Set item) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("content_id", item.id);
            parameters.put("reported_by", Variables.sharedPreferences.getString(Variables.u_id, ""));
            parameters.put("type", "comment");

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


    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());

        super.onDetach();
    }

    // this funtion will get all the comments against post
    public void Get_All_Comments(){

        Functions.Call_Api_For_get_Comment(getActivity(), video_id, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                ArrayList<Comment_Get_Set> arrayList1=arrayList;
                for(Comment_Get_Set item:arrayList1){
                     data_list.add(item);
                }
                comment_count_txt.setText(data_list.size()+" comments");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }

        });

    }


    // this function will call an api to upload your comment
    public void Send_Comments(final String video_id, final String comment) {

        Functions.Call_Api_For_Send_Comment(getActivity(), video_id, comment, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                send_progress.setVisibility(View.GONE);
                send_btn.setVisibility(View.VISIBLE);

                ArrayList<Comment_Get_Set> arrayList1 = arrayList;
                for (Comment_Get_Set item : arrayList1) {
                    data_list.add(0, item);
                    comment_count++;

                    // SendPushNotification(getActivity(),user_id,comment,video_id);

                    comment_count_txt.setText(comment_count+" comments");

                    if(fragment_data_send!=null)
                        fragment_data_send.onDataSent(""+comment_count);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });

    }


    public void SendPushNotification(Activity activity, String user_id, String comment, String video_id) {

        JSONObject notimap = new JSONObject();
        try {
            notimap.put("title", Variables.sharedPreferences.getString(Variables.u_name, "") + " Comment on your video");
            notimap.put("message", comment);
            notimap.put("icon", Variables.sharedPreferences.getString(Variables.u_pic, ""));
            notimap.put("senderid", Variables.sharedPreferences.getString(Variables.u_id, ""));
            notimap.put("receiverid", user_id);
            notimap.put("action_type", "comment");
            notimap.put("videoId", video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context,Variables.sendPushNotification,notimap,null);

    }




}
