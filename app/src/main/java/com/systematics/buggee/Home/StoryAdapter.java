package com.systematics.buggee.Home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.systematics.buggee.R;
import com.systematics.buggee.WatchVideos.WatchVideos_F;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private Context context;
    private ArrayList<Home_Get_Set> stories;
    private ArrayList<ImageStoryData> imageStoryData;
    private String type;

    public StoryAdapter(Context context, ArrayList<Home_Get_Set> stories, ArrayList<ImageStoryData> imageStoryData) {
        this.context = context;
        this.stories = stories;
        this.imageStoryData = imageStoryData;
    }


    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryViewHolder(LayoutInflater.from(context).inflate(R.layout.story_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, final int position) {
        Log.d("imageUrl", imageStoryData.get(0).url);
        if (position < stories.size() && stories.size() != 0) {
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
        } else {
            Log.d("image", "gotImage");
            Picasso.with(context).load(imageStoryData.get(position - stories.size()).url).placeholder(R.drawable.profile_image_placeholder).resize(100, 100).into(holder.storyThum);
            holder.storyThum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Uri> images = new ArrayList<>();
                    for (int i = 0; i < imageStoryData.size(); i++) {
                        images.add(Uri.parse(imageStoryData.get(i).url));
                    }
                    new StfalconImageViewer.Builder<>(context, images, new ImageLoader<Uri>() {
                        @Override
                        public void loadImage(ImageView imageView, Uri image) {
                            Picasso.with(context).load(image).into(imageView);
                        }
                    }).withStartPosition(position - stories.size()).show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        Log.d("sizeOfStory", stories.size() + " " + imageStoryData.size());
        return (stories.size() + imageStoryData.size());
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {
        CircleImageView storyThum;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            storyThum = itemView.findViewById(R.id.storyThum);
        }
    }
}
