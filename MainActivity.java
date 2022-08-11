package com.example.gps_coord_3;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.gps_coord_3.databinding.ActivityMainBinding;
import com.example.gps_coord_3.databinding.MeniuBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.behavior.SwipeDismissBehavior;

import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;

import android.app.AlertDialog;

import android.content.DialogInterface;


import android.text.InputType;


import android.widget.Button;

import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_FINE_LOCATION = 99;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_dtime, tv_namegpx;
    Switch sw_locationsupdates, sw_gps;


    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    private String m_Text = "";

    Button stopBtn;

    // String sFileName2 = "file.gpx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        //tv_sensor = findViewById(R.id.tv_sensor);
        //tv_updates = findViewById(R.id.tv_updates);
        //tv_address = findViewById(R.id.tv_address);
        //sw_gps = findViewById(R.id.sw_gps);
        //sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        tv_dtime = findViewById(R.id.tv_dtime);
        tv_namegpx = findViewById(R.id.tv_namegpx);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        locationRequest = new com.google.android.gms.location.LocationRequest();

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
       // locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

       // String sFileName2;

       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

       // startLocationUpdates();
/*

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensor");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using GSM");
                }
            }
        });  */
/*
        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationsupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
        */
        updateGPS();

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Are you sure you want to stop GPS recording?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        stopLocationUpdates();
                        String sFooter = "</gpx>";
                        // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
                        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);
                        writeFile(Meniu.getFileName(), sFooter);
                        finish();
                       // startActivity(new Intent(MainActivity.this, Meniu.class));

                    }

                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    }

                });
                builder.show();

            }

        });

    }

    @Override
    protected  void onStart() {

        super.onStart();

        startLocationUpdates();

    }

    private void stopLocationUpdates() {
       // tv_updates.setText("-");
        tv_lat.setText(" ");
        tv_lon.setText("-");
        tv_speed.setText("-");
       // tv_address.setText("-");
        tv_accuracy.setText("-");
        tv_altitude.setText("-");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);


    }

    private void startLocationUpdates() {
       // tv_updates.setText("ok");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
      //  updateGPS();
     //   updateDT();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    updateUIValues(location);

                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

    }

    private void updateUIValues(Location location) {

        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.format("%.1f", location.getAccuracy()) + " m");

        if (location.hasAltitude()) {
            tv_altitude.setText(String.format("%.0f", location.getAltitude()) + " m");
        } else {
            tv_altitude.setText("Altitude is not available");
        }

        if (location.hasSpeed()) {
            tv_speed.setText(String.format("%.1f",(location.getSpeed() * 3.6)) + " km/h");
        } else {
            tv_speed.setText("Speed is not available");
        }

       // updateDT();

      //  Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        //SimpleDateFormat df2 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        // String formattedDate = df.format(c.getTime());
     //   String fileName = df2.format(c.getTime()) + ".txt";
      //  String sHeader;
        String sBody = "<wpt lat=\"" + String.valueOf(location.getLatitude()) + "\" lon=\"" + String.valueOf(location.getLongitude()) + "\"><ele>" + String.format("%.2f", location.getAltitude()) + "</ele><time>" + df.format(location.getTime()) + "</time></wpt>\n";
        // String sBody2 = sBody + "<ele " + String.valueOf(location.getAltitude()) + " - Speed " + String.valueOf(location.getSpeed()) + " >";
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);
       // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 25);
        writeFile(Meniu.getFileName(), sBody);
        tv_dtime.setText(df2.format(location.getTime()));

    }

    public void writeFile(String sFileName, String sBody) {
        try {
           // File root = new File(Environment.getExternalStorageDirectory(), "GPS");
            File root = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
           // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
          //  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);
           // File[] externalStorageVolumes =
           //         ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
          //  File root = externalStorageVolumes[1];

            File gpxfile = new File(root, sFileName);
            tv_namegpx.setText(gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    public void updateDT() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy_MM_dd");
        String formattedDate = df.format(c.getTime());
        String formattedDate2 = df2.format(c.getTime());
        //  tv_dtime.setText(formattedDate);
        //generateNoteOnSD(Context context, formattedDate2, String sBody)

    }
*/
    @Override
    public void onBackPressed() {
        //stopLocationUpdates();
        String sFooter = "</gpx>";
       // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);
        //writeFile(Meniu.getFileName(), sFooter);
        //finish();

    }




}

