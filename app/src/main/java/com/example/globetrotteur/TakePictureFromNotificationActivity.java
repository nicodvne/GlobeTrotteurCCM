package com.example.globetrotteur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import java.io.File;


public class TakePictureFromNotificationActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private final String url = "http://10.0.2.2:8080/TestAndroid/UploadServlet";
    private String lon = "";
    private String lat = "";
    private Button button_choose_picture;
    private String provider;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
    final static int PERMISSION_ALL = 1;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional).
                .maxResultSize(1080, 1080)
                .start();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode  == RESULT_OK) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                File image = new File(((Uri)data.getData()).getPath());
                String path = image.getPath();
                if(lon != "" && lat != ""){
                    path = renameFile(image, lon +","+ lat + ".png");
                }
                Toast.makeText(TakePictureFromNotificationActivity.this, path, Toast.LENGTH_SHORT).show();
                Toast.makeText(TakePictureFromNotificationActivity.this, R.string.image_posted_success, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception ex) {
            Toast.makeText(TakePictureFromNotificationActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private String renameFile(File file, String name) {
        File dir = Environment.getExternalStorageDirectory();
        if(dir.exists()){
            File imageNewName = new File(file.getParent(), name);
            if(file.exists()){
                if(file.renameTo(imageNewName))
                    return imageNewName.getPath();
            }
        }
        return "NULL";
    }

    @Override
    public void onLocationChanged(Location location) {
        lon = String.valueOf(location.getLongitude());
        lat = String.valueOf(location.getLatitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(TakePictureFromNotificationActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
