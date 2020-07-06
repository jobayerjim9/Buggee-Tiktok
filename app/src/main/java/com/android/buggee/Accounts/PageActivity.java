package com.android.buggee.Accounts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PageActivity extends AppCompatActivity {
    private TextView nameView, descriptionView;
    private EditText nameEditText, descriptionEditText;
    private ImageView circleImageView, nameSubmit, descSubmit;
    private LinearLayout nameEditLayout, descriptionEditLayout;
    final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String encodedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        getPageInfo();
        nameView = findViewById(R.id.nameView);
        descriptionView = findViewById(R.id.descriptionView);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        circleImageView = findViewById(R.id.circleImageView);
        nameSubmit = findViewById(R.id.nameSubmit);
        descSubmit = findViewById(R.id.descSubmit);
        nameEditLayout = findViewById(R.id.nameEditLayout);
        descriptionEditLayout = findViewById(R.id.descriptionEditLayout);
        findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(PageActivity.this, permissions, 1);
                } else {
                    pickImage();
                }


            }
        });
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameView.setVisibility(View.GONE);
                nameEditLayout.setVisibility(View.VISIBLE);
                nameEditText.requestFocus();
            }
        });
        nameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(PageActivity.this, "Name Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    updateNameForPage(name);
                }
            }
        });
        descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descriptionView.setVisibility(View.GONE);
                descriptionEditLayout.setVisibility(View.VISIBLE);
                descriptionEditText.requestFocus();
            }
        });
        descSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = descriptionEditText.getText().toString();
                if (desc.isEmpty()) {
                    Toast.makeText(PageActivity.this, "Description Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    updateDescForPage(desc);
                }
            }
        });


    }

    private void getPageInfo() {
        Functions.Show_loader(this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        iosDialog.show();
        ApiRequest.Call_Api(PageActivity.this, Variables.getPageInfo, parameters, new Callback() {
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
                        Picasso.with(PageActivity.this).load(Variables.base_url + profile_pic).placeholder(R.drawable.profile_image_placeholder).into(circleImageView);
                        nameView.setText(name);
                        nameEditText.setText(name);
                        if (!description.toLowerCase().equals("null")) {
                            descriptionView.setText(description);
                            descriptionEditText.setText(description);
                        } else {
                            descriptionView.setText("Add Your Description!");
                        }
                    } else {
                        Toast.makeText(PageActivity.this, "Something Wrong With Your Page", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateDescForPage(final String desc) {
        Functions.Show_loader(this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            parameters.put("desc", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        iosDialog.show();
        ApiRequest.Call_Api(PageActivity.this, Variables.updatePageDesc, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("liveResponseCreate", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        descriptionView.setVisibility(View.VISIBLE);
                        descriptionEditLayout.setVisibility(View.GONE);
                        descriptionView.setText(desc);
                    } else {
                        Toast.makeText(PageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateNameForPage(final String name) {

        Functions.Show_loader(this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            parameters.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        iosDialog.show();
        ApiRequest.Call_Api(PageActivity.this, Variables.updatePageName, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("liveResponseCreate", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        nameView.setVisibility(View.VISIBLE);
                        nameEditLayout.setVisibility(View.GONE);
                        nameView.setText(name);
                    } else {
                        Toast.makeText(PageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                ContentResolver cr = getContentResolver();
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
            final InputStream imageStream = getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            circleImageView.setImageBitmap(selectedImage);
            Log.d("imageType", type);
            if (type.equals("jpeg") || type.equals("jpg")) {
                encodedImage = encodeJpgImage(selectedImage);
            } else if (type.equals("png")) {
                encodedImage = encodepngImage(selectedImage);
            }
            if (encodedImage != null) {
                uploadImageForPage(encodedImage);
                Log.d("encodedImage", encodedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void uploadImageForPage(String encodedImage) {
        Functions.Show_loader(this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            parameters.put("pic", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        iosDialog.show();
        ApiRequest.Call_Api(PageActivity.this, Variables.updatePagePic, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("liveResponseCreate", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Toast.makeText(PageActivity.this, "Profile Pic Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}