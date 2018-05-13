package com.example.timotemalherbe.compass;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList mPointX;
    ArrayList mPointY;
    ArrayList mTypeObstacles;
    ArrayList mObstaclesX;
    ArrayList mObstaclesY;
    ArrayList mObstaclesCouleurs;
    ArrayList mNumerosObstacles;
    double stepLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mPointX = intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_POINTX);
        mPointY = intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_POINTY);
        mTypeObstacles = intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_TYPEOBSTACLES);
        mObstaclesX = intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_OBSTACLESX);
        mObstaclesY = intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_OBSTACLESY);
        int nombreCouleursObstacles=intent.getIntExtra(EnregistrementParcours.EXTRA_OBSTACLESTYPESNBR,4);
        mNumerosObstacles = intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_NUMEROSOBSTACLES);
        stepLength=intent.getDoubleExtra(EnregistrementParcours.EXTRA_DISTANCE,0.0);
        setContentView(R.layout.activity_main);
    }

    public void EnregistrerParcours(View view) {
        Intent intent = new Intent(this, EnregistrementParcours.class);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Set your required file type
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Enregistrement"),1001);
    }


    public void Credit(View view){
        Intent intent = new Intent(MainActivity.this, credit.class);
        startActivity(intent);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            Uri currFileURI = data.getData();
            String path=currFileURI.getPath();
        }
    }
}
