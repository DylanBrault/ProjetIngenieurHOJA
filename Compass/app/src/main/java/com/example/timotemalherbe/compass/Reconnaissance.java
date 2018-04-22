package com.example.timotemalherbe.compass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Xfermode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Reconnaissance extends AppCompatActivity implements SensorEventListener {
    int distTot;
    int nombrePas;
    TextView nbrPas;
    TextView boussoleTV;
    TextView ax;
    TextView ay;
    TextView az;
    TextView gx;
    TextView gy;
    TextView gz;
    boolean pasDetecte;
    TextView distance;
    int x; //xActuel
    int y; //yActuel

    public static final String EXTRA_POINTX = "com.example.myfirstapp.POINTX";
    public static final String EXTRA_POINTY = "com.example.myfirstapp.POINTY";
    TextView heure;
    long time;
    private int dist;

    private SensorManager mSensorManager;

    private Sensor mStepCounterSensor;

    private Sensor mStepDetectorSensor;

    private Sensor mBoussoleSensor;

    private Sensor mAcSensor;

    private Sensor mGrSensor;

    private ArrayList mXArrayList;
    private ArrayList mYArrayList;
    private ArrayList mAngles;

    private EditText LongueurPas;

    public static final String CARTE = "com.example.malherbetimote.compass.CARTE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnaissance);
        nombrePas=0;
        time=0;
        x=0;
        y=0;
        distTot=0;
        dist=0;
        mAngles=new ArrayList();
        mXArrayList = new ArrayList();
        mXArrayList.add(x);
        mYArrayList = new ArrayList();
        mYArrayList.add(y);
        nbrPas = (TextView) findViewById(R.id.counter);
        boussoleTV = (TextView) findViewById(R.id.boussole);
        ax=findViewById(R.id.Ax);
        ay=findViewById(R.id.Ay);
        az=findViewById(R.id.Az);
        gx=findViewById(R.id.Gyrox);
        gy=findViewById(R.id.Gy);
        gz=findViewById(R.id.Gz);
        heure=findViewById(R.id.hTW);
        LongueurPas=findViewById(R.id.editText);
        distance=findViewById(R.id.Distance);
        pasDetecte=false;
        // Sensors
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mBoussoleSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAcSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGrSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Register Sensor Listener
        mSensorManager.registerListener(this,mBoussoleSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mStepCounterSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mAcSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mGrSensor,SensorManager.SENSOR_DELAY_FASTEST);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = new float[3];

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            nombrePas += 1;
            nbrPas.setText("Step Counter Detected : " + nombrePas);
            distTot+=dist;
            distance.setText("Distance totale parcourue : "+ distTot);
            pasDetecte=true;

        }
        if (sensor.getType() == Sensor.TYPE_ORIENTATION && pasDetecte){
            boussoleTV.setText("Boussole : "+Math.round(event.values[0]));
            float angle=event.values[0];
            mAngles.add(event.values[0]);
            x = (int) (x + Math.cos((event.values[0]- (float) mAngles.get(0))*2*Math.PI/360) * dist);
            mXArrayList.add(x);
            y = (int) (y + Math.sin((event.values[0]- (float) mAngles.get(0))*2*Math.PI/360) * dist);
            mYArrayList.add(y);
            pasDetecte=false;
        }
        if (sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax.setText("Ax : " +event.values[0]);
            ay.setText("Ay: " +event.values[1]);
            az.setText("Az: " +event.values[2]);
        }
        if (sensor.getType()==Sensor.TYPE_GYROSCOPE){
            gx.setText("Gx : " + event.values[0]);
            gy.setText("Gy : " + event.values[1]);
            gz.setText("Gz : " + event.values[2]);
        }
        time=System.currentTimeMillis();

        heure.setText(Long.toString(time));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
        mSensorManager.unregisterListener(this,mBoussoleSensor);
        mSensorManager.unregisterListener(this,mAcSensor);
    }

    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mBoussoleSensor,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this,mStepCounterSensor,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this,mAcSensor,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this,mGrSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
        mSensorManager.unregisterListener(this,mBoussoleSensor);
        mSensorManager.unregisterListener(this,mAcSensor);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, Carte.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        intent.putExtra(EXTRA_POINTX, mXArrayList);
        intent.putExtra(EXTRA_POINTY, mYArrayList);
        startActivity(intent);
    }

    public void calibrate(View view){
        dist=Integer.parseInt(LongueurPas.getText().toString());
    }
}