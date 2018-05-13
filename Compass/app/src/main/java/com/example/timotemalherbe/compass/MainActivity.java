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
    ArrayList mNumerosObstacles;
    double stepLength;
    int nombreCouleursObstacles;

    public static final String EXTRA_POINTX = "com.example.timotemalherbe.POINTX";
    public static final String EXTRA_POINTY = "com.example.timotemalherbe.POINTY";
    public static final String EXTRA_TYPEOBSTACLES = "com.example.timotemalherbe.TYPEOBSTACLES";
    public static final String EXTRA_OBSTACLESX = "com.example.timotemalherbe.OBSTACLESX";
    public static final String EXTRA_OBSTACLESY = "com.example.timotemalherbe.OBSTACLESY";
    public static final String EXTRA_NUMEROSOBSTACLES = "com.example.timotemalherbe.NUMEROSOBSTACLES";
    public static final String EXTRA_DISTANCE="com.example.timotemalherbe.DISTANCE";
    public static final String EXTRA_OBSTACLESTYPESNBR="com.example.timotemalherbe.OBSTACLESTYPESNBR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mPointX = intent.getIntegerArrayListExtra(Carte.EXTRA_POINTX);
        mPointY = intent.getIntegerArrayListExtra(Carte.EXTRA_POINTY);
        mTypeObstacles = intent.getIntegerArrayListExtra(Carte.EXTRA_TYPEOBSTACLES);
        mObstaclesX = intent.getIntegerArrayListExtra(Carte.EXTRA_OBSTACLESX);
        mObstaclesY = intent.getIntegerArrayListExtra(Carte.EXTRA_OBSTACLESY);
        nombreCouleursObstacles=intent.getIntExtra(Carte.EXTRA_OBSTACLESTYPESNBR,4);
        mNumerosObstacles = intent.getIntegerArrayListExtra(Carte.EXTRA_NUMEROSOBSTACLES);
        stepLength=intent.getDoubleExtra(Carte.EXTRA_DISTANCE,0.0);
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

    public void CommencerCourse(View view) {
        if (mPointX !=null) {
            Intent intent = new Intent(this, Course.class);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            // Set your required file type
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(EXTRA_POINTX, mPointX);
            intent.putExtra(EXTRA_POINTY, mPointY);
            intent.putExtra(EXTRA_TYPEOBSTACLES, mTypeObstacles);
            intent.putExtra(EXTRA_OBSTACLESX,mObstaclesX);
            intent.putExtra(EXTRA_OBSTACLESY,mObstaclesY);
            intent.putExtra(EXTRA_NUMEROSOBSTACLES,mNumerosObstacles);
            intent.putExtra(EXTRA_DISTANCE,stepLength);
            intent.putExtra(EXTRA_OBSTACLESTYPESNBR,nombreCouleursObstacles);
            startActivityForResult(Intent.createChooser(intent, "Course"), 1001);
        }
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
