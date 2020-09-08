package com.android.buggee.Video_Recording;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.DialogFragment;

import com.android.buggee.Profile.Profile_F;
import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.WatchVideos.LiveDetailsDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.android.buggee.Main_Menu.MainMenuFragment.hasPermissions;

public class StoryModeChooser extends DialogFragment {
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
        TextView imageText = v.findViewById(R.id.imageText);
        imageText.setText("Take A Picture");

        recordChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Video_Recoder_A.class);
                intent.putExtra("type", "story");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
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
//                    Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show();
//                }
//                dismiss();
//            }
//        });

        liveChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_permissions())
                    openCameraIntent();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        } else {

            return true;
        }

        return false;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(), getActivity().getPackageName() + ".fileprovider", photoFile);
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
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    byte[] image_byte_array;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
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
                imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
            Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);

            Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, (int) (rotatedBitmap.getWidth() * 0.7), (int) (rotatedBitmap.getHeight() * 0.7), true);


            saveImageToStorage(resized);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//            image_byte_array = baos.toByteArray();
//            Save_Image();

        }
    }

    private void saveImageToStorage(Bitmap finalBitmap) {
        Functions.Show_loader(context, false, false);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Buggee/");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "temp" + timeStamp + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Variables.output_story = fname;
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(context, "Opening Picture For Editing!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, ImageEditingActivity.class));
            Functions.cancel_loader();
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void Save_Image() {

        Functions.Show_loader(context, false, false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String key = reference.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference filelocation = storageReference.child("Story")
                .child(key + ".jpg");

        filelocation.putBytes(image_byte_array).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filelocation.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Call_Api_For_image(uri.toString());
                        }
                    });
                } else {
                    Functions.cancel_loader();
                }
            }
        });


    }

    public void Call_Api_For_image(final String image_link) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("url", image_link);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.createImageStory, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject response = new JSONObject(resp);
                    boolean success = response.optBoolean("success");
                    if (success) {
                        Toast.makeText(context, "Story Added!", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
