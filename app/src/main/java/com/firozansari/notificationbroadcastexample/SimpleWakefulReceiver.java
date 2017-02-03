package com.firozansari.notificationbroadcastexample;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Firoz Ansari on 03/02/2017.
 */

public class SimpleWakefulReceiver extends WakefulBroadcastReceiver {

    //BroadcastReceiver : It is not guaranteed that CPU will stay awake if you initiate some long running process. CPU may go immediately back to sleep.
    // whereas
    //WakefulBroadcastReceiver : It is guaranteed that CPU will stay awake until you fire completeWakefulIntent.

    @Override
    public void onReceive(Context context, Intent intent) {
        // This is the Intent to deliver to our service.
        Intent service = new Intent(context, SimpleWakefulService.class);

        // Start the service, keeping the device awake while it is launching.
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, service);
    }
}