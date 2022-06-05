package com.example.globetrotteur;



import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private final String url = "http://10.0.2.2:8080/TestAndroid/UploadServlet";
    private String lon = "";
    private String lat = "";
    private Button button_choose_picture;
    public static final int PHOTO_ACTIVITY_RETURN_CODE = 42;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        //notifyTest();
        scheduleJob();
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            lon = ("Location not available");
            lat = ("Location not available");
        }
    }

    private void scheduleJob(){

        scheduleJobTakePictureReminder();

    }

    private void scheduleJobTakePictureReminder(){

        Log.i("TAG", "SCHEDULING !");
        JobScheduler jobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this,
                TakePictureReminderService.class);

        JobInfo jobInfo = new JobInfo.Builder(5844, componentName)
                .setPeriodic(5000)
                .setPersisted(true).build();

        jobScheduler.schedule(jobInfo);
    }

    public void notifyTest(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "le channel";
            String description = "le channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("42", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        Intent intent = new Intent(this, TakePictureFromNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "42")
                .setSmallIcon(R.drawable.ic_photo_black_48dp)
                .setContentTitle("Viens prendre un photo !")
                .setContentText("N'oublie pas de prendre une photo tous les jours ;) ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(58, builder.build());
    }

    public void onClickChoosePicture(View view) {
        ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional).
                .maxResultSize(1080, 1080)
                .start(PHOTO_ACTIVITY_RETURN_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PHOTO_ACTIVITY_RETURN_CODE  && resultCode  == RESULT_OK) {
                //Toast.makeText(MainActivity.this, data.getData().toString(), Toast.LENGTH_SHORT).show();
                File image = new File(((Uri)data.getData()).getPath());
                String newFile = renameFile(image, lon +","+ lat + ".png");
                Toast.makeText(MainActivity.this,lon + " : " + lat + " " + newFile, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

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