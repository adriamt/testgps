package com.amt.testgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    public MyAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        Intent i = new Intent(context, MyLogService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
