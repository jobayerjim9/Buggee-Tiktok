package com.android.buggee.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.buggee.Comments.Comment_Get_Set;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.WatchVideos.LiveWatchActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.RtcEngine;

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.LiveViewHolder> {
    private Context context;
    private ArrayList<LiveData> liveData;
    private LiveAdapter.OnItemClickListener listener;

    public LiveAdapter(Context context, ArrayList<LiveData> liveData, OnItemClickListener listener) {
        this.context = context;
        this.liveData = liveData;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LiveData item, View view);
    }

    @NonNull
    @Override
    public LiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveViewHolder(LayoutInflater.from(context).inflate(R.layout.live_video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.bind(position, liveData.get(position), listener);
        holder.username.setText(liveData.get(position).live_name);
        holder.desc_txt.setText(liveData.get(position).live_details);
        try {
            Picasso.with(context).
                    load(liveData.get(position).profile_pic)
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .resize(100, 100).into(holder.user_pic);

            Picasso.with(context).
                    load(liveData.get(position).profile_pic)
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .resize(100, 100).into(holder.user_pic2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (liveData.get(position).verified != null && liveData.get(position).verified.equalsIgnoreCase("1")) {
            holder.varified_btn.setVisibility(View.VISIBLE);
        } else {
            holder.varified_btn.setVisibility(View.GONE);
        }
        holder.name.setText(liveData.get(position).username);


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return liveData.size();
    }

    class LiveViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_pic2, user_pic;
        LinearLayout comment_layout;
        ImageView smallPlusButton, varified_btn;
        TextView username, desc_txt, name;
        ConstraintLayout live_item;
        public LiveViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            desc_txt = itemView.findViewById(R.id.desc_txt);
            name = itemView.findViewById(R.id.name);
//            comment_layout = itemView.findViewById(R.id.comment_layout);
            user_pic2 = itemView.findViewById(R.id.user_pic2);
            smallPlusButton = itemView.findViewById(R.id.smallPlusButton);
            varified_btn = itemView.findViewById(R.id.varified_btn);
            user_pic = itemView.findViewById(R.id.user_pic);
            name = itemView.findViewById(R.id.name);
            live_item = itemView.findViewById(R.id.live_item);
        }

        public void bind(final int postion, final LiveData item, final LiveAdapter.OnItemClickListener listener) {
//            comment_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.onItemClick(postion, item, view);
//                }
//            });
            live_item.setOnTouchListener(new View.OnTouchListener() {

                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        showReportOption(item);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            user_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(postion, item, view);
                }
            });
            user_pic2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(postion, item, view);
                }
            });
            smallPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(postion, item, view);
                }
            });
        }
    }

    private void showReportOption(final LiveData liveData) {

        final CharSequence[] options = {"Report Video", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Report Video")) {
                    //dialog.dismiss();

                    reportComment(liveData);

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });
        if (!liveData.fb_id.equals(Variables.sharedPreferences.getString(Variables.u_id, ""))) {
            builder.show();
        }

    }

    private void reportComment(final LiveData liveData) {
        new AlertDialog.Builder(context)
                .setTitle("Are You Sure?")
                .setMessage("Action May Take Within 24 Hour!")
                .setPositiveButton("Report!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JSONObject parameters = new JSONObject();
                        try {
                            parameters.put("content_id", liveData.live_id);
                            parameters.put("reported_by", Variables.sharedPreferences.getString(Variables.u_id, ""));
                            parameters.put("type", "live");

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
                                        Toast.makeText(context, "Reported!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String message = jsonObject.optString("msg");
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
}
