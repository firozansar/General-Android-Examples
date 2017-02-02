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

        IntentFilter f=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(onBattery, f);
    }

    @Override
    public void onPause() {
        unregisterReceiver(onBattery);

        super.onPause();
    }

    BroadcastReceiver onBattery=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int pct=
                    100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 1)
                            / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);

            bar.setProgress(pct);
            level.setText(String.valueOf(pct));

            switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    status.setImageResource(R.drawable.charging);
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    int plugged=
                            intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                    if (plugged == BatteryManager.BATTERY_PLUGGED_AC
                            || plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                        status.setImageResource(R.drawable.full);
                    }
                    else {
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
