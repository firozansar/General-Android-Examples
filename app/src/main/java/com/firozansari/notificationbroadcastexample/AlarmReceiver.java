package com.firozansari.notificationbroadcastexample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Firoz Ansari on 03/02/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String WAKEFUL_META_DATA = "com.firozansari.notificationbroadcastexample";

    public AlarmReceiver() {
    }

    public void onReceive(Context ctxt, Intent intent) {
        WakefulIntentService.AlarmListener listener = this.getListener(ctxt);
        if(listener != null) {
            if(intent.getAction() == null) {
                SharedPreferences prefs = ctxt.getSharedPreferences("com.firozansari.notificationbroadcastexample.WakefulIntentService", 0);
                prefs.edit().putLong("lastAlarm", System.currentTimeMillis()).commit();
                listener.sendWakefulWork(ctxt);
            } else {
                WakefulIntentService.scheduleAlarms(listener, ctxt, true);
            }
        }

    }

    private WakefulIntentService.AlarmListener getListener(Context ctxt) {
        PackageManager pm = ctxt.getPackageManager();
        ComponentName cn = new ComponentName(ctxt, this.getClass());

        try {
            ActivityInfo e = pm.getReceiverInfo(cn, 0);
            XmlResourceParser xpp = e.loadXmlMetaData(pm, "com.firozansari.notificationbroadcastexample");

            while(xpp.getEventType() != 1) {
                if(xpp.getEventType() == 2 && xpp.getName().equals("WakefulIntentService")) {
                    String clsName = xpp.getAttributeValue((String)null, "listener");
                    Class cls = Class.forName(clsName);
                    return (WakefulIntentService.AlarmListener)cls.newInstance();
                }

                xpp.next();
            }

            return null;
        } catch (PackageManager.NameNotFoundException var8) {
            throw new RuntimeException("Cannot find own info???", var8);
        } catch (XmlPullParserException var9) {
            throw new RuntimeException("Malformed metadata resource XML", var9);
        } catch (IOException var10) {
            throw new RuntimeException("Could not read resource XML", var10);
        } catch (ClassNotFoundException var11) {
            throw new RuntimeException("Listener class not found", var11);
        } catch (IllegalAccessException var12) {
            throw new RuntimeException("Listener is not public or lacks public constructor", var12);
        } catch (InstantiationException var13) {
            throw new RuntimeException("Could not create instance of listener", var13);
        }
    }
}
