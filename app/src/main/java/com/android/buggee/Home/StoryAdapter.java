package com.android.buggee.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.buggee.R;
import com.android.buggee.WatchVideos.WatchVideos_F;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private Context context;
    private ArrayList<Home_Get_Set> stories;


    public StoryAdapter(Context context, ArrayList<Home_Get_Set> stories) {
        this.context = context;
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryViewHolder(LayoutInflater.from(context).inflate(R.layout.story_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, final int position) {
        Picasso.with(context).load(stories.get(position).thum).placeholder(R.drawable.profile_image_placeholder).into(holder.storyThum);
        holder.storyThum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WatchVideos_F.class);
                intent.putExtra("type", "story");
                intent.putExtra("arraylist", stories);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {
        CircleImageView storyThum;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            storyThum = itemView.findViewById(R.id.storyThum);
        }
    }
}
