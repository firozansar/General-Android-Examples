package com.firozansari.notificationbroadcastexample;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;

/**
 * Created by firoz on 01/12/2016.
 */

// IntentService : Uses an intent to start a background service so as not to disturb the UI
// Android Broadcast : Triggers an event that a BroadcastReceiver can act on
// BroadcastReceiver : Acts when a specific broadcast is made
// NotificationManager : Allows us to notify the user that something happened in the background
// AlarmManager : Allows you to schedule for your application to do something at a later date
// even if it is in the background


public class MainActivity extends AppCompatActivity {

    Button showNotificationBut, stopNotificationBut, alertButton;

    // Allows us to notify the user that something happened in the background
    NotificationManager notificationManager;

    // Used to track notifications
    int notifID = 33;

    EditText downloadedEditText;

    private BroadcastReceiver downloadReceiver;

    // Used to track if notification is active in the task bar
    boolean isNotificActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        showNotificationBut = (Button) findViewById(R.id.showNotificationBut);
        stopNotificationBut = (Button) findViewById(R.id.stopNotificationBut);
        alertButton = (Button) findViewById(R.id.alertButton);

        downloadedEditText = (EditText) findViewById(R.id.downloadedEditText);


    }


    @Override
    protected void onResume(){
        super.onResume();
        // Is alerted when the IntentService broadcasts TRANSACTION_DONE
        downloadReceiver = new BroadcastReceiver() {

            // Called when the broadcast is received
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.e("FileService", "Service Received");

                showFileContents();

            }
        };

        // Allows use to track when an intent with the id TRANSACTION_DONE is executed
        // We can call for an intent to execute something and then tell use when it finishes
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileService.TRANSACTION_DONE);

        // Prepare the main thread to receive a broadcast and act on it
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(downloadReceiver);

        super.onPause();
    }


    public void showNotification(View view) {

        // Builds a notification
        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Message")
                .setContentText("New Message")
                .setTicker("Alert New Message")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp);

        // Define that we have the intention of opening MoreInfoNotification
        Intent moreInfoIntent = new Intent(this, MoreInfoNotification.class);

        // Used to stack tasks across activites so we go to the proper place when back is clicked
        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);

        // Add all parents of this activity to the stack
        tStackBuilder.addParentStack(MoreInfoNotification.class);

        // Add our new Intent to the stack
        tStackBuilder.addNextIntent(moreInfoIntent);

        // Define an Intent and an action to perform with it by another application
        // FLAG_UPDATE_CURRENT : If the intent exists keep it but update it if needed
        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Defines the Intent to fire when the notification is clicked
        notificBuilder.setContentIntent(pendingIntent);

        // Gets a NotificationManager which is used to notify the user of the background event
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Post the notification
        notificationManager.notify(notifID, notificBuilder.build());

        // Used so that we can't stop a notification that has already been stopped
        isNotificActive = true;


    }

    public void stopNotification(View view) {

        // If the notification is still active close it
        if(isNotificActive) {
            notificationManager.cancel(notifID);
        }

    }

    public void setAlarm(View view) {

        //AlarmManager.ELAPSED_REALTIME_WAKEUP type is used to trigger the alarm since boot time:
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 600000, pendingIntent);

        //AlarmManager.RTC_WAKEUP will trigger the alarm according to the time of the clock. For example if you do:
        //long thirtySecondsFromNow = System.currentTimeMillis() + 30 * 1000;
        //alarmManager.set(AlarmManager.RTC_WAKEUP, thirtySecondsFromNow , pendingIntent);

        // Define a time value of 5 seconds
        Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;

        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(this, AlertReceiver.class);

        // Allows you to schedule for your application to do something at a later date
        // even if it is in he background or isn't active
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // set() schedules an alarm to trigger
        // Trigger for alertIntent to fire in 5 seconds
        // FLAG_UPDATE_CURRENT : Update the Intent if active
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this, 1, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));

    }

    public void startFileService(View view) {

        // Create an intent to run the IntentService in the background
        Intent intent = new Intent(this, FileService.class);

        // Pass the URL that the IntentService will download from
        intent.putExtra("url", "https://www.newthinktank.com/wordpress/lotr.txt");

        // Start the intent service
        this.startService(intent);

    }



    // Will read our local file and put the text in the EditText
    public void showFileContents() {

        // Will build the String from the local file
        StringBuilder sb;

        try {
            // Opens a stream so we can read from our local file
            FileInputStream fis = this.openFileInput("myFile");

            // Gets an input stream for reading data
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

            // Used to read the data in small bytes to minimize system load
            BufferedReader bufferedReader = new BufferedReader(isr);

            // Read the data in bytes until nothing is left to read
            sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            // Put downloaded text into the EditText
            downloadedEditText.setText(sb.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
