package org.tic.floris.floristic.Managers;

import android.view.View;
import android.view.ViewGroup;

public class ActivityManager {

    public static void unbindDrawables(View view) {
        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
                if (view instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                        ActivityManager.unbindDrawables(((ViewGroup) view).getChildAt(i));
                    }
                    ((ViewGroup) view).removeAllViews();
                }
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
