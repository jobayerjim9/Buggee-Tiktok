package com.android.buggee.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class PrivacyChooserDialog extends DialogFragment {
    private Context context;
    //    IOSDialog iosDialog;
    CardView onlyMe, friends, everyone;
    String privacy;

    public interface PrivacyDialogListener {
        public void onDialogMessageClick(String privacy);

        public void onDialogCommentClick(String privacy);

        public void onDialogLiveClick(String privacy);
    }

    PrivacyDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_privacy_chooser, null);
//        iosDialog = new IOSDialog.Builder(context)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();
        TextView onlyMe = v.findViewById(R.id.onlyMeText);
        TextView friends = v.findViewById(R.id.friendText);
        TextView everyone = v.findViewById(R.id.everyoneText);


        onlyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacy = "only me";
                if (getTag().equals("message")) {
                    updateMessagePrivacy(privacy);
                } else if (getTag().equals("comment")) {
                    updateCommentPrivacy(privacy);
                } else if (getTag().equals("live")) {
                    updateLivePrivacy(privacy);
                }
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacy = "friend";
                if (getTag().equals("message")) {
                    updateMessagePrivacy(privacy);
                } else if (getTag().equals("comment")) {
                    updateCommentPrivacy(privacy);
                } else if (getTag().equals("live")) {
                    updateLivePrivacy(privacy);
                }
            }
        });
        everyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacy = "everyone";
                if (getTag().equals("message")) {
                    updateMessagePrivacy(privacy);
                } else if (getTag().equals("comment")) {
                    updateCommentPrivacy(privacy);
                } else if (getTag().equals("live")) {
                    updateLivePrivacy(privacy);
                }

            }
        });


        builder.setView(v);
        return builder.create();
    }

    private void updateMessagePrivacy(final String privacy) {
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("id", Variables.user_id);
            parameters.put("privacy", privacy);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.messagePrivacy, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                        listener.onDialogMessageClick(privacy);
                        dismiss();
                    } else {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void updateCommentPrivacy(final String privacy) {
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("id", Variables.user_id);
            parameters.put("privacy", privacy);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.commentPrivacy, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                        listener.onDialogCommentClick(privacy);
                        dismiss();
                    } else {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void updateLivePrivacy(final String privacy) {
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("id", Variables.user_id);
            parameters.put("privacy", privacy);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.livePrivacy, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                        listener.onDialogLiveClick(privacy);
                        dismiss();

                    } else {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (PrivacyDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
