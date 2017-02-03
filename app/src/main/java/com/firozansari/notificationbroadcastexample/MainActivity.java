package com.firozansari.notificationbroadcastexample;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private DownloadManager downloadManager;

    Button showNotificationBut, stopNotificationBut, alertButton;

    TextView textView;

    // Allows us to notify the user that something happened in the background
    NotificationManager notificationManager;

    // Used to track notifications
    private int notifID = 33;

    private long Image_DownloadId;

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
        textView = (TextView) findViewById(R.id.downloadedText);

        sendBroadcast(new Intent(this, SimpleWakefulReceiver.class));

    }


    @Override
    protected void onResume(){
        super.onResume();

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadImageReceiver, filter);


        // Allows use to track when an intent with the id TRANSACTION_DONE is executed
        // We can call for an intent to execute something and then tell use when it finishes
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileService.TRANSACTION_DONE);

        // Prepare the main thread to receive a broadcast and act on it
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, intentFilter);
    }

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadReceiver);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadImageReceiver);

        super.onPause();
    }


    public void showNotification(View view) {

        // Builds a notification
        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Notifications Title")
                .setContentText("Notification content")
                .setSubText("tap to see details")
                .setTicker("Alert New Message")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));


        // Define that we have the intention of opening MoreInfoNotifActivity
        Intent moreInfoIntent = new Intent(this, MoreInfoNotifActivity.class);

        // Used to stack tasks across activites so we go to the proper place when back is clicked
        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);

        // Add all parents of this activity to the stack
        tStackBuilder.addParentStack(MoreInfoNotifActivity.class);

        // Add our new Intent to the stack
        tStackBuilder.addNextIntent(moreInfoIntent);

        // Define an Intent and an action to perform with it by another application
        // FLAG_UPDATE_CURRENT : If the intent exists keep it but update it if needed
        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // or we could add other pending intent like
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/"));
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Defines the Intent to fire when the notification is clicked
        notificBuilder.setContentIntent(pendingIntent);

        // Gets a NotificationManager which is used to notify the user of the background event
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
        //
        //context Context: The Context in which this PendingIntent should perform the broadcast.
        //requestCode int: Private request code for the sender
        //intent Intent: The Intent to be broadcast.
        //flags int: May be FLAG_ONE_SHOT, FLAG_NO_CREATE, FLAG_CANCEL_CURRENT, FLAG_UPDATE_CURRENT, FLAG_IMMUTABLE
        //     or any of the flags as supported by Intent.fillIn() to control which unspecified parts of the intent
        //     that can be supplied when the actual send happens. Here "FLAG_UPDATE_CURRENT" which update the Intent if active
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent);

    }

    public void startFileService(View view) {

        //The IntentService is triggered using an Intent, it spawns a new worker thread and the method onHandleIntent() is called on this thread.
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
            textView.setText(sb.toString());


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

    public void startForegroundService(View view) {
        Button button = (Button) view;
        Intent service = new Intent(MainActivity.this, ForegroundService.class);
        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = true;
            button.setText("Stop Foreground Service");
        } else {
            service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = false;
            button.setText("Start Foreground Service");

        }
        startService(service);
    }

    public void showBatteryStatus(View view) {
        Intent i = new Intent();
        i.setClass(MainActivity.this, BatteryStatusActivity.class);
        startActivity(i);

    }

    public void downloadImage(View view) {

        Uri image_uri = Uri.parse("https://pixabay.com/static/uploads/photo/2015/08/07/00/41/lg-878843_960_720.jpg");
        Image_DownloadId = DownloadData(image_uri);
    }

    private long DownloadData (Uri uri) {

        long downloadReference;

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Image Download");

        //Setting description of request
        request.setDescription("Android image download using DownloadManager.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"downloadedimage.jpg");

        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    private BroadcastReceiver downloadImageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == Image_DownloadId) {

                Toast.makeText(getApplicationContext(), "Image download complete",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        // Called when the broadcast is received
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("FileService", "Service Received");

            showFileContents();

        }
    };
}
