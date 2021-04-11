package com.systematics.buggee.Main_Menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.ApiRequest;
import com.systematics.buggee.SimpleClasses.Callback;
import com.systematics.buggee.SimpleClasses.Functions;
import com.systematics.buggee.SimpleClasses.Variables;
import com.systematics.buggee.Video_Recording.Video_Recoder_A;
import com.systematics.buggee.WatchVideos.LiveDetailsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class RecordModeChoser extends DialogFragment {
    private Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.record_mode_dialog, null);
        CardView recordChooser = v.findViewById(R.id.recordChooser);
        CardView liveChooser = v.findViewById(R.id.liveChooser);
       //ardView storyCard = v.findViewById(R.id.storyCard);

        recordChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                    Intent intent = new Intent(getActivity(), Video_Recoder_A.class);
                    intent.putExtra("type", "record");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                } else {
                    Functions.showToast(getActivity(), "You have to login First");
                }
                dismiss();
            }
        });
//        storyCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
//
//                    Intent intent = new Intent(getActivity(), Video_Recoder_A.class);
//                    intent.putExtra("type","story");
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
//                } else {
//                }
//                dismiss();
//            }
//        });

        liveChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                    JSONObject parameters = new JSONObject();
                    try {
                        parameters.put("id", Variables.user_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Functions.Show_loader(context, false, false);
                    ApiRequest.Call_Api(context, Variables.getUserLiveStatus, parameters, new Callback() {
                        @Override
                        public void Responce(String resp) {
                            Functions.cancel_loader();
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                boolean success = jsonObject.optBoolean("success");
                                Log.d("livePermitted", success + "");
                                if (success) {
                                    int status = jsonObject.optInt("status");
                                    Log.d("livePermitted", status + "");
                                    if (status == 0) {
                                        Functions.showToast(getActivity(), "You are not permitted to start live!");
                                    } else {
                                        int fans = Integer.parseInt(Variables.sharedPreferences.getString(Variables.fan_count, "0"));
                                        if (fans < 100) {
                                            Functions.showToast(getActivity(), "You are not eligible to start live");
                                            dismiss();
                                        } else {
                                            LiveDetailsDialog liveDetailsDialog = new LiveDetailsDialog();
                                            liveDetailsDialog.show(getChildFragmentManager(), "liveDetails");
                                        }
//                                        LiveDetailsDialog liveDetailsDialog = new LiveDetailsDialog();
//                                        liveDetailsDialog.show(getChildFragmentManager(), "liveDetails");

                                    }

                                } else {
                                    String message = jsonObject.optString("message");
                                    Functions.showToast(getActivity(), message);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    Functions.showToast(getActivity(), "You have to login First");
                }


            }
        });
        builder.setView(v);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}