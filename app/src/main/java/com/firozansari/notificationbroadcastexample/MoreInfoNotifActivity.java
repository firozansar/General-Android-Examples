package com.firozansari.notificationbroadcastexample;

/**
 * Created by firoz on 01/12/2016.
 */
import android.app.Activity;
import android.os.Bundle;

// Called when the notification is clicked on in the task bar
public class MoreInfoNotifActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info_notific);
    }
}