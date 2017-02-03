package com.firozansari.notificationbroadcastexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Firoz Ansari on 02/02/2017.
 */

public class BatteryStatusActivity extends AppCompatActivity {

    private ProgressBar bar=null;
    private ImageView status=null;
    private TextView level=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_layout);

        bar=(ProgressBar)findViewById(R.id.bar);
        status=(ImageView)findViewById(R.id.status);
        level=(TextView)findViewById(R.id.level);

    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter =new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(onBattery, intentFilter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(onBattery);
        super.onPause();
    }

    BroadcastReceiver onBattery=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            //Following constants defined on the BatteryManager class:
            //EXTRA_HEALTH, which should generally be BATTERY_HEALTH_GOOD
            //EXTRA_LEVEL, which is the proportion of battery life remaining as an integer, specified on the scale described by the EXTRA_SCALE value
            //EXTRA_PLUGGED, which will indicate if the device is plugged into AC power (BATTERY_PLUGGED_AC) or USB power (BATTERY_PLUGGED_USB)
            //EXTRA_SCALE, which indicates the maximum possible value of level (e.g., 100, indicating that level is a percentage of charge remaining)
            //EXTRA_STATUS, which will tell you if the battery is charging (BATTERY_STATUS_CHARGING), full (BATTERY_STATUS_FULL), or discharging (BATTERY_STATUS_DISCHARGING)
            //EXTRA_TECHNOLOGY, which indicates what sort of battery is installed (e.g., "Li-Ion")
            //EXTRA_TEMPERATURE, which tells you how warm the battery is, in tenths of a degree Celsius (e.g., 213 is 21.3 degrees Celsius)
            //EXTRA_VOLTAGE, indicating the current voltage being delivered by the battery, in millivolts

            //To get current battery information on Android 5.0 and higher, BatteryManager offers getIntProperty() and getLongProperty(), where the keys for the “properties”
            // are BATTERY_PROPERTY_* constants defined on BatteryManager, such as BATTERY_PROPERTY_CAPACITY to determine the percentage of remaining battery capacity.

            int pct= 100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 1)
                            / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);

            bar.setProgress(pct);
            level.setText(String.valueOf(pct));

            switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    status.setImageResource(R.drawable.charging);
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    int plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                    if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                        status.setImageResource(R.drawable.full);
                    } else {
                        status.setImageResource(R.drawable.unplugged);
                    }
                    break;

                default:
                    status.setImageResource(R.drawable.unplugged);
                    break;
            }
        }
    };

}
