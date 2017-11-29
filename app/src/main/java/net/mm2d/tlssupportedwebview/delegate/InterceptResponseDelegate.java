/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.tlssupportedwebview.delegate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public interface InterceptResponseDelegate {
    void setUserAgent(@NonNull String userAgent);

    @Nullable
    WebResourceResponse shouldInterceptRequest(
            @NonNull WebView view,
            @NonNull String url);
}
