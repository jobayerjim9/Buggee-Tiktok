package com.android.buggee.Main_Menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.Variables;
import com.android.buggee.Video_Recording.LiveBroadcasterActivity;
import com.android.buggee.Video_Recording.Video_Recoder_A;
import com.android.buggee.WatchVideos.LiveDetailsDialog;

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
                    Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show();
//                }
//                dismiss();
//            }
//        });

        liveChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                    LiveDetailsDialog liveDetailsDialog = new LiveDetailsDialog();
                    liveDetailsDialog.show(getChildFragmentManager(), "liveDetails");

                } else {
                    Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show();
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
