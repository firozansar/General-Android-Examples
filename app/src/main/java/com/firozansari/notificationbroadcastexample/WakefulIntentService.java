package com.firozansari.notificationbroadcastexample;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Firoz Ansari on 03/02/2017.
 */

public class WakefulIntentService extends IntentService {
    static final String NAME = "com.firozansari.notificationbroadcastexample.WakefulIntentService";
    static final String LAST_ALARM = "lastAlarm";
    private static volatile PowerManager.WakeLock lockStatic = null;

    public WakefulIntentService(){
        super("WakefulIntentService");

    }

    protected void doWakefulWork(Intent intent){
        Log.d(getClass().getSimpleName(), "WakefulIntentService ran!");
    };

    private static synchronized PowerManager.WakeLock getLock(Context context) {
        if(lockStatic == null) {
            PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(1, "com.firozansari.notificationbroadcastexample.WakefulIntentService");
            lockStatic.setReferenceCounted(true);
        }

        return lockStatic;
    }

    public static void sendWakefulWork(Context ctxt, Intent i) {
        getLock(ctxt.getApplicationContext()).acquire();
        ctxt.startService(i);
    }

    public static void sendWakefulWork(Context ctxt, Class<?> clsService) {
        sendWakefulWork(ctxt, new Intent(ctxt, clsService));
    }

    public static void scheduleAlarms(WakefulIntentService.AlarmListener listener, Context ctxt) {
        scheduleAlarms(listener, ctxt, true);
    }

    public static void scheduleAlarms(WakefulIntentService.AlarmListener listener, Context ctxt, boolean force) {
        SharedPreferences prefs = ctxt.getSharedPreferences("com.firozansari.notificationbroadcastexample.WakefulIntentService", 0);
        long lastAlarm = prefs.getLong("lastAlarm", 0L);
        if(lastAlarm == 0L || force || System.currentTimeMillis() > lastAlarm && System.currentTimeMillis() - lastAlarm > listener.getMaxAge()) {
            AlarmManager mgr = (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(ctxt, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
            listener.scheduleAlarms(mgr, pi, ctxt);
        }

    }

    public static void cancelAlarms(Context ctxt) {
        AlarmManager mgr = (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
        mgr.cancel(pi);
        ctxt.getSharedPreferences("com.firozansari.notificationbroadcastexample.WakefulIntentService", 0).edit().remove("lastAlarm").commit();
    }

    public WakefulIntentService(String name) {
        super(name);
        this.setIntentRedelivery(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager.WakeLock lock = getLock(this.getApplicationContext());
        if(!lock.isHeld() || (flags & 1) != 0) {
            lock.acquire();
        }

        super.onStartCommand(intent, flags, startId);
        return Service.START_REDELIVER_INTENT;
    }

    protected final void onHandleIntent(Intent intent) {
        boolean var10 = false;

        try {
            var10 = true;
            this.doWakefulWork(intent);
            var10 = false;
        } finally {
            if(var10) {
                PowerManager.WakeLock lock1 = getLock(this.getApplicationContext());
                if(lock1.isHeld()) {
                    try {
                        lock1.release();
                    } catch (Exception var11) {
                        Log.e(this.getClass().getSimpleName(), "Exception when releasing wakelock", var11);
                    }
                }

            }
        }

        PowerManager.WakeLock lock = getLock(this.getApplicationContext());
        if(lock.isHeld()) {
            try {
                lock.release();
            } catch (Exception var12) {
                Log.e(this.getClass().getSimpleName(), "Exception when releasing wakelock", var12);
            }
        }

    }

    public interface AlarmListener {
        void scheduleAlarms(AlarmManager var1, PendingIntent var2, Context var3);

        void sendWakefulWork(Context var1);

        long getMaxAge();
    }
}
