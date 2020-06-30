package com.android.buggee.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.buggee.R;
import com.android.buggee.WatchVideos.LiveWatchActivity;
import com.squareup.picasso.Picasso;

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
        ImageView smallPlusButton;
        TextView username, desc_txt, name;

        public LiveViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            desc_txt = itemView.findViewById(R.id.desc_txt);
            name = itemView.findViewById(R.id.name);
            comment_layout = itemView.findViewById(R.id.comment_layout);
            user_pic2 = itemView.findViewById(R.id.user_pic2);
            smallPlusButton = itemView.findViewById(R.id.smallPlusButton);
            user_pic = itemView.findViewById(R.id.user_pic);
            name = itemView.findViewById(R.id.name);
        }

        public void bind(final int postion, final LiveData item, final LiveAdapter.OnItemClickListener listener) {
            comment_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(postion, item, view);
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


}
