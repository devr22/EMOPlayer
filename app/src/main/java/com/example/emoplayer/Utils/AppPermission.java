package com.example.emoplayer.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class AppPermission {

    private static final String TAG = "AppPermission";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private Context context;

    public AppPermission(Context context) {
        this.context = context;
    }

    public void requestCameraPermission(){
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    public boolean checkCameraPermission(){

        int check = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
