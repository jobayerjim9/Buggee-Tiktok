package com.android.buggee.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.buggee.Home.Home_Get_Set;
import com.android.buggee.R;

import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {
    private Context context;
    private ArrayList<String> subCategories;
    private SubCategoryAdapter.OnItemClickListener listener;

    public SubCategoryAdapter(Context context, ArrayList<String> subCategories, OnItemClickListener listener) {
        this.context = context;
        this.subCategories = subCategories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubCategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.sub_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        holder.subCategoryButton.setText(subCategories.get(position));
        holder.bind(position, subCategories.get(position), listener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String item, View view);
    }

    class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        RadioButton subCategoryButton;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            subCategoryButton = itemView.findViewById(R.id.subCategoryButton);
        }

        public void bind(final int position, final String item, final SubCategoryAdapter.OnItemClickListener listener) {
            subCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position, item, v);
                }
            });

        }
    }
}
