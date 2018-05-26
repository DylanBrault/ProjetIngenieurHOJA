package com.example.timotemalherbe.compass;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Course extends AppCompatActivity {

    int foulee;
    int distAppel;
    double positionActuelle;
    ArrayList mRalentissementDist;
    ArrayList mAccelerationDist;
    int posAcc;
    int posRal;
    boolean speedChange;
    boolean enCourse;

    ArrayList mPointX;
    ArrayList mPointY;
    ArrayList mTypeObstacles;
    ArrayList mObstaclesX;
    ArrayList mObstaclesY;
    ArrayList mObstaclesCouleurs;
    ArrayList mNumerosObstacles;
    double stepLength;
    long time;
    long oldTime;
    int lastObstacle;
    int posX;
    int posY;
    boolean sonnerie;

    ImageView iv;
    Bitmap bm;

    //Vitesse en centimètres par seconde
    double vitesseNormale;
    double vitesseLente;
    double vitesseAcceleration;

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
        time = 0;
        oldTime = 0;
        lastObstacle = 0;
        enCourse = false;
        posX = 0;
        posY = 0;
        posAcc = 0;
        posRal = 0;
        vitesseNormale = 10 * 0.0277; // 24.5 km/h en cm/ms
        vitesseLente = 9 * 0.0277;
        vitesseAcceleration = 11 *0.0277;
        sonnerie = false;
        mObstaclesCouleurs = new ArrayList();
        mRalentissementDist = new ArrayList();
        mAccelerationDist = new ArrayList();
        positionActuelle = 0;
        speedChange = false;
        for (int k = 0; k < nombreCouleursObstacles; k++) {
            Random rand = new Random();
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            int c = Color.rgb(r, g, b);
            mObstaclesCouleurs.add(c);
        }
        setContentView(R.layout.activity_course);
        ImageView imageView = (ImageView) findViewById(R.id.map);
        Bitmap bitmap = Bitmap.createBitmap(304, 304, Bitmap.Config.ARGB_8888);
        int facteur = Math.max(((int) Collections.max(mPointX) - (int) Collections.min(mPointX)) / 304 + 1, ((int) Collections.max(mPointY) - (int) Collections.min(mPointY)) / 304 + 1);
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
            //canvas.drawCircle(152 * facteur - (int) mPointY.get(j), 152 * facteur + (int) mPointX.get(j), 3, paint);
            //paint.setColor(Color.BLACK);
            int DeltaMax = Math.max((int) Collections.max(mPointX) - (int) Collections.min(mPointX), (int) Collections.max(mPointY) - (int) Collections.min(mPointY));
            if ((int) Collections.max(mPointX) - (int) Collections.min(mPointX) == 0 || (int) Collections.max(mPointY) - (int) Collections.min(mPointY) == 0) {
                canvas.drawCircle(0, 304, 1, paint);
                paint.setColor(Color.BLACK);
            } else {
                int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get(j)) * 304 / DeltaMax));
                // test
                //int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / ((int) Collections.max(mPointX) - (int) Collections.min(mPointX)));
                //int y = (int) ((304+((int) Collections.min(mPointY)- (int) mPointY.get(j)) * 304 / ((int) Collections.max(mPointY) - (int) Collections.min(mPointY))));
                canvas.drawCircle(x, y, 1, paint);
                paint.setColor(Color.BLACK);
            }
        }
        for (int i = 0; i < mObstaclesX.size(); i++) {
            int typeObstacle = (int) mTypeObstacles.get(i);
            paint.setColor((Integer) mObstaclesCouleurs.get(typeObstacle));
            int DeltaMax = Math.max((int) Collections.max(mPointX) - (int) Collections.min(mPointX), (int) Collections.max(mPointY) - (int) Collections.min(mPointY));
            if ((int) Collections.max(mPointX) - (int) Collections.min(mPointX) == 0 || (int) Collections.max(mPointY) - (int) Collections.min(mPointY) == 0) {
                canvas.drawCircle(0, 304, 1, paint);
            } else {
                int x = (int) (((int) mObstaclesX.get(i) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mObstaclesY.get(i)) * 304 / DeltaMax));
                // test
                //int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / ((int) Collections.max(mPointX) - (int) Collections.min(mPointX)));
                //int y = (int) ((304+((int) Collections.min(mPointY)- (int) mPointY.get(j)) * 304 / ((int) Collections.max(mPointY) - (int) Collections.min(mPointY))));
                paint.setColor(Color.WHITE);
                //canvas.drawRect(x-5,y+15,x+15,y+5,paint);
                paint.setColor(Color.BLUE);
                canvas.drawText(i + 1 + "", (float) x - 10, (float) y - 10, paint);
            }
        }
        for (int p = 1; p < mNumerosObstacles.size(); p++) {
            int DeltaMax = Math.max((int) Collections.max(mPointX) - (int) Collections.min(mPointX), (int) Collections.max(mPointY) - (int) Collections.min(mPointY));
            int distObst = (int) Math.round(((int) mNumerosObstacles.get(p) - (int) mNumerosObstacles.get(p - 1)) * stepLength / 100);
            if ((distObst - (distAppel)) % foulee == 1) { // On accélère
                paint.setColor(Color.GREEN);
                if ((int) mNumerosObstacles.get(p) - 5 > 0) {
                    for (int k = 1; k <= 5; k++) {
                        int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p) - k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                        int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p) - k)) * 304 / DeltaMax));
                        canvas.drawCircle(x, y, 1, paint);
                    }
                    mAccelerationDist.add(((int) mNumerosObstacles.get(p) - 5) * stepLength);
                    mAccelerationDist.add((int) mNumerosObstacles.get(p) * stepLength);
                }
            } else {
                if ((distObst - distAppel) % foulee == 2) { // On ralentit sur 2 foulees
                    paint.setColor(Color.RED);
                    if ((int) mNumerosObstacles.get(p - 1) + 9 < (int) mNumerosObstacles.get(p)) {
                        for (int k = 1; k <= 9; k++) {
                            int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p - 1) + k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                            int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p - 1) + k)) * 304 / DeltaMax));
                            canvas.drawCircle(x, y, 1, paint);
                        }
                        mRalentissementDist.add(((int) mNumerosObstacles.get(p)) * stepLength);
                        mRalentissementDist.add(((int) mNumerosObstacles.get(p) + 9) * stepLength);
                    }
                } else {
                    if ((distObst - distAppel) % foulee == 3) { // On ralentit sur 1 foulee
                        paint.setColor(Color.RED);
                        if ((int) mNumerosObstacles.get(p - 1) + 5 < (int) mNumerosObstacles.get(p)) {
                            for (int k = 1; k <= 5; k++) {
                                int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p - 1) + k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p - 1) + k)) * 304 / DeltaMax));
                                canvas.drawCircle(x, y, 1, paint);
                            }
                            mRalentissementDist.add(((int) mNumerosObstacles.get(p)) * stepLength);
                            mRalentissementDist.add(((int) mNumerosObstacles.get(p) + 5) * stepLength);
                        }
                    }
                }

            }

        }
        imageView.setImageBitmap(bitmap);
    }

    public void course() {

    }

    public void demarrerCourse(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                positionActuelle=0;
                posAcc=0;
                posRal=0;
                time = System.currentTimeMillis();
                oldTime = time;
                while (positionActuelle < mPointX.size() * stepLength) {
                    time = System.currentTimeMillis();
                    long DeltaT = time - oldTime;
                    speedChange = false;
                    if (mRalentissementDist.size() != 0) {
                        if (posRal < mRalentissementDist.size() - 1) {
                            oldTime = time;
                            if (positionActuelle >= (double) mRalentissementDist.get(posRal) && positionActuelle < (double) mRalentissementDist.get(posRal + 1)) {
                                if (!sonnerie) {
                                    ToneGenerator son = new ToneGenerator(0, (int) (ToneGenerator.MAX_VOLUME));
                                    son.startTone(ToneGenerator.TONE_DTMF_4, 1000);
                                    sonnerie = true;
                                }
                                positionActuelle += vitesseLente * DeltaT;// Ajouter vitesse ralentissement
                                speedChange = true;
                            }

                        }
                    }
                    if (mAccelerationDist.size() != 0) {
                        if (posAcc < mAccelerationDist.size() - 1) {
                            oldTime = time;
                            if (positionActuelle >= (double) mAccelerationDist.get(posAcc) && positionActuelle < (double) mAccelerationDist.get(posAcc + 1)) {
                                if (!sonnerie) {
                                    ToneGenerator son = new ToneGenerator(0, (int) (ToneGenerator.MAX_VOLUME));
                                    son.startTone(ToneGenerator.TONE_DTMF_0, 1000);
                                    sonnerie = true;
                                }
                                positionActuelle += vitesseAcceleration * DeltaT;// Ajouter vitesse acceleration
                                speedChange = true;
                            }
                        }
                    }
                    if (speedChange == false) {
                        if (posRal < mRalentissementDist.size()-1 && positionActuelle > (double) mRalentissementDist.get(posRal + 1)) {
                            posRal += 2;
                        } else {
                            if (posAcc < mAccelerationDist.size()-1 && positionActuelle > (double) mAccelerationDist.get(posAcc + 1)) {
                                posAcc += 2;
                            }
                        }
                        sonnerie = false;
                        positionActuelle += vitesseNormale * DeltaT;// Ajouter vitesse normale
                    }
                    oldTime = time;
                    double mon=positionActuelle;
                    int p=posAcc;
                    int q=posRal;
                }
            }
        }).start();

    }
}
