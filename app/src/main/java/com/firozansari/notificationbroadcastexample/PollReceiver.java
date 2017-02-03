package com.firozansari.notificationbroadcastexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Firoz Ansari on 03/02/2017.
 */

public class PollReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        Intent intent = new Intent(context, WakefulIntentService.class);
        context.startService(intent);
    }
}
