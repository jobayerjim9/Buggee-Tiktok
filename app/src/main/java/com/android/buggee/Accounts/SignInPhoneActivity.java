package com.android.buggee.Accounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class SignInPhoneActivity extends AppCompatActivity {
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    CountryCodePicker ccp;
    TextInputLayout phoneSignUp,otpSignUp;
    LinearLayout phoneInputLayout;
    //    IOSDialog iosDialog;
    String phoneNumber;
    SharedPreferences sharedPreferences;
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_phone);
        initUi();
    }

    private void initUi() {
//        iosDialog = new IOSDialog.Builder(this)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        ccp = findViewById(R.id.ccp);
        phoneSignUp = findViewById(R.id.phoneSignUp);
        otpSignUp = findViewById(R.id.otpSignUp);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        ccp.registerCarrierNumberEditText(phoneSignUp.getEditText());
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code
                if (isValidNumber) {

                } else {


                }
            }
        });

        final ImageView nextPhoneSignUp = findViewById(R.id.nextPhoneSignUp);
        final ImageView doneButtonSignIn = findViewById(R.id.doneButtonSignIn);
        doneButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otpSignUp.getEditText().getText().toString();
                if (otp.isEmpty()) {
                    otpSignUp.setErrorEnabled(true);
                    otpSignUp.setError("Enter An OTP");
                } else {
                    otpSignUp.setErrorEnabled(false);
                    verifyVerificationCode(otp);
                }
            }
        });
        nextPhoneSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ccp.isValidFullNumber()) {
                    phoneSignUp.setErrorEnabled(false);
                    phoneNumber = "+" + ccp.getFullNumber();
                    checkPhoneExist(phoneNumber);
                    startPhoneNumberVerification(phoneNumber);
                } else {
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
                loginWithPhoneNumber(phoneNumber);
//                iosDialog.cancel();
                Functions.cancel_loader();
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


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(SignInPhoneActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(SignInPhoneActivity.this, "Quota exceeded", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(SignInPhoneActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
//                iosDialog.cancel();
                Functions.cancel_loader();
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
                doneButtonSignIn.setVisibility(View.VISIBLE);
//                iosDialog.cancel();
                Functions.cancel_loader();
                // [START_EXCLUDE]
                // Update UI

                // [END_EXCLUDE]
            }
        };



    }

    private void checkPhoneExist(final String phoneNumber) {

        Functions.Show_loader(SignInPhoneActivity.this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("phone", phoneNumber);

        } catch (JSONException e) {
            e.printStackTrace();
            Functions.cancel_loader();
            Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
        }


        ApiRequest.Call_Api(this, Variables.checkphoneExist, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Log.d("SignUpExist", resp);
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    boolean exist=jsonObject.optBoolean("success");
                    if (exist) {
                        startPhoneNumberVerification(phoneNumber);
                        phoneSignUp.setErrorEnabled(false);
                    }
                    else {
                        phoneSignUp.setErrorEnabled(true);
                        phoneSignUp.setError("Phone Number is not exist");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignInPhoneActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void loginWithPhoneNumber(String phoneNumber) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("phone", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        iosDialog.show();
        Functions.Show_loader(SignInPhoneActivity.this, false, false);
        ApiRequest.Call_Api(this, Variables.loginPhone, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_signup_data(resp);
            }
        });


    }
    public void Parse_signup_data(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.u_id, userdata.optString("fb_id"));
                editor.putString(Variables.f_name, userdata.optString("first_name"));
                editor.putString(Variables.l_name, userdata.optString("last_name"));
                editor.putString(Variables.u_name, userdata.optString("first_name") + " " + userdata.optString("last_name"));
                editor.putString(Variables.gender, userdata.optString("gender"));
                editor.putString(Variables.bio, userdata.optString("bio"));
                editor.putInt(Variables.page_have, userdata.optInt("page_have"));
                editor.putString(Variables.u_pic, userdata.optString("profile_pic"));
                editor.putString(Variables.api_token, userdata.optString("tokon"));
                editor.putBoolean(Variables.islogin, true);
                editor.commit();

                Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
                Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");

                Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                finish();



            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void startPhoneNumberVerification(String phoneNumber) {
//        iosDialog.show();
        Functions.Show_loader(SignInPhoneActivity.this, false, false);
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyVerificationCode(String otp) {
        try {
            //creating the credential
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

            //signing the user
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something Wrong With Server! Try Again Later", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignInPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loginWithPhoneNumber(phoneNumber);
                            //verification successful we will start the profile activity


                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(SignInPhoneActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}