/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.tlssupportedwebview.delegate;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class InterceptResponseDelegateFactory {
    @NonNull
    public static InterceptResponseDelegate create() {
        return create(null);
    }

    @NonNull
    public static InterceptResponseDelegate create(final String userAgent) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN && VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            return InterceptResponseDelegatePreLollipop.newInstance(userAgent);
        }
        return new InterceptResponseDelegateNull();
    }
}
