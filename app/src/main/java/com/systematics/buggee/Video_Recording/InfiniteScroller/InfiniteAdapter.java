package com.systematics.buggee.Video_Recording.InfiniteScroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.systematics.buggee.R;

import java.util.ArrayList;

public class InfiniteAdapter extends RecyclerView.Adapter<InfiniteAdapter.InfiniteViewHolder> {
    private Context context;
    private ArrayList<String> arrayList;

    public InfiniteAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public InfiniteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InfiniteViewHolder(LayoutInflater.from(context).inflate(R.layout.infinite_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InfiniteViewHolder holder, int position) {
        holder.sec.setText(arrayList.get(position % arrayList.size()));
        //Log.d("positionOfInfinite",position+" "+position % arrayList.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position % arrayList.size();
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class InfiniteViewHolder extends RecyclerView.ViewHolder {
        TextView sec;

        public InfiniteViewHolder(@NonNull View itemView) {
            super(itemView);
            sec = itemView.findViewById(R.id.sec);
        }
    }
}
