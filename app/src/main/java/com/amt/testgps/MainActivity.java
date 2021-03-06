package com.amt.testgps;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.amt.testgps.httpTask.HttpHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    Button btnLogin;
    Button btnCreateSession;
    Button btnStartService;
    Button btnStopService;

    String user_id = "";
    String session_id = "";

    // GPSTracker class
    GPSTracker gps;

    public static final String PREFS_NAME = "GPS_PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateSession = (Button) findViewById(R.id.btnCreate);
        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStopService = (Button) findViewById(R.id.btnStopService);

        gps = new GPSTracker(this);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpHandler() {
                    @Override
                    public void onResponse(String result) {
                        user_id = result;
                        String temp = "";
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String formattedDate = df.format(c.getTime());
                        if (!result.equals("")){
                            temp = "Login OK!";
                            writeToFile("["+formattedDate+"] "+"Login OK. Session ID: " + session_id + "\\r\\n");
                        }else{
                            temp = "Login Error!";
                            writeToFile("["+formattedDate+"] "+"Login Error." );
                        }
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("msg", temp);
                        back_dialog.setArguments(args);
                        back_dialog.show(getFragmentManager(), "Info msg");

                    }
                }.open_session();

            }
        });

        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new HttpHandler() {
                        @Override
                        public void onResponse(String result) {
                            session_id = result;
                            String temp = "";
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String formattedDate = df.format(c.getTime());
                            if (!result.equals("")){
                                temp = "OpenSession OK!";
                                writeToFile("["+formattedDate+"] "+"OpenSession OK. Session ID: " + session_id);
                            }else{
                                temp = "OpenSession Error!";
                                writeToFile("["+formattedDate+"] "+"OpenSession Error.");
                            }
                            DialogFragment back_dialog = new GeneralDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("msg", temp);
                            back_dialog.setArguments(args);
                            back_dialog.show(getFragmentManager(), "Info msg");

                        }
                    }.create_session(user_id);
            }
        });

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps.getLocation();
                if(gps.canGetLocation()) {
                    if(session_id.equals("")){
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        String msg = "Es necessari fer Login primer";
                        args.putString("msg", msg);
                        back_dialog.setArguments(args);
                        back_dialog.show(getFragmentManager(), "Login miss");
                    }else if(user_id.equals("")){
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        String msg = "Es necessari fer CreateSession primer";
                        args.putString("msg", msg);
                        back_dialog.setArguments(args);
                        back_dialog.show(getFragmentManager(), "Session miss");
                    }else{
                        SharedPreferences settings;
                        SharedPreferences.Editor editor;
                        settings = getBaseContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        editor = settings.edit();
                        editor.putString("session_id", session_id);
                        editor.putString("user_id", user_id);
                        editor.apply();
                        Toast.makeText(getBaseContext(),"Service Started!",Toast.LENGTH_SHORT).show();
                        scheduleAlarm();
                    }
                }else{

                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();

                }
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Service Stoped!",Toast.LENGTH_SHORT).show();
                cancelAlarm();
            }
        });
    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1 * 60 * 1000, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private void writeToFile(String data) {
        File storage = new File(Environment.getExternalStorageDirectory(), "TESTGPS");
        if (! storage.exists()){
            if (! storage.mkdirs()){
                Log.d("TESTGPS", "failed to create directory");
            }
        }

        try {
            FileWriter fileW = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/TESTGPS/Log.txt",true);
            fileW.append(data);
            fileW.append("\r\n");
            fileW.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
