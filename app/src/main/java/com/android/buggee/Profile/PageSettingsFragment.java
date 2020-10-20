package com.android.buggee.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.Settings.PageButtonCategory;
import com.android.buggee.Settings.RequestVerificationActivity;
import com.android.buggee.Settings.SettingsActivity;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;

import org.json.JSONException;
import org.json.JSONObject;


public class PageSettingsFragment extends Fragment {
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page_settings, container, false);
        Button pageCategory = v.findViewById(R.id.pageCategory);
        Button pageButton = v.findViewById(R.id.pageButton);
        Button pageVerification = v.findViewById(R.id.pageVerification);
        Button useAsId = v.findViewById(R.id.useAsId);
        LinearLayout backToId = v.findViewById(R.id.backToId);
        if (Variables.sharedPreferences.getInt(Variables.id_page, 0) == 1) {
            backToId.setVisibility(View.VISIBLE);
            useAsId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Variables.sharedPreferences.edit().putInt(Variables.id_page, 0).apply();
                }
            });
        }
        Switch pagePublishSwitch = v.findViewById(R.id.pagePublishSwitch);
        int published = Variables.sharedPreferences.getInt(Variables.page_published, 1);
        if (published == 1) {
            pagePublishSwitch.setText("Page Visibility(Published)");
            pagePublishSwitch.setChecked(true);
        } else {
            pagePublishSwitch.setText("Page Visibility(Not published)");
            pagePublishSwitch.setChecked(false);
        }
        pagePublishSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    publishUnpublishPage(1);
                } else {
                    publishUnpublishPage(0);
                }
            }
        });
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

    private void publishUnpublishPage(final int status) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.user_id);
            parameters.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.publishUnpublishPage, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Variables.sharedPreferences.edit().putInt(Variables.page_published, status).apply();
                        if (status == 1) {
                            Functions.showToast(getActivity(), "Page Published!");
                        } else {
                            Functions.showToast(getActivity(), "Page Unpublished!");
                        }

                    } else {
                        Functions.showToast(getActivity(), "Failed!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}