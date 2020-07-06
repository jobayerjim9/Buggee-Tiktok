package com.android.buggee.Accounts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.Main_Menu.MainMenuActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SignUpDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    int activeView=1;
    TextInputLayout firstNameInput,lastNameInput,passwordInput,usernameInput;
    Button datePicker;
    Spinner genderPicker;
    CardView spinnerCard;
    String fname,lname,password,username,dob,gender,email;
    SharedPreferences sharedPreferences;
    //    IOSDialog iosDialog;
    ImageView nextEmailSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);
        email = getIntent().getStringExtra("email");
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        usernameInput = findViewById(R.id.usernameInput);
        spinnerCard = findViewById(R.id.spinnerCard);
        datePicker = findViewById(R.id.datePicker);
        genderPicker = findViewById(R.id.genderPicker);
//        iosDialog = new IOSDialog.Builder(this)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();
        genderPicker.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, R.layout.gender_spinner_item);
        adapter.setDropDownViewResource(R.layout.gender_spinner_item);
        genderPicker.setAdapter(adapter);
        genderPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(SignUpDetailsActivity.this, "Select A Gender!", Toast.LENGTH_SHORT).show();
                    gender=null;
                }
                else if(i==1) {
                    gender="m";
                }
                else if(i==2) {
                    gender="f";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog dialog = new DatePickerDialog(SignUpDetailsActivity.this, SignUpDetailsActivity.this,
                        2003, 12,
                        31);
                dialog.show();
            }
        });
        nextEmailSignUp=findViewById(R.id.nextEmailSignUp);
        nextEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewRecycleForward();
            }
        });
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewRecycleBackrward();
            }
        });

    }


    private void viewRecycleForward() {
        if (activeView==1) {
            hideAllView();
            firstNameInput.setVisibility(View.VISIBLE);
            lastNameInput.setVisibility(View.VISIBLE);
            fname=firstNameInput.getEditText().getText().toString().trim();
            lname=lastNameInput.getEditText().getText().toString().trim();

            if (fname.isEmpty() || lname.isEmpty()) {
                if (fname.isEmpty()) {
                    firstNameInput.setErrorEnabled(true);
                    firstNameInput.setError("First Name Cannot Be Empty");
                }
                else {
                    firstNameInput.setErrorEnabled(false);
                }
                if (lname.isEmpty()) {
                    lastNameInput.setErrorEnabled(true);
                    lastNameInput.setError("Last Name Cannot Be Empty");
                }
                else {
                    lastNameInput.setErrorEnabled(false);
                }
            }
            else {
                activeView=2;
                hideAllView();
                passwordInput.setVisibility(View.VISIBLE);
            }
        }
        else if (activeView==2) {
            password=passwordInput.getEditText().getText().toString().trim();
            if (password.isEmpty() || password.length()<8) {
                if (password.isEmpty()) {
                    passwordInput.setErrorEnabled(true);
                    passwordInput.setError("Password Cannot Be Empty");
                }
                else {
                    passwordInput.setErrorEnabled(false);
                }
                if (password.length()<8) {
                    passwordInput.setErrorEnabled(true);
                    passwordInput.setError("Password Cannot Be Less Then 8 Characters");
                }
                else {
                    passwordInput.setErrorEnabled(false);
                }
            }
            else {
                activeView=3;
                hideAllView();
                usernameInput.setVisibility(View.VISIBLE);

            }
        }
        else if (activeView==3) {
            username = usernameInput.getEditText().getText().toString().trim();
            if (username.isEmpty()) {
                usernameInput.setErrorEnabled(true);
                usernameInput.setError("Password Cannot Be Empty");
            } else {
                checkUsernameExist(username);
            }
        }
        else if (activeView==4) {
            if (dob!=null)
            {
                activeView=5;
                hideAllView();
                spinnerCard.setVisibility(View.VISIBLE);
                nextEmailSignUp.setImageDrawable(getDrawable(R.drawable.submit_blue));
            }
            else {
                Toast.makeText(this, "Choose Yor Date Of Birth!", Toast.LENGTH_SHORT).show();
            }

        }
        else if (activeView==5) {
            if (gender!=null) {
                signUp();
            }
            else {
                Toast.makeText(this, "Choose Yor Gender!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void signUp() {
        PackageInfo packageInfo = null;
        try {
            packageInfo =getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appversion=packageInfo.versionName;

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("email", email);
            parameters.put("password", password);
            parameters.put("dob",dob);
            parameters.put("username",username);
            parameters.put("first_name",fname);
            parameters.put("last_name", lname);
            parameters.put("profile_pic"," ");
            parameters.put("gender",gender);
            parameters.put("version",appversion);
            parameters.put("signup_type","email");
            parameters.put("device", Variables.device);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(SignUpDetailsActivity.this, false, false);
        ApiRequest.Call_Api(this, Variables.signUpEmail, parameters, new Callback() {
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
            if(code.equals("200")){
                JSONArray jsonArray=jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.u_id,userdata.optString("fb_id"));
                editor.putString(Variables.f_name,userdata.optString("first_name"));
                editor.putString(Variables.l_name,userdata.optString("last_name"));
                editor.putString(Variables.u_name,userdata.optString("first_name")+" "+userdata.optString("last_name"));
                editor.putString(Variables.gender,userdata.optString("gender"));
                editor.putString(Variables.u_pic,userdata.optString("profile_pic"));
                editor.putString(Variables.api_token,userdata.optString("tokon"));
                editor.putBoolean(Variables.islogin,true);
                editor.commit();

                Variables.sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);
                Variables.user_id=Variables.sharedPreferences.getString(Variables.u_id,"");

                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                finish();



            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void viewRecycleBackrward() {
        if (activeView>0) {
            activeView--;
        }
        else if (activeView==0) {
            finish();
        }
        if (activeView==1) {
            hideAllView();
            firstNameInput.setVisibility(View.VISIBLE);
            lastNameInput.setVisibility(View.VISIBLE);
        }
        else if (activeView==2) {
            hideAllView();
            passwordInput.setVisibility(View.VISIBLE);
        }
        else if (activeView==3) {
            hideAllView();
            usernameInput.setVisibility(View.VISIBLE);
        }
        else if (activeView==4) {
            hideAllView();
            datePicker.setVisibility(View.VISIBLE);
        }
        else if (activeView==5) {
            hideAllView();
            spinnerCard.setVisibility(View.VISIBLE);
        }

    }
    private void hideAllView() {
        firstNameInput.setVisibility(View.GONE);
        lastNameInput.setVisibility(View.GONE);
        passwordInput.setVisibility(View.GONE);
        usernameInput.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
        spinnerCard.setVisibility(View.GONE);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Date date=new Date();
        Log.d("Years",date.getYear()+" "+i);
        if (((date.getYear()+1900)-i)<13) {
            Toast.makeText(this, "Your age is below 13", Toast.LENGTH_SHORT).show();
        }
        else {
            dob = (i) + "-" + (i1 + 1) + "-" + i2;
            String monthName = "Your Date Of Birth is\n" + i2 + " " + getMonthName(i1) + " " + i;
            this.datePicker.setText(monthName);
        }

    }

    private String getMonthName(int month) {
        if (month==0) {
            return "January";
        } else if (month==1) {
            return "February";
        } else if (month==2) {
            return "March";
        } else if (month==3) {
            return "April";
        } else if (month==4) {
            return "May";
        } else if (month==5) {
            return "June";
        } else if (month==6) {
            return "July";
        } else if (month==7) {
            return "August";
        } else if (month==8) {
            return "September";
        } else if (month==9) {
            return "October";
        } else if (month==10) {
            return "November";
        }  else  {
            return "December";
        }

    }
    private void checkUsernameExist(final String username)
    {
        Functions.Show_loader(SignUpDetailsActivity.this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("username", username);

        } catch (JSONException e) {
            e.printStackTrace();
            Functions.cancel_loader();
            Toast.makeText(SignUpDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
        }


        ApiRequest.Call_Api(SignUpDetailsActivity.this, Variables.checkUsernameExist, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Log.d("SignUpExist", resp);
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    boolean exist=jsonObject.optBoolean("success");
                    if (exist) {
                        usernameInput.setErrorEnabled(true);
                        usernameInput.setError("Username is already exist");
                    }
                    else {
                        usernameInput.setErrorEnabled(false);
                        activeView = 4;
                        hideAllView();
                        datePicker.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpDetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}