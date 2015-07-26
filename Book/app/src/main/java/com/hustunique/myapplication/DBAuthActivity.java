package com.hustunique.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import util.Constant;

/**
 * Created by taozhiheng on 15-7-21.
 * Activity for douban login or register
 */
public class DBAuthActivity extends AppCompatActivity{


    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        mWebView = (WebView) findViewById(R.id.db_webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        mWebView.loadUrl(Constant.DB_AUTH_URL);
        //设置Web视图
        mWebView.setWebViewClient(new HelloWebViewClient());
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Toast.makeText(getBaseContext(), url, Toast.LENGTH_SHORT).show();
            Log.d("net", "url:" + url);
            if(url.contains(Constant.DB_REDIRECT_URL)) {
                String code = url.substring(url.indexOf('?') + 1);
                Intent intent = new Intent();
                intent.putExtra(Constant.AUTH_CODE, code);
                setResult(RESULT_OK, intent);
                finish();
            }
            else
                view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }
}
