package com.android.buggee.Accounts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;


public class PhoneSignUpFragment extends Fragment {
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    IOSDialog iosDialog;
    CountryCodePicker ccp;
    TextInputLayout phoneSignUp,otpSignUp;
    LinearLayout phoneInputLayout;
    private boolean mVerificationInProgress = false;
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_phone_sign_up, container, false);
        iosDialog = new IOSDialog.Builder(getContext())
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();
        ccp=v.findViewById(R.id.ccp);
        phoneSignUp=v.findViewById(R.id.phoneSignUp);
        otpSignUp=v.findViewById(R.id.otpSignUp);
        phoneInputLayout=v.findViewById(R.id.phoneInputLayout);
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

        final ImageView nextPhoneSignUp=v.findViewById(R.id.nextPhoneSignUp);
        nextPhoneSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ccp.isValidFullNumber()) {
                    phoneSignUp.setErrorEnabled(false);
                    phoneNumber="+"+ccp.getFullNumber();
                    checkPhoneExist(phoneNumber);

                }
                else {
                    phoneSignUp.setErrorEnabled(true);
                    phoneSignUp.setError("Phone Number is not valid");
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to contact or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                // [START_EXCLUDE silent]
                iosDialog.cancel();
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // [END_EXCLUDE]
                String code = credential.getSmsCode();
                Log.d("onVerificationCompleted", code + "");
                otpSignUp.getEditText().setText(code);
                phoneInputLayout.setVisibility(View.GONE);
                otpSignUp.setVisibility(View.VISIBLE);
                Intent intent=new Intent(getContext(),SignUpDetailsActivity.class);
                intent.putExtra("email",phoneNumber);
                getContext().startActivity(intent);
                getActivity().finish();
                // signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                iosDialog.cancel();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(getContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(getContext(), "Quota exceeded", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
                else if (e instanceof FirebaseNetworkException)
                {
                    Toast.makeText(getContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                phoneInputLayout.setVisibility(View.GONE);
                otpSignUp.setVisibility(View.VISIBLE);
                nextPhoneSignUp.setVisibility(View.GONE);
                // [START_EXCLUDE]
                // Update UI
                iosDialog.cancel();
                // [END_EXCLUDE]
            }
        };



        return v;
    }

    private void checkPhoneExist(final String phoneNumber) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("phone", phoneNumber);

        } catch (JSONException e) {
            e.printStackTrace();
            iosDialog.cancel();
            Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
        }


        ApiRequest.Call_Api(getContext(), Variables.checkphoneExist, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                iosDialog.cancel();
                Log.d("SignUpExist",resp);
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    boolean exist=jsonObject.optBoolean("success");
                    if (exist) {
                        phoneSignUp.setErrorEnabled(true);
                        phoneSignUp.setError("Phone Number is already exist");
                    }
                    else {
                        startPhoneNumberVerification(phoneNumber);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        iosDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

}