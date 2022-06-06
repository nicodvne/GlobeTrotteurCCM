package com.example.globetrotteur;



import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.Manifest;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;


public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    private final String url = "http://10.0.2.2:8080/TestAndroid/UploadServlet";
    private String lon = "";
    private String lat = "";
    public static final int PHOTO_ACTIVITY_RETURN_CODE = 42;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
    final static int PERMISSION_ALL = 1;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }
        scheduleJob();

        setContentView(R.layout.activity_main);

        checkIfLocationHasChanged();
    }


    private void scheduleJob(){
        @SuppressLint("RestrictedApi") SharedPreferences sharedPref = getActivity(this).getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        if(!sharedPref.getBoolean("firstRunComplete", false)){
            //schedule the job only once.
            scheduleJobTakePictureReminder();
            //update shared preference
            editor.putBoolean("firstRunComplete", true);
            editor.commit();
        }

    }

    private void scheduleJobTakePictureReminder(){

        JobScheduler jobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this,
                TakePictureReminderService.class);

        JobInfo jobInfo = new JobInfo.Builder(4242, componentName)
                .setPeriodic(15 * 60 * 1000)
                .setPersisted(true).build();

        jobScheduler.schedule(jobInfo);
    }

    @SuppressLint("MissingPermission")
    public void onClickChoosePicture(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);

        ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional).
                .maxResultSize(1080, 1080)
                .start(PHOTO_ACTIVITY_RETURN_CODE);

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PHOTO_ACTIVITY_RETURN_CODE  && resultCode  == RESULT_OK) {
                checkIfLocationHasChanged();
                File image = new File(((Uri)data.getData()).getPath());
                String path = image.getPath();
                if(lon != "" && lat != ""){
                    path = renameFile(image, lon +","+ lat + ".png");
                }
                Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, R.string.image_posted_success, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("MissingPermission")
    private void checkIfLocationHasChanged() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}