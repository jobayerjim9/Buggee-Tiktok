package com.android.buggee.Accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class EmailSignUpFragment extends Fragment {
    TextInputLayout emailSignUp;
    private Context context;
//    IOSDialog iosDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_email_sign_up, container, false);
//        iosDialog = new IOSDialog.Builder(context)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();

        emailSignUp = v.findViewById(R.id.emailSignUp);
        Button signUpButton = v.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailSignUp.getEditText().getText().toString().trim();
                if (email.isEmpty()) {
                    emailSignUp.setErrorEnabled(true);
                    emailSignUp.setError("Cannot Be Empty");
                }
                else {
                    if (!email.contains("@") || !email.contains("."))
                    {
                        emailSignUp.setErrorEnabled(true);
                        emailSignUp.setError("Enter Valid Email Address");
                    }
                    else {
                        checkExist(email);
                    }
                }
            }
        });


        return v;



    }
    private void checkExist(final String email) {
//        iosDialog.show();
        Functions.Show_loader(context, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", email);

        } catch (JSONException e) {
            e.printStackTrace();
//            iosDialog.cancel();
            Functions.cancel_loader();
            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
        }


        ApiRequest.Call_Api(getContext(), Variables.checkExist, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Log.d("SignUpExist", resp);
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean exist = jsonObject.optBoolean("success");
                    if (exist) {
                        emailSignUp.setErrorEnabled(true);
                        emailSignUp.setError("Email is already exist");
                    } else {
                        Intent intent = new Intent(context, SignUpDetailsActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("type", "email");
                        context.startActivity(intent);
                        getActivity().finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }
}