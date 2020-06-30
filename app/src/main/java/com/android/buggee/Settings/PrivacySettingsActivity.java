package com.android.buggee.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacySettingsActivity extends AppCompatActivity implements PrivacyChooserDialog.PrivacyDialogListener {
    TextView directMessagePrivacy, commentPrivacy, liveStreamPrivacy;
    Switch privateAccountSwitch;
    IOSDialog iosDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();
        directMessagePrivacy = findViewById(R.id.directMessagePrivacy);
        privateAccountSwitch = findViewById(R.id.privateAccountSwitch);
        commentPrivacy = findViewById(R.id.commentPrivacy);
        liveStreamPrivacy = findViewById(R.id.liveStreamPrivacy);

        privateAccountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    updateAccountType("private");
                } else {
                    updateAccountType("public");
                }
            }
        });
        directMessagePrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyChooserDialog privacyChooserDialog = new PrivacyChooserDialog();
                privacyChooserDialog.show(getSupportFragmentManager(), "message");
                privacyChooserDialog.setCancelable(true);
            }
        });
        commentPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyChooserDialog privacyChooserDialog = new PrivacyChooserDialog();
                privacyChooserDialog.show(getSupportFragmentManager(), "comment");
                privacyChooserDialog.setCancelable(true);
            }
        });
        liveStreamPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyChooserDialog privacyChooserDialog = new PrivacyChooserDialog();
                privacyChooserDialog.show(getSupportFragmentManager(), "live");
                privacyChooserDialog.setCancelable(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPrivacy();
    }

    private void updateAccountType(String privacy) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.user_id);
            parameters.put("privacy", privacy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        iosDialog.show();
        ApiRequest.Call_Api(PrivacySettingsActivity.this, Variables.changeAccountType, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                iosDialog.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Toast.makeText(PrivacySettingsActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PrivacySettingsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getPrivacy() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.user_id);
            Log.d("idFromUser", Variables.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        iosDialog.show();
        ApiRequest.Call_Api(PrivacySettingsActivity.this, Variables.getPrivacy, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.d("privacyResponse", resp);
                iosDialog.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String account_type = jsonObject.optString("account_type");
                    String message_privacy = jsonObject.optString("message_privacy");
                    String comment_privacy = jsonObject.optString("comment_privacy");
                    String live_privacy = jsonObject.optString("live_privacy");
                    String placeHolder = null;
                    if (message_privacy.equals("friend")) {
                        placeHolder = "Friends";
                    } else if (message_privacy.equals("everyone")) {
                        placeHolder = "Everyone";
                    } else if (message_privacy.equals("only me")) {
                        placeHolder = "Only Me";
                    }
                    directMessagePrivacy.setText(placeHolder);

                    if (comment_privacy.equals("friend")) {
                        placeHolder = "Friends";
                    } else if (comment_privacy.equals("everyone")) {
                        placeHolder = "Everyone";
                    } else if (comment_privacy.equals("only me")) {
                        placeHolder = "Only Me";
                    }
                    commentPrivacy.setText(placeHolder);
                    if (live_privacy.equals("friend")) {
                        placeHolder = "Friends";
                    } else if (live_privacy.equals("everyone")) {
                        placeHolder = "Everyone";
                    } else if (live_privacy.equals("only me")) {
                        placeHolder = "Only Me";
                    }
                    liveStreamPrivacy.setText(placeHolder);

                    if (account_type.equals("private")) {
                        privateAccountSwitch.setChecked(true);
                    } else {
                        privateAccountSwitch.setChecked(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onDialogMessageClick(String privacy) {
        getPrivacy();
    }

    @Override
    public void onDialogCommentClick(String privacy) {
        getPrivacy();
    }

    @Override
    public void onDialogLiveClick(String privacy) {
        getPrivacy();
    }
}