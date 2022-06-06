package com.example.globetrotteur;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TakePictureReminderService.scheduleJob(context);
        Log.i("JobServiceSample", "Boot completed");
    }

}