package com.atguigu.mobilesafe.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by miao on 2016/3/21.
 */
public class GpsUtils {

    private static LocationManager mLm;

    public static void getLocation(Context context, final String safeNum ) {
        mLm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!mLm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MsUtils.showMsg(context, "GPS没有开启");
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = mLm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.e("TAG",longitude+"--"+latitude);
            MsUtils.sendSmsMsg(safeNum,longitude+"--"+latitude);

        }

        mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                MsUtils.sendSmsMsg(safeNum,longitude+"--"+latitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }
}
