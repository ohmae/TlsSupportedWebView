/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.tlssupportedwebview.delegate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class InterceptResponseDelegatePreLollipop implements InterceptResponseDelegate {
    @NonNull
    private final OkHttpClient mOkHttpClient;
    @NonNull
    private String mUserAgent = "";

    private InterceptResponseDelegatePreLollipop(@NonNull final OkHttpClient client) {
        mOkHttpClient = client;
    }

    @Override
    public void setUserAgent(@NonNull final String userAgent) {
        mUserAgent = userAgent;
    }

    @Override
    @Nullable
    public WebResourceResponse shouldInterceptRequest(
            @NonNull final WebView view,
            @NonNull final String url) {
        if (!URLUtil.isNetworkUrl(url)) {
            return null;
        }
        try {
            final Request request = new Request.Builder()
                    .addHeader("User-Agent", mUserAgent)
                    .url(url)
                    .build();
            final Response response = mOkHttpClient.newCall(request).execute();
            final ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            final MediaType type = body.contentType();
            if (type == null) {
                return null;
            }
            final Charset charset = type.charset();
            return new WebResourceResponse(type.type() + "/" + type.subtype(),
                    charset != null ? charset.name() : null, body.byteStream());
        } catch (final IOException ignored) {
        }
        return null;
    }

    @NonNull
    static InterceptResponseDelegate newInstance() {
        final OkHttpClient client = createTlsClient();
        if (client == null) {
            return new InterceptResponseDelegateEmpty();
        }
        return new InterceptResponseDelegatePreLollipop(client);
    }

    @Nullable
    private static OkHttpClient createTlsClient() {
        final CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        try {
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                return null;
            }
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            builder.sslSocketFactory(new TlsSocketFactory(sslContext.getSocketFactory()), (X509TrustManager) trustManagers[0]);

            final ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                    .build();
            final List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(connectionSpec);
            specs.add(ConnectionSpec.COMPATIBLE_TLS);
            specs.add(ConnectionSpec.CLEARTEXT);
            builder.connectionSpecs(specs);
        } catch (final Exception ignored) {
            return null;
        }
        return builder.build();
    }
}
