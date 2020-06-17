package com.android.buggee.Accounts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.buggee.R;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;


public class PhoneSignUpFragment extends Fragment {

    CountryCodePicker ccp;
    TextInputLayout phoneSignUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_phone_sign_up, container, false);

        ccp=v.findViewById(R.id.ccp);
        phoneSignUp=v.findViewById(R.id.phoneSignUp);
        ccp.registerCarrierNumberEditText(phoneSignUp.getEditText());
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code
                if (isValidNumber){

                }
                else {


                }
            }
        });

        ImageView nextPhoneSignUp=v.findViewById(R.id.nextPhoneSignUp);
        nextPhoneSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ccp.isValidFullNumber()) {
                    phoneSignUp.setErrorEnabled(false);
                    Toast.makeText(getContext(), ccp.getFullNumber(), Toast.LENGTH_SHORT).show();
                }
                else {
                    phoneSignUp.setErrorEnabled(true);
                    phoneSignUp.setError("Phone Number is not valid");
                }
            }
        });





        return v;
    }
}