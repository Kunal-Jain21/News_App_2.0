package com.example.newsapp20;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {

    //Ui
    private FrameLayout frameLayout;
    private ProgressBar progressBar;
    private WebView webView;
    private String url = "";
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // init actionbar
        actionBar = getSupportActionBar();
        //back button in actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init UI View
        frameLayout = findViewById(R.id.frameLayout);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);

        // url from intent
        url = getIntent().getStringExtra("url");

        webView.setWebViewClient(new HelpClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);

                actionBar.setTitle("Loading");
                if (newProgress == 100) {
                    frameLayout.setVisibility(View.GONE);
                    actionBar.setTitle(view.getTitle());
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(url);
        progressBar.setProgress(0);
    }

    private class HelpClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            frameLayout.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Toast.makeText(NewsDetailActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            webView.reload();
        } else if (id == R.id.action_copy) {
            copyUrl();
        } else if (id == R.id.action_share){
            shareUrl();
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyUrl() {
        String urlToCopy = webView.getUrl();

        ClipboardManager cb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Url Link", urlToCopy);
        cb.setPrimaryClip(clipData);
        Toast.makeText(this, "URL copied...", Toast.LENGTH_SHORT).show();
    }

    private void shareUrl() {
        String urlToShare = webView.getUrl();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));
    }

}