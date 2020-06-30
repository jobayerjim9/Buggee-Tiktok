package com.android.buggee.Comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.buggee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveCommentAdapter extends RecyclerView.Adapter<LiveCommentAdapter.LiveCommentViewHolder> {
    private Context context;
    private ArrayList<LiveCommentData> liveCommentData;

    public LiveCommentAdapter(Context context, ArrayList<LiveCommentData> liveCommentData) {
        this.context = context;
        this.liveCommentData = liveCommentData;
    }

    @NonNull
    @Override
    public LiveCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveCommentViewHolder holder, int position) {
        try {
            Picasso.with(context).
                    load(liveCommentData.get(position).getProfile_pic())
                    .resize(50, 50)
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .into(holder.user_pic);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String placeHolder = liveCommentData.get(position).getFirst_name() + " " + liveCommentData.get(position).getLast_name();
        holder.username.setText(placeHolder);
        holder.message.setText(liveCommentData.get(position).getComment_text());


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return liveCommentData.size();
    }

    class LiveCommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_pic;
        TextView username, message;

        public LiveCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            user_pic = itemView.findViewById(R.id.user_pic);
        }
    }
}
