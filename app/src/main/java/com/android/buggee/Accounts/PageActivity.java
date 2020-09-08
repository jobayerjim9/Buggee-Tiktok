package com.android.buggee.Accounts;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.Profile.Liked_Videos.Liked_Video_F;
import com.android.buggee.Profile.PageSettingsFragment;
import com.android.buggee.Profile.Profile_Tab_F;
import com.android.buggee.Profile.UserVideos.UserVideo_F;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.android.buggee.Main_Menu.MainMenuFragment.hasPermissions;

public class PageActivity extends AppCompatActivity {
    private TextView nameView, descriptionView, pageCategory;
    //    private EditText nameEditText, descriptionEditText;
    private ImageView circleImageView, nameSubmit, descSubmit;
    //    private LinearLayout nameEditLayout, descriptionEditLayout;
    final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String encodedImage = null;
    protected TabLayout tabLayout;
    private Button requestDeletePage;
    protected ViewPager pager;
    private String id;
    private ViewPagerAdapter adapter;
    private String pageId, pageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        id = getIntent().getStringExtra("id");
        if (id == null) {
            id = Variables.sharedPreferences.getString(Variables.u_id, "");
        }
        getPageInfo();
        nameView = findViewById(R.id.pageName);
        pageCategory = findViewById(R.id.pageCategory);
        //descriptionView = findViewById(R.id.descriptionView);
        // nameEditText = findViewById(R.id.nameEditText);
        //descriptionEditText = findViewById(R.id.descriptionEditText);
        circleImageView = findViewById(R.id.user_image);
        requestDeletePage = findViewById(R.id.requestDeletePage);
        //nameSubmit = findViewById(R.id.nameSubmit);
        //descSubmit = findViewById(R.id.descSubmit);
        //nameEditLayout = findViewById(R.id.nameEditLayout);
        //descriptionEditLayout = findViewById(R.id.descriptionEditLayout);
//        findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        if (id.equals(Variables.sharedPreferences.getString(Variables.u_id, ""))) {
            requestDeletePage.setVisibility(View.VISIBLE);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectImage();

                }
            });
        } else {
            requestDeletePage.setVisibility(View.GONE);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);

        adapter = new ViewPagerAdapter(this.getResources(), getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 50, 0);
            tab.requestLayout();
        }
//        nameView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nameView.setVisibility(View.GONE);
//                nameEditLayout.setVisibility(View.VISIBLE);
//                nameEditText.requestFocus();
//            }
//        });
//        nameSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = nameEditText.getText().toString();
//                if (name.isEmpty()) {
//                    Toast.makeText(PageActivity.this, "Name Cannot Be Empty!", Toast.LENGTH_SHORT).show();
//                } else {
//                    updateNameForPage(name);
//                }
//            }
//        });
//        descriptionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                descriptionView.setVisibility(View.GONE);
//                descriptionEditLayout.setVisibility(View.VISIBLE);
//                descriptionEditText.requestFocus();
//            }
//        });
//        descSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String desc = descriptionEditText.getText().toString();
//                if (desc.isEmpty()) {
//                    Toast.makeText(PageActivity.this, "Description Cannot Be Empty!", Toast.LENGTH_SHORT).show();
//                } else {
//                    updateDescForPage(desc);
//                }
//            }
//        });


    }
    private void getPageInfo() {
        Functions.Show_loader(this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", id);
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
                        Variables.sharedPreferences.edit().putString(Variables.page_name, name).apply();
                        String id = jsonObject.optString("id");
                        Variables.sharedPreferences.edit().putString(Variables.p_id, id).apply();
                        String profile_pic = jsonObject.optString("profile_pic");
                        String description = jsonObject.optString("description");
                        String category = jsonObject.optString("category");
                        int published = jsonObject.optInt("published");
                        Variables.sharedPreferences.edit().putInt(Variables.page_published, published).apply();
                        pageCategory.setText(category);
                        Picasso.with(PageActivity.this).load(profile_pic).placeholder(R.drawable.profile_image_placeholder).into(circleImageView);
                        nameView.setText(name);
                        //nameEditText.setText(name);
//                        if (!description.toLowerCase().equals("null")) {
//                            descriptionView.setText(description);
//                           // descriptionEditText.setText(description);
//                        } else {
//                            descriptionView.setText("Add Your Description!");
//                        }
                    } else {
                        Toast.makeText(PageActivity.this, "Something Wrong With Your Page", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void updateDescForPage(final String desc) {
//        Functions.Show_loader(this, false, false);
//        JSONObject parameters = new JSONObject();
//        try {
//            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
//            parameters.put("desc", desc);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        iosDialog.show();
//        ApiRequest.Call_Api(PageActivity.this, Variables.updatePageDesc, parameters, new Callback() {
//            @Override
//            public void Responce(String resp) {
////                iosDialog.cancel();
//                Functions.cancel_loader();
//                Log.d("liveResponseCreate", resp);
//                try {
//                    JSONObject jsonObject = new JSONObject(resp);
//                    boolean success = jsonObject.optBoolean("success");
//                    if (success) {
//                        descriptionView.setVisibility(View.VISIBLE);
//                        descriptionEditLayout.setVisibility(View.GONE);
//                        descriptionView.setText(desc);
//                    } else {
//                        Toast.makeText(PageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    private void updateNameForPage(final String name) {
//
//        Functions.Show_loader(this, false, false);
//        JSONObject parameters = new JSONObject();
//        try {
//            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
//            parameters.put("name", name);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        iosDialog.show();
//        ApiRequest.Call_Api(PageActivity.this, Variables.updatePageName, parameters, new Callback() {
//            @Override
//            public void Responce(String resp) {
////                iosDialog.cancel();
//                Functions.cancel_loader();
//                Log.d("liveResponseCreate", resp);
//                try {
//                    JSONObject jsonObject = new JSONObject(resp);
//                    boolean success = jsonObject.optBoolean("success");
//                    if (success) {
//                        nameView.setVisibility(View.VISIBLE);
//                        nameEditLayout.setVisibility(View.GONE);
//                        nameView.setText(name);
//                    } else {
//                        Toast.makeText(PageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    byte[] image_byte_array;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                if (data == null) {
                    //Display an error
                    return;
                }
                Uri uri = data.getData();
                String fileType = getMimeType(uri);
                String[] splitedType = fileType.split("/", 2);
                Log.d("SplitedType", splitedType[0] + " " + splitedType[1]);
                storeImage(uri, splitedType[1]);
            }
            if (requestCode == 1) {
                Matrix matrix = new Matrix();
                try {
                    ExifInterface exif = new ExifInterface(imageFilePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);

                Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, (int) (rotatedBitmap.getWidth() * 0.7), (int) (rotatedBitmap.getHeight() * 0.7), true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 20, baos);

                image_byte_array = baos.toByteArray();
                String encImage = Base64.encodeToString(image_byte_array, Base64.DEFAULT);
                uploadImageForPage(encImage);

            }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(PageActivity.this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        } else {

            return true;
        }

        return false;
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

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this, R.style.AlertDialogCustom);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (check_permissions())
                            openCameraIntent();
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    if (ContextCompat.checkSelfPermission(PageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        ActivityCompat.requestPermissions(PageActivity.this, permissions, 1);
                    } else {
                        pickImage();
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, 1);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
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

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final Resources resources;

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new UserVideo_F(id, "page");
                    break;
                case 1:
                    result = new Liked_Video_F(id);
                    break;
                case 2:
                    result = new PageSettingsFragment();
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            if (id.equals(Variables.sharedPreferences.getString(Variables.u_id, ""))) {
                return 3;
            } else {
                return 2;
            }
        }


        @Override
        public CharSequence getPageTitle(final int position) {
            if (position == 0) {
                return "Posts";
            } else if (position == 1) {
                return "Saved";
            } else if (position == 2) {
                return "Settings";
            } else {
                return null;
            }
        }


    }


}