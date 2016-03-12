package com.amt.testgps;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.amt.testgps.httpTask.HttpHandler;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button btnSendLocation;
    Button btnLogin;
    Button btnCreateSession;

    String user_id = "";
    String session_id = "";

    // GPSTracker class
    GPSTracker gps;

    double latitude = 0;
    double longitude = 0;
    double altitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateSession = (Button) findViewById(R.id.btnCreate);
        btnSendLocation = (Button) findViewById(R.id.btnShowLocation);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpHandler() {
                    @Override
                    public void onResponse(String result) {
                        user_id = result;
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("msg", user_id);
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
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("msg", session_id);
                        back_dialog.setArguments(args);
                        back_dialog.show(getFragmentManager(), "Info msg");

                    }
                }.create_session(user_id);

            }
        });


        // show location button click event
        btnSendLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    altitude = gps.getAltitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude +  "\nAltitude: " + altitude , Toast.LENGTH_SHORT).show();

                    new HttpHandler() {
                        @Override
                        public void onResponse(String result) {
                            user_id = result;
                            DialogFragment back_dialog = new GeneralDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("msg", user_id);
                            back_dialog.setArguments(args);
                            back_dialog.show(getFragmentManager(), "Info msg");

                        }
                    }.send_location(String.valueOf(latitude), String.valueOf(longitude),session_id);


                }else{

                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }

}
