/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.tlssupportedwebview;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import net.mm2d.tlssupportedwebview.delegate.InterceptResponseDelegate;
import net.mm2d.tlssupportedwebview.delegate.InterceptResponseDelegateFactory;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class MainActivity extends AppCompatActivity {
    private InterceptResponseDelegate mDelegate;
    private ActionBar mActionBar;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private MenuItem mBackMenu;
    private MenuItem mForwardMenu;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> showEditUrlDialog());
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("");

        mWebView = findViewById(R.id.webView);
        final WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        mDelegate = InterceptResponseDelegateFactory.create();
        mDelegate.setUserAgent(settings.getUserAgentString());

        mProgressBar = findViewById(R.id.progressBar);

        setWebChromeClient();
        setWebViewClient();
        mWebView.loadUrl("https://www.ssllabs.com/ssltest/viewMyClient.html");
    }

    private void showEditUrlDialog() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {
                    final String input = editText.getText().toString();
                    if (PatternsCompat.WEB_URL.matcher(input).matches()) {
                        mWebView.loadUrl(input);
                    } else {
                        mWebView.loadUrl("https://search.yahoo.co.jp/search?p=" + input);
                    }
                })
                .show();
    }

    private void setWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(final WebView view, final String title) {
                mActionBar.setTitle(title);
            }

            @Override
            public void onProgressChanged(final WebView view, final int newProgress) {
                mProgressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        });
    }

    private void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
                return mDelegate.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                mActionBar.setTitle("");
                mActionBar.setSubtitle(url);
            }

            @Override
            public void doUpdateVisitedHistory(final WebView view, final String url, final boolean isReload) {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mBackMenu = menu.findItem(R.id.action_back);
        mForwardMenu = menu.findItem(R.id.action_forward);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        mBackMenu.setEnabled(mWebView.canGoBack());
        mBackMenu.getIcon().setAlpha(mWebView.canGoBack() ? 0xff : 0x40);
        mForwardMenu.setEnabled(mWebView.canGoForward());
        mForwardMenu.getIcon().setAlpha(mWebView.canGoForward() ? 0xff : 0x40);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                mWebView.goBack();
                break;
            case R.id.action_forward:
                mWebView.goForward();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
