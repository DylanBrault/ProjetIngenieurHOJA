package com.example.timotemalherbe.compass;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EnregistrementParcours extends AppCompatActivity implements SensorEventListener {
    //private float currentDegree = 0f;
    private SensorManager mSensorManager;
    File myfile;
    //TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement_parcours);
        //tvHeading = (TextView) findViewById(R.id.textView);

        final File path = Environment.getExternalStoragePublicDirectory("/HOP_app/");
        if(!path.exists())//Create directory if non existant
        {
            path.mkdirs();
        }

        int i=1;
        /*
        //Create a unique new file to store data
        boolean unique = false;
        String name;

        File dir = new File (String.valueOf(path));
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                name = f.getName();
                if (name != "New"+i) {
                    unique=true;
                    break;
                }
                i++;
            }
        }*/

        myfile = new File(path,"New"+i+".txt");

        try {
            myfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File creation failed: " + e.toString());
        }


        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //writeToFile();
    }

    public void writeToFile(String value){
        try {
            // catches IOException below
            final String TESTSTRING = new String(value);
            FileOutputStream fOut = openFileOutput(String.valueOf(myfile), MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            // Write the string to the file
            osw.write(TESTSTRING);
            osw.flush();
            osw.close();
        } catch (IOException ioe)
        {ioe.printStackTrace();}
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }


    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        final float degree = Math.round(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }


    public void commencer(View view) {

    }

    public void terminer(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FILE_PATH",myfile);
        startActivity(intent);
    }
}
