# TlsSupportedWebView
[![license](https://img.shields.io/github/license/ohmae/TlsSupportedWebView.svg)](./LICENSE)
[![GitHub release](https://img.shields.io/github/release/ohmae/TlsSupportedWebView.svg)](https://github.com/ohmae/TlsSupportedWebView/releases)
[![GitHub issues](https://img.shields.io/github/issues/ohmae/TlsSupportedWebView.svg)](https://github.com/ohmae/TlsSupportedWebView/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/ohmae/TlsSupportedWebView.svg)](https://github.com/ohmae/TlsSupportedWebView/issues?q=is%3Aissue+is%3Aclosed)

## What is this?

This is a test implementation that enables TSL 1.2 in WebView of Android 4.1 - 4.3.

The trick is as follows.
```java
mWebView.setWebViewClient(new WebViewClient() {
    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
        return InterceptResponseDelegates.get().shouldInterceptRequest(view, url);
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
        InterceptResponseDelegates.get().setUserAgent(view.getSettings().getUserAgentString());
    }
});
```

## Screenshots

|before|after|
|--|--|
|![](docs/img/before.png)|![](docs/img/after.png)|

## Author
大前 良介 (OHMAE Ryosuke)
http://www.mm2d.net/

## License
[MIT License](./LICENSE)
