package com.systematics.buggee.Accounts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.systematics.buggee.Main_Menu.MainMenuActivity;
import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.ApiRequest;
import com.systematics.buggee.SimpleClasses.Callback;
import com.systematics.buggee.SimpleClasses.Functions;
import com.systematics.buggee.SimpleClasses.Variables;
import com.systematics.buggee.SimpleClasses.WebViewActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login_A extends Activity {


    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
//    IOSDialog iosDialog;

    SharedPreferences sharedPreferences;

    View top_view;

    TextView login_title_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        }

        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_login);

        CardView signUp=findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_A.this, SignUpActivity.class));
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // if the user is already login trought facebook then we will logout the user automatically
        LoginManager.getInstance().logOut();
//
//        iosDialog = new IOSDialog.Builder(this)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);

        findViewById(R.id.facebook_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loginwith_FB();
            }
        });


        CardView signInButton=findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(Login_A.this,SignInActivity.class));
            }
        });
        findViewById(R.id.google_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sign_in_with_gmail();
            }
        });



        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

         top_view=findViewById(R.id.top_view);


        login_title_txt = findViewById(R.id.login_title_txt);
        login_title_txt.setText("You need buggee\naccount to continue");


        SpannableString ss = new SpannableString("By registering, you are agreeing with buggee’s “Terms of Use” and “Privacy policy”. We encourage you to read the terms of use carefully before sign up");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
//                "https://www.buggee.app/terms-of-service/"
                Open_Privacy_policy("https://www.buggee.app/privacy-policy/");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        //ss.setSpan(clickableSpan, 47, 62, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 66, 82, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TextView textView = (TextView) findViewById(R.id.login_terms_condition_txt);
        textView.setText(ss);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        printKeyHash();


    }


    public void Open_Privacy_policy(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        top_view.startAnimation(anim);
        top_view.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        top_view.setVisibility(View.GONE);
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }


    // Bottom two function are related to Fb implimentation
    private CallbackManager mCallbackManager;
    //facebook implimentation
    public void Loginwith_FB(){

        LoginManager.getInstance()
                .logInWithReadPermissions(Login_A.this,
                        Arrays.asList("public_profile","email"));

        // initialze the facebook sdk and request to facebook for login
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()  {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d("resp_token",loginResult.getAccessToken()+"");
            }

            @Override
            public void onCancel() {
                // App code
                Functions.showToast(Login_A.this, "Login Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("resp", "" + error.toString());
                Functions.showToast(Login_A.this, "Login Error" + error.toString());

            }

        });


    }

    private void handleFacebookAccessToken(final AccessToken token) {
        // if user is login then this method will call and
        // facebook will return us a token which will user for get the info of user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Log.d("resp_token",token.getToken()+"");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // iosDialog.show();
                            Functions.Show_loader(Login_A.this, false, false);
                            final String id = Profile.getCurrentProfile().getId();
                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                    Log.d("resp", user.toString());
                                    //after get the info of user we will pass to function which will store the info in our server

                                    String fname = "" + user.optString("first_name");
                                    String lname = "" + user.optString("last_name");


                                    if(fname.equals("") || fname.equals("null"))
                                        fname=getResources().getString(R.string.app_name);

                                    if(lname.equals("") || lname.equals("null"))
                                        lname="";

                                    Call_Api_For_Signup(""+id,fname
                                            ,lname,
                                            "https://graph.facebook.com/"+id+"/picture?width=500&width=500",
                                            "facebook");

                                }
                            });

                            // here is the request to facebook sdk for which type of info we have required
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "last_name,first_name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            Functions.showToast(Login_A.this, "Authentication failed.");
                        }

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        if(requestCode==123){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(mCallbackManager!=null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }



    //google Implimentation
    GoogleSignInClient mGoogleSignInClient;
    public void Sign_in_with_gmail(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Login_A.this);
        if (account != null) {
            String id=account.getId();
            String fname=""+account.getGivenName();
            String lname=""+account.getFamilyName();

            String pic_url;
            if(account.getPhotoUrl()!=null) {
                 pic_url = account.getPhotoUrl().toString();
            }else {
                pic_url="null";
            }



            if(fname.equals("") || fname.equals("null"))
                fname=getResources().getString(R.string.app_name);

            if(lname.equals("") || lname.equals("null"))
                lname="User";
            Call_Api_For_Signup(id,fname,lname,pic_url,"gmail");


        }
        else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 123);
        }

    }


    //Relate to google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String id=account.getId();
                String fname=""+account.getGivenName();
                String lname=""+account.getFamilyName();

                // if we do not get the picture of user then we will use default profile picture

                String pic_url;
                if(account.getPhotoUrl()!=null) {
                    pic_url = account.getPhotoUrl().toString();
                }else {
                    pic_url="null";
                }


                if(fname.equals("") || fname.equals("null"))
                    fname=getResources().getString(R.string.app_name);

                if(lname.equals("") || lname.equals("null"))
                    lname="";

                Call_Api_For_Signup(id,fname,lname,pic_url,"gmail");


            }
        } catch (ApiException e) {
            Log.w("Error message", "signInResult:failed code=" + e.getStatusCode());
        }

    }




    // this function call an Api for Signin
    private void Call_Api_For_Signup  (String id,
                                     String f_name,
                                     String l_name,
                                     String picture,
                                     String singnup_type) {


        PackageInfo packageInfo = null;
        try {
            packageInfo =getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appversion=packageInfo.versionName;

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("fb_id", id);
            parameters.put("first_name",""+f_name);
            parameters.put("last_name", ""+l_name);
            parameters.put("profile_pic", picture);
            parameters.put("gender", "m");
            parameters.put("version", appversion);
            parameters.put("signup_type", singnup_type);
            parameters.put("device", Variables.device);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.Show_loader(Login_A.this, false, false);
//        iosDialog.show();
        ApiRequest.Call_Api(this, Variables.SignUp, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
//                iosDialog.cancel();
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });

    }




    // if the signup successfull then this method will call and it store the user info in local
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
                editor.putInt(Variables.id_page, userdata.optInt("id_as_page"));
                editor.putString(Variables.u_pic, userdata.optString("profile_pic"));
                editor.putString(Variables.api_token, userdata.optString("tokon"));
                editor.putString(Variables.signUpType, userdata.optString("signup_type"));
                editor.putBoolean(Variables.islogin, true);
                editor.commit();

                Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
                Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");

                top_view.setVisibility(View.GONE);
                finish();
                startActivity(new Intent(this, MainMenuActivity.class));



            }else {
                Functions.showToast(Login_A.this, "" + jsonObject.optString("msg"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    // this function will print the keyhash of your project
    // which is very helpfull during Fb login implimentation
    public void printKeyHash()  {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName() , PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash" , Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}