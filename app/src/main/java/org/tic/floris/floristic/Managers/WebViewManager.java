package org.tic.floris.floristic.Managers;

import android.os.Build;
import android.view.View;
import android.webkit.WebView;

public class WebViewManager {

    public static void disableHardwareAcceleration(WebView view) {
        if (Build.VERSION.SDK_INT >= 11) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
}
