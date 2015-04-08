package com.example.hackforimpact.tesapps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity implements SensorEventListener {

    File root = Environment.getExternalStorageDirectory();
    File save1;
    // Accelerometer X, Y, and Z values
    private TextView accelXValue;
    private TextView accelYValue;
    private TextView accelZValue;

    private String simpan;
    private int count;
    private float x;
    private float y;
    private float z;
    private SimpleDateFormat dateFormat;
    private Date date;
    private String waktu;

    private SensorManager sensorManager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);

        count = 0;
        x = 0;
        y = 0;
        z = 0;

        dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH:mm:ss");
        date = new Date();
        waktu = dateFormat.format(date);

        // Capture accelerometer related view elements
        accelXValue = (TextView) findViewById(R.id.accel_x_value);
        accelYValue = (TextView) findViewById(R.id.accel_y_value);
        accelZValue = (TextView) findViewById(R.id.accel_z_value);

        // Initialize accelerometer related view elements
        accelXValue.setText("0.00");
        accelYValue.setText("0.00");
        accelZValue.setText("0.00");

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater
                        .from(MainActivity.this);

                View promptView = layoutInflater.inflate(
                        R.layout.activity_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                // set prompts.xml to be the layout file of the
                // alertdialog builder
                alertDialogBuilder.setView(promptView);

                final EditText namaFile = (EditText) promptView
                        .findViewById(R.id.namaFile);
                Log.e("FILE", "MASUUUUK");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // get user input and set it to
                                        // result
                                        if (namaFile.getText().toString()
                                                .trim().equals("")) {
                                            namaFile.setError("Field is required!");
                                        } else {
                                            Log.e("FILE", "MASUK ELSE SAVE");
                                            try {

                                                File directory = new File(root
                                                        + "/abc/");
                                                Log.e("DIR", directory.toString());

												  if (!directory.exists()){
												        directory.mkdirs();
                                                  } else
												    Log.d("error", "dir. already exists");

                                                Log.e("FILE", namaFile
                                                        .getText().toString());
                                                save1 = new File(directory,
                                                        namaFile.getText()
                                                                .toString()
                                                                + ".csv");
                                                FileOutputStream fOut = new FileOutputStream(
                                                        save1);
                                                String header = "Waktu" + ","
                                                        + "Acc X" + ","
                                                        + "Acc Y" + ","
                                                        + "Acc Z" + "\n";
                                                fOut.write(header.getBytes());
                                                fOut.write(simpan.getBytes());
                                                fOut.flush();
                                                fOut.close();
                                                Log.e("FILE", "finish");
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "File Saved",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                simpan = "";
                                            } catch (FileNotFoundException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        simpan = "";
                                    }
                                });

                // create an alert dialog
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
        });
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                accelXValue.setText(Float.toString(sensorEvent.values[0]));
                accelYValue.setText(Float.toString(sensorEvent.values[1]));
                accelZValue.setText(Float.toString(sensorEvent.values[2]));

                x += sensorEvent.values[0];
                y += sensorEvent.values[1];
                z += sensorEvent.values[2];
                count++;
                if (count > 5) {
                    x /= 5;
                    y /= 5;
                    z /= 5;
					 simpan +=waktu + "," + Float.toString(x) +
					 "," + Float.toString(y) + "," + Float.toString(z)
					 + "\n";

                    count = 0;
                    x = 0;
                    y = 0;
                    z = 0;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        // ...and the orientation sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

}
