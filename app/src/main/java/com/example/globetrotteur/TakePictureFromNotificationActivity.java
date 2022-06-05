package com.example.globetrotteur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.listener.DismissListener;

import java.io.File;

public class TakePictureFromNotificationActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private final String url = "http://10.0.2.2:8080/TestAndroid/UploadServlet";
    private String lon = "";
    private String lat = "";
    private Button button_choose_picture;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
        //scheduleJob();
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            lon = ("Location not available");
            lat = ("Location not available");
        }
        Log.i("TAG","EE");
        ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional).
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode  == RESULT_OK) {
            //Toast.makeText(MainActivity.this, data.getData().toString(), Toast.LENGTH_SHORT).show();
            File image = new File(((Uri)data.getData()).getPath());
            String newFile = renameFile(image, lon +","+ lat + ".png");
            Toast.makeText(TakePictureFromNotificationActivity.this,lon + " : " + lat + " " + newFile, Toast.LENGTH_SHORT).show();
            finish();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf((Double) (location.getLatitude()));
        lon = String.valueOf((Double) (location.getLongitude()));
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}
