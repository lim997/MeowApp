package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Button3Activity extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button3);

        mWebView = (WebView)findViewById(R.id.camera_web);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("http://220.66.60.211:8080/Meow/image/imageView.jsp");
    }

    public void Button0(View v){
        finish();
    }

    public void Button1(View v) {
        finish();
        startActivity(new Intent(this,Button1Activity.class));
    }

    public void Button2(View v) {
        finish();
        startActivity(new Intent(this,Button2Activity.class));
    }

    public void Button3(View v) {    }

}
