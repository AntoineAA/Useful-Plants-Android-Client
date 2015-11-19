package org.tic.floris.floristic.Managers;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import org.tic.floris.floristic.R;

import java.util.List;

public class GpsManager {

    /**
     * checks if the device has GPS
     *
     * @param context the current context
     * @return true if the device has GPS, false otherwise
     */
    public static boolean hasGPSDevice(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                List<String> providers = locationManager.getAllProviders();
                if (providers != null) {
                    return providers.contains(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(context.getString(R.string.exception), e.toString());
        }
        return false;
    }

    /**
     * checks if GPS is enable
     *
     * @param context the current context
     * @return true if the GPS is enabled, false otherwise
     */
    public static boolean isEnableGPS(Context context) {
        if (GpsManager.hasGPSDevice(context)) {
            try {
                LocationManager locationManager = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);
                if (locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(context.getString(R.string.exception), e.toString());
            }
        }
        return false;
    }

    /**
     * starts the implicit GPS activity
     */
    public static void startGPSIntent(Context context) {
        Intent GPSSettingsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(GPSSettingsIntent);
    }
}
