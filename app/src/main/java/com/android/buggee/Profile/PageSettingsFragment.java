package com.android.buggee.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.buggee.R;
import com.android.buggee.Settings.PageButtonCategory;
import com.android.buggee.Settings.RequestVerificationActivity;
import com.android.buggee.Settings.SettingsActivity;


public class PageSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page_settings, container, false);
        Button pageCategory = v.findViewById(R.id.pageCategory);
        Button pageButton = v.findViewById(R.id.pageButton);
        Button pageVerification = v.findViewById(R.id.pageVerification);
        pageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageCategoryDialog pageCategoryDialog = new PageCategoryDialog();
                pageCategoryDialog.show(getChildFragmentManager(), "category");
            }
        });
        pageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageButtonCategory pageCategoryDialog = new PageButtonCategory();
                pageCategoryDialog.show(getChildFragmentManager(), "button");
            }
        });
        pageVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RequestVerificationActivity.class);
                intent.putExtra("type", "page");
                startActivity(intent);
            }
        });


        return v;
    }
}