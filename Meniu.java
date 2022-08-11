package com.example.gps_coord_3;

import static android.os.Environment.*;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Meniu extends AppCompatActivity {

    TextView tv_dtime;
    static String sFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meniu);

       // String m_Text = "";

        Button btnview;
       // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 25);


        btnview = (Button) findViewById(R.id.btnview);
       // tv_dtime = findViewById(R.id.tv_dtime);

        btnview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Meniu.this);
                builder.setTitle("Enter the name of the gpx file:");
// Set up the input
                final EditText input = new EditText(Meniu.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String m_Text = "";

                        m_Text = input.getText().toString();
                        sFileName = m_Text + ".gpx";
                        String sHeader = "<?xml version=\"1.0\"?> \n <gpx version=\"1.1\" creator=\"gpxgenerator.com\">\n";
                        writeFile(sFileName, sHeader);
                    //    tv_dtime.setText("File created");
                        startActivity(new Intent(Meniu.this, MainActivity.class));

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

    public void writeFile(String sFileName, String sBody) {
        try {
            File root = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);

          //  File[] externalStorageVolumes =
           //        ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
          //  File root = externalStorageVolumes[1];

            File gpxfile = new File(root, sFileName);
            //tv_dtime.setText(gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getFileName() {

        return sFileName;
    }
}
