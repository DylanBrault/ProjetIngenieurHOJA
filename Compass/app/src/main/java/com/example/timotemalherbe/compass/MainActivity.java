package com.example.timotemalherbe.compass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.IOException;
import android.os.Handler;
import android.os.Environment;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private float currentDegree;

    // device sensor manager
    //private SensorManager mSensorManager;
    //TextView tvHeading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentDegree=mSensorManager.getDefaultSensor()

        tvHeading = (TextView) findViewById(R.id.textView);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        writeToFile();
    }

    public void writeToFile(){
        try {

            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("hhhhhhhhhh \\n hhhhh");
            BufferedWriter b = new BufferedWriter(myOutWriter);
            b.newLine();
            myOutWriter.append("hhhhhhhhhh \\n hhhhh");
            b.close();
            myOutWriter.close();

            fOut.flush();
            fOut.close();

            Log.d("Exception", "-------------------File ok: " + path  );
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());

        }




        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
               /* try {

                    //file.createNewFile();
                    //FileOutputStream fOut = new FileOutputStream(file);
                    //OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    //myOutWriter.append(tvHeading.getText());

                    //myOutWriter.close();

                    //fOut.flush();
                    //fOut.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());

                }*/

                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getOrientation(), SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }


    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        final float degree = Math.round(event.values[0]);

        tvHeading.setText(Float.toString(degree));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }*/

    public void EnregistrerParcours(View view) {
        Intent EnregistrerParcours = new Intent(this, EnregistrementParcours.class);
        startActivity(EnregistrerParcours);
    }
}
