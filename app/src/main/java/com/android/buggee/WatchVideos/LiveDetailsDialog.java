package com.android.buggee.WatchVideos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.Video_Recording.LiveBroadcasterActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class LiveDetailsDialog extends DialogFragment {
    private Context context;
    TextInputLayout liveName, liveDetails;
//

    String encodedImage = null;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.live_details, null);
//        iosDialog = new IOSDialog.Builder(context)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();

        ImageView startLive = v.findViewById(R.id.startLive);
        liveName = v.findViewById(R.id.liveName);
        liveDetails = v.findViewById(R.id.liveDetails);
        startLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = liveName.getEditText().getText().toString();
                String details = liveDetails.getEditText().getText().toString();
                if (!name.isEmpty() && !details.isEmpty()) {
                    liveDetails.setErrorEnabled(false);
                    liveName.setErrorEnabled(false);
                    uploadToServer(name, details);
                } else if (name.isEmpty()) {
                    liveDetails.setErrorEnabled(false);
                    liveName.setErrorEnabled(true);
                    liveName.setError("Please Enter A Name");
                } else if (details.isEmpty()) {
                    liveName.setErrorEnabled(false);
                    liveDetails.setErrorEnabled(true);
                    liveDetails.setError("Please Enter Details Of Live");
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

    private void uploadToServer(final String name, final String details) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("name", name);
            parameters.put("id", Variables.user_id);
            parameters.put("live_details", details);
            parameters.put("status", 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.Show_loader(context, false, false);
//        iosDialog.show();
        ApiRequest.Call_Api(context, Variables.createLive, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("liveResponseCreate", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.live_file), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.live_exist), true);
                        editor.putString(getString(R.string.live_name), name);
                        editor.putString(getString(R.string.live_details), details);
                        editor.apply();
                        final Intent intent = new Intent(context, LiveBroadcasterActivity.class);
                        intent.putExtra("liveName", name);
                        intent.putExtra("liveDetails", details);
                        String id = Variables.user_id.replace(".", "");

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);
                        databaseReference.child("live").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(intent);
                                    getActivity().finish();
                                    dismiss();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void createLiveComment() {


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri uri = data.getData();
            String fileType = getMimeType(uri);
            String[] splitedType = fileType.split("/", 2);
            Log.d("SplitedType", splitedType[0] + " " + splitedType[1]);

            storeImage(uri, splitedType[1]);

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private String encodeJpgImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private String encodepngImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        try {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
            return mimeType;
        } catch (Exception e) {
            return "image/jpeg";
        }
    }

    private void storeImage(Uri uri, String type) {
        try {
            // get uri from Intent
            // get bitmap from uri
            final InputStream imageStream = context.getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            //pickImage.setImageBitmap(selectedImage);

            Log.d("imageType", type);
            if (type.equals("jpeg") || type.equals("jpg")) {
                encodedImage = encodeJpgImage(selectedImage);
            } else if (type.equals("png")) {
                encodedImage = encodepngImage(selectedImage);
            }
            if (encodedImage != null) {
                Log.d("encodedImage", encodedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
