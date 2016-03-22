package com.amt.testgps;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;
import com.amt.testgps.httpTask.HttpHandler;

public class MyLogService extends IntentService {

    public static final String PREFS_NAME = "GPS_PREFS";

    // GPSTracker class
    GPSTracker gps;

    String user_id = "";
    String session_id = "";

    double latitude = 0;
    double longitude = 0;
    double altitude = 0;
    float battery = 0;

    public MyLogService() {
        super("MyLogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("Log Service", "Reading data ...");

        SharedPreferences settings;
        settings = getBaseContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        session_id = settings.getString("session_id", "");
        user_id = settings.getString("user_id","");

        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            altitude = gps.getAltitude();
            battery = getBatteryLevel();

            // \n is for new line
           // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude + "\nAltitude: " + altitude
            //        + "\nBat: " + battery, Toast.LENGTH_SHORT).show();
            System.out.println("Lat: " + String.valueOf(latitude) + " Lon " + String.valueOf(longitude) + " Bat " + String.valueOf(battery));
            new HttpHandler() {
                @Override
                public void onResponse(String result) {

                }
            }.send_location(String.valueOf(latitude), String.valueOf(longitude),session_id,String.valueOf(battery));


        }else{

            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


    }

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }


}
