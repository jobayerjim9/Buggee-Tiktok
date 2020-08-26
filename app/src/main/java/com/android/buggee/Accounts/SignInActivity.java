package com.android.buggee.Accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    TextInputLayout emailUsernameInput,passwordInput;
    SharedPreferences sharedPreferences;

    //    IOSDialog iosDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUi();
    }

    private void initUi() {
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
//        iosDialog = new IOSDialog.Builder(this)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();
        TextView loginViaMobile = findViewById(R.id.loginViaMobile);
        emailUsernameInput = findViewById(R.id.emailUsernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginViaMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignInPhoneActivity.class));
                finish();
            }
        });
        ImageView loginButtonEmail=findViewById(R.id.loginButtonEmail);
        loginButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailusername=emailUsernameInput.getEditText().getText().toString().trim();
                String password=passwordInput.getEditText().getText().toString().trim();
                if (emailusername.isEmpty())
                {
                    emailUsernameInput.setErrorEnabled(true);
                    emailUsernameInput.setError("Cannot Be Empty");
                }
                else if (password.isEmpty()) {
                    passwordInput.setErrorEnabled(true);
                    passwordInput.setError("Cannot Be Empty");
                }
                else if (emailusername.contains("@") && emailusername.contains(".")) {
                    signInViaEmail(emailusername,password);
                }
                else {
                    signInViaUsername(emailusername,password);
                }
            }
        });


    }

    private void signInViaUsername(String emailusername, String password) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("username", emailusername);
            parameters.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.Show_loader(SignInActivity.this, false, true);
//        iosDialog.show();
        ApiRequest.Call_Api(this, Variables.loginUsername, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });
    }

    private void signInViaEmail(String emailusername, String password) {
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("email", emailusername);
            parameters.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        iosDialog.show();
        Functions.Show_loader(SignInActivity.this, false, false);
        ApiRequest.Call_Api(this, Variables.loginEmail, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });
    }

    public void Parse_signup_data(String loginData){
        try {
            Log.d("signInData", loginData);
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
                editor.putInt(Variables.id_page, userdata.optInt("id_as_page"));
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
}