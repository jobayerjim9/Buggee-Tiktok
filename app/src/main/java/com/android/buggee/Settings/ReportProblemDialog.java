package com.android.buggee.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ReportProblemDialog extends DialogFragment {
    private Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.report_problem_dialog, null);
        final EditText problemText = v.findViewById(R.id.problemText);
        ImageView problemSubmit = v.findViewById(R.id.problemSubmit);
        problemSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prob = problemText.getText().toString();
                if (prob.isEmpty()) {
                    Toast.makeText(context, "Write Your Problem Please!", Toast.LENGTH_SHORT).show();
                } else {
                    reportProblem(prob);
                }

            }
        });

        builder.setView(v);
        return builder.create();
    }

    private void reportProblem(String prob) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("reported_problem", prob);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.reportProblem, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Toast.makeText(context, "Reported!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }
}
