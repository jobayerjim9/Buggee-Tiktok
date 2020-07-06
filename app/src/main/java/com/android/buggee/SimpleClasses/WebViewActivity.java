package com.android.buggee.SimpleClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.buggee.R;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {
    private String session_id;
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String URL = getIntent().getStringExtra("url");
        findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        final Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Header-Key", session_id);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                view.getSettings().setJavaScriptEnabled(true);
                view.loadUrl(url, extraHeaders);
                Log.d("urlTapped", url);
                return false;
            }

        });
        webView.loadUrl(URL, extraHeaders);


//        webView.setWebViewClient(new WebViewClient() {
//
//            // Handle API until level 21
//            @SuppressWarnings("deprecation")
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//
//                return getNewResponse(url);
//            }
//
//            // Handle API 21+
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//
//                String url = request.getUrl().toString();
//
//                return getNewResponse(url);
//            }
//
//            private WebResourceResponse getNewResponse(String url) {
//
//                try {
//                    OkHttpClient httpClient = new OkHttpClient();
//                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_file), Context.MODE_PRIVATE);
//                    session_id=sharedPref.getString("session_id",null);
//                    Request request = new Request.Builder()
//                            .url(url.trim())
//                            .addHeader("Header-Key", session_id) // Example header
//                            .build();
//
//                    Response response = httpClient.newCall(request).execute();
//
//                    return new WebResourceResponse(
//                            null,
//                            response.header("content-encoding", "utf-8"),
//                            response.body().byteStream()
//                    );
//
//                } catch (Exception e) {
//                    return null;
//                }
//
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}