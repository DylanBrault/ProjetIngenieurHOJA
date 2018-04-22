package com.example.timotemalherbe.compass;

import android.app.AlertDialog;
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
import java.util.List;

public class EnregistrementParcours extends AppCompatActivity {
    File myfile;
    private ListView mListView;
    double stepLength;
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

        ObstacleViewAdapter adapter = new ObstacleViewAdapter(EnregistrementParcours.this, obstacles);
        mListView.setAdapter(adapter);
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FILE_PATH",myfile);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void ajouterObstacle(View view) {
        //Mettre en pause l'activité reconnaissance
        //Afficher un popup de choix d'obstacle
        //Ajouter un item à la liste avec la distance par rapport à l'obstacle précédent et type d'obstacle
        //Puis resume l'activité reconnaissance avec deuxième popup de "reprise" de la reconnaissance
    }
}
