package com.example.timotemalherbe.compass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class EnregistrementParcours extends AppCompatActivity implements SensorEventListener {
    File myfile;
    private ListView mListView;

    // Champs d'affichage
    TextView nbrPas;
    TextView boussoleTV;
    TextView distance;

    // Champs de mesures
    double stepLength;
    float distTot;
    int nombrePas;
    boolean pasDetecte;
    long time;

    // Champs de capteurs
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private Sensor mBoussoleSensor;

    // Champs pour tracer la carte
    private ArrayList mXArrayList;
    private ArrayList mYArrayList;
    private ArrayList mAngles;
    int x; //xActuel
    int y; //yActuel
    public static final String EXTRA_POINTX = "com.example.timotemalherbe.POINTX";
    public static final String EXTRA_POINTY = "com.example.timotemalherbe.POINTY";
    //TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement_parcours);
        //tvHeading = (TextView) findViewById(R.id.textView);

        //Initialisation de la longueur d'un pas par popup
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EnregistrementParcours.this);
        alertDialog.setTitle("Longueur de pas");
        alertDialog.setMessage("Entrez la longueur moyenne de pas");

        final EditText input = new EditText(EnregistrementParcours.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        stepLength = Double.parseDouble(input.getText().toString());
                    }
                });
        alertDialog.show();

        //Initialisation des mesures
        nombrePas=0;
        time=0;
        x=0;
        y=0;
        distTot=0;
        stepLength=0;
        pasDetecte=false;
        mAngles=new ArrayList();
        mXArrayList = new ArrayList();
        mXArrayList.add(x);
        mYArrayList = new ArrayList();
        mYArrayList.add(y);

        //Initialisation des champs de texte
        nbrPas = (TextView) findViewById(R.id.counter);
        boussoleTV = (TextView) findViewById(R.id.boussole);
        distance=findViewById(R.id.Distance);

        // Capteurs
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mBoussoleSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mSensorManager.registerListener( this,mBoussoleSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener( this,mStepCounterSensor,SensorManager.SENSOR_DELAY_FASTEST);


        //Initialisation de l'écriture dans un fichier
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


        //Démarrer l'activité reconnaissance et envoyer les données dans writeToFile(String data)
        //writeToFile(data);


        //Initialization of the ViewHolder
        mListView = findViewById(R.id.listView);

        List<Obstacle> obstacles = null;

        if (obstacles!=null){
            ObstacleViewAdapter adapter = new ObstacleViewAdapter(EnregistrementParcours.this, obstacles);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = new float[3];

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            nombrePas += 1;
            nbrPas.setText("Nombre de pas : " + nombrePas);
            distTot+=stepLength;
            distance.setText("Distance totale parcourue : "+ distTot);
            pasDetecte=true;

        }
        if (sensor.getType() == Sensor.TYPE_ORIENTATION){
            boussoleTV.setText("Angle : "+Math.round(event.values[0]));
        }
        if (sensor.getType() == Sensor.TYPE_ORIENTATION && pasDetecte) {
            boussoleTV.setText("Angle : " + Math.round(event.values[0]));
            float angle = event.values[0];
            mAngles.add(event.values[0]);
            // x = (int) (x + Math.cos((event.values[0]- (float) mAngles.get(0))*2*Math.PI/360) * dist);
            x = (int) (x - Math.cos((event.values[0]) * 2 * Math.PI / 360) * stepLength);
            mXArrayList.add(x);
            // y = (int) (y + Math.sin((event.values[0]- (float) mAngles.get(0))*2*Math.PI/360) * dist);
            y = (int) (y - Math.sin((event.values[0]) * 2 * Math.PI / 360) * stepLength);
            mYArrayList.add(y);
            pasDetecte = false;
        }
        time=System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
        mSensorManager.unregisterListener(this,mBoussoleSensor);
    }

    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mBoussoleSensor,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this,mStepCounterSensor,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
        mSensorManager.unregisterListener(this,mBoussoleSensor);
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

    public void commencer(View view) {
        //Commencer l'activité reconnaissance en background

    }

    public void terminer(View view) {
        Intent intent = new Intent(this, Carte.class);
        intent.putExtra(EXTRA_POINTX, mXArrayList);
        intent.putExtra(EXTRA_POINTY, mYArrayList);
        startActivity(intent);
    }

    public void ajouterObstacle(View view) {
        //Mettre en pause l'activité reconnaissance
        //Afficher un popup de choix d'obstacle
        //Ajouter un item à la liste avec la distance par rapport à l'obstacle précédent et type d'obstacle
        //Puis resume l'activité reconnaissance avec deuxième popup de "reprise" de la reconnaissance
    }
}
