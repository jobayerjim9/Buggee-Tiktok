package com.android.buggee.Settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class CreatePageDialog extends DialogFragment {
    private Context context;
    final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    ImageView profilePic;
    TextInputLayout pageNameInput;
    TextView uploadText;
    ImageView submitButton;

    String encodedImage = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.create_page_dialog, null);
        profilePic = v.findViewById(R.id.profilePic);
        uploadText = v.findViewById(R.id.uploadText);
        pageNameInput = v.findViewById(R.id.pageNameInput);
        submitButton = v.findViewById(R.id.submitButton);
        ImageView backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(), permissions, 1);
                } else {
                    pickImage();
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = pageNameInput.getEditText().getText().toString();
                if (name.isEmpty()) {
                    pageNameInput.setErrorEnabled(true);
                    pageNameInput.setError("Please Enter Page Name!");
                } else if (encodedImage == null) {
                    Toast.makeText(context, "Please Select A Profile Picture", Toast.LENGTH_SHORT).show();
                } else {
                    pageNameInput.setErrorEnabled(false);
                    createPage(name, encodedImage);
                }
            }
        });
        builder.setView(v);
        return builder.create();

    }

    private void createPage(String name, String encodedImage) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("name", name);
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            parameters.put("profile_pic", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.Show_loader(context, false, false);
//        iosDialog.show();
        ApiRequest.Call_Api(context, Variables.createPage, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("liveResponseCreate", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Variables.sharedPreferences.edit().putInt(Variables.page_have, 1).apply();
                        Toast.makeText(context, "You Can Now Post Video From Your Page By Following Regular Video Uploading", Toast.LENGTH_LONG).show();
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
            profilePic.setImageBitmap(selectedImage);
            uploadText.setVisibility(View.GONE);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
