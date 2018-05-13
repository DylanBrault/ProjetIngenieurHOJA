package com.example.timotemalherbe.compass;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Course extends AppCompatActivity {
    public static final String EXTRA_POINTX = "com.example.timotemalherbe.POINTX";
    public static final String EXTRA_POINTY = "com.example.timotemalherbe.POINTY";
    public static final String EXTRA_TYPEOBSTACLES = "com.example.timotemalherbe.TYPEOBSTACLES";
    public static final String EXTRA_OBSTACLESX = "com.example.timotemalherbe.OBSTACLESX";
    public static final String EXTRA_OBSTACLESY = "com.example.timotemalherbe.OBSTACLESY";
    public static final String EXTRA_NUMEROSOBSTACLES = "com.example.timotemalherbe.NUMEROSOBSTACLES";
    public static final String EXTRA_DISTANCE="com.example.timotemalherbe.DISTANCE";

    int foulee;
    int distAppel;

    ArrayList mPointX;
    ArrayList mPointY;
    ArrayList mTypeObstacles;
    ArrayList mObstaclesX;
    ArrayList mObstaclesY;
    ArrayList mObstaclesCouleurs;
    ArrayList mNumerosObstacles;
    double stepLength;

    float vitesseNormale;
    float vitesseLente;
    float vitesseAcceleration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mPointX = intent.getIntegerArrayListExtra(MainActivity.EXTRA_POINTX);
        mPointY = intent.getIntegerArrayListExtra(MainActivity.EXTRA_POINTY);
        mTypeObstacles = intent.getIntegerArrayListExtra(MainActivity.EXTRA_TYPEOBSTACLES);
        mObstaclesX = intent.getIntegerArrayListExtra(MainActivity.EXTRA_OBSTACLESX);
        mObstaclesY = intent.getIntegerArrayListExtra(MainActivity.EXTRA_OBSTACLESY);
        int nombreCouleursObstacles = intent.getIntExtra(MainActivity.EXTRA_OBSTACLESTYPESNBR, 4);
        mNumerosObstacles = intent.getIntegerArrayListExtra(MainActivity.EXTRA_NUMEROSOBSTACLES);
        stepLength = intent.getDoubleExtra(MainActivity.EXTRA_DISTANCE, 0.0);
        foulee = 4;
        distAppel = 2;
        mObstaclesCouleurs = new ArrayList();
        for (int k = 0; k < nombreCouleursObstacles; k++) {
            Random rand = new Random();
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            int c = Color.rgb(r, g, b);
            mObstaclesCouleurs.add(c);
        }

        setContentView(R.layout.activity_course);
        paintMap();
    }

    public void paintMap(){
        ImageView imageView = (ImageView) findViewById(R.id.carte);
        Bitmap bitmap = Bitmap.createBitmap(304, 304, Bitmap.Config.ARGB_8888);
        int facteur = Math.max(((int) Collections.max(mPointX) - (int) Collections.min(mPointX))/304+1,((int) Collections.max(mPointY) - (int) Collections.min(mPointY))/304+1);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        for (int j = 0; j < mPointX.size(); j++) {
            if (j == 0) {
                paint.setColor(Color.RED);
            }
            if (j == mPointX.size() - 1) {
                paint.setColor(Color.BLUE);
            }
            int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX)==0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 3, paint);
                paint.setColor(Color.BLACK);
            }else {
                int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get(j)) * 304 / DeltaMax));
                canvas.drawCircle(x, y, 3, paint);
                paint.setColor(Color.BLACK);
            }
        }
        for (int i = 0; i < mObstaclesX.size(); i++) {
            int typeObstacle = (int) mTypeObstacles.get(i);
            paint.setColor((Integer) mObstaclesCouleurs.get(typeObstacle));
            int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX) == 0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 3, paint);
            }else {
                int x = (int) (((int) mObstaclesX.get(i) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mObstaclesY.get(i)) * 304 / DeltaMax));
                canvas.drawCircle(x, y,3, paint);
            }
        }
        for (int p = 1; p < mNumerosObstacles.size(); p++) {
            int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            int distObst= (int) Math.round(((int) mNumerosObstacles.get(p)-(int) mNumerosObstacles.get(p-1))*stepLength/100);
            if ((distObst-(distAppel))%foulee==1){ // On accélère
                paint.setColor(Color.GREEN);
                if ((int)mNumerosObstacles.get(p)-5>0) {
                    for (int k=1;k<=5;k++) {
                        int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p) - k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                        int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p) - k)) * 304 / DeltaMax));
                        canvas.drawCircle(x, y, 3, paint);
                    }
                }
            }else{
                if ((distObst-distAppel)%foulee==2){ // On ralentit sur 2 foulees
                    paint.setColor(Color.RED);
                    if ((int)mNumerosObstacles.get(p-1)+9<(int)mNumerosObstacles.get(p)){
                        for (int k=1;k<=9;k++) {
                            int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p-1) + k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                            int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p-1) + k)) * 304 / DeltaMax));
                            canvas.drawCircle(x, y, 3, paint);
                        }
                    }
                }else{
                    if ((distObst-distAppel)%foulee==3){ // On ralentit sur 1 foulee
                        paint.setColor(Color.RED);
                        if ((int)mNumerosObstacles.get(p-1)+5<(int)mNumerosObstacles.get(p)) {
                            for (int k=1;k<=5;k++) {
                                int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p-1) + k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p-1) + k)) * 304 / DeltaMax));
                                canvas.drawCircle(x, y, 3, paint);
                            }
                        }
                    }
                }

            }

        }
        imageView.setImageBitmap(bitmap);
    }
}
