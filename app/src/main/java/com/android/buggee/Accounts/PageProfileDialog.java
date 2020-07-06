package com.android.buggee.Accounts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class PageProfileDialog extends DialogFragment {

    private TextView nameView, descriptionView;
    private ImageView circleImageView;
    private Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.page_profile_dialog, null);
        nameView = v.findViewById(R.id.nameView);
        descriptionView = v.findViewById(R.id.descriptionView);
        circleImageView = v.findViewById(R.id.circleImageView);
        v.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        getPageDate(getTag());

        builder.setView(v);
        return builder.create();
    }

    private void getPageDate(String id) {
        Functions.Show_loader(context, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        iosDialog.show();
        ApiRequest.Call_Api(context, Variables.getPageInfo, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("liveResponseCreate", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        String name = jsonObject.optString("name");
                        String profile_pic = jsonObject.optString("profile_pic");
                        String description = jsonObject.optString("description");
                        Picasso.with(context).load(Variables.base_url + profile_pic).placeholder(R.drawable.profile_image_placeholder).into(circleImageView);
                        nameView.setText(name);
                        if (!description.toLowerCase().equals("null")) {
                            descriptionView.setText(description);
                        } else {
                            descriptionView.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(context, "Something Wrong With that Page", Toast.LENGTH_SHORT).show();
                        dismiss();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
