package com.lovelycoding.whatapp.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RunTimePermission extends Application {

    public static final int REQUEST_CODE = 1234;
    public static boolean mPermission;

    public static boolean cameraPermission(Context context) {


            Log.d(TAG, "verifyPermissions: asking user for permissions.");
            String[] permissions = {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(context,
                    permissions[0] ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context.getApplicationContext(),
                    permissions[1] ) == PackageManager.PERMISSION_GRANTED) {
                mPermission = true;
                Log.d(TAG, "verifyPermissions: ");


            } else {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissions,
                        REQUEST_CODE
                );
            }
        return mPermission;
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
