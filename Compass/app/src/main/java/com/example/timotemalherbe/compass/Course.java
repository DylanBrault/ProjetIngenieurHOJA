package com.example.timotemalherbe.compass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Course extends AppCompatActivity {

    // Variables à transférer au changement d'activité
    public static final String EXTRA_POINTX = "com.example.timotemalherbe.POINTX";
    public static final String EXTRA_POINTY = "com.example.timotemalherbe.POINTY";
    public static final String EXTRA_TYPEOBSTACLES = "com.example.timotemalherbe.TYPEOBSTACLES";
    public static final String EXTRA_OBSTACLESX = "com.example.timotemalherbe.OBSTACLESX";
    public static final String EXTRA_OBSTACLESY = "com.example.timotemalherbe.OBSTACLESY";
    public static final String EXTRA_NUMEROSOBSTACLES = "com.example.timotemalherbe.NUMEROSOBSTACLES";
    public static final String EXTRA_DISTANCE="com.example.timotemalherbe.DISTANCE";
    public static final String EXTRA_OBSTACLESTYPESNBR="com.example.timotemalherbe.OBSTACLESTYPESNBR";

    int foulee;
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
    ArrayList mDistAppel;
    int nombreCouleursObstacles; // Nombre de types d'obstacles différents

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

        // On récupère les variables transférées d'une activité à l'autre
        Intent intent = getIntent();
        mPointX = intent.getIntegerArrayListExtra(MainActivity.EXTRA_POINTX);
        mPointY = intent.getIntegerArrayListExtra(MainActivity.EXTRA_POINTY);
        mTypeObstacles = intent.getIntegerArrayListExtra(MainActivity.EXTRA_TYPEOBSTACLES);
        mObstaclesX = intent.getIntegerArrayListExtra(MainActivity.EXTRA_OBSTACLESX);
        mObstaclesY = intent.getIntegerArrayListExtra(MainActivity.EXTRA_OBSTACLESY);
        int nombreCouleursObstacles = intent.getIntExtra(MainActivity.EXTRA_OBSTACLESTYPESNBR, 4);
        mNumerosObstacles = intent.getIntegerArrayListExtra(MainActivity.EXTRA_NUMEROSOBSTACLES);
        stepLength = intent.getDoubleExtra(MainActivity.EXTRA_DISTANCE, 0.0);
        mDistAppel=intent.getIntegerArrayListExtra(MainActivity.EXTRA_DISTANCEAPPEL);

        // On initialise les variables pour le tracé de la carte :
        foulee = 4;
        time = 0;
        oldTime = 0;
        lastObstacle = 0;
        enCourse = false;
        posX = 0;
        posY = 0;
        posAcc = 0;
        posRal = 0;
        vitesseNormale = 24.5 * 0.0277; // 24.5 km/h en cm/ms
        vitesseLente = 22 * 0.0277;// 22 km/h en cm/ms
        vitesseAcceleration = 27 *0.0277;// 27 km/h en cm/ms
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

        // On commence le tracé de la carte :
        ImageView imageView = (ImageView) findViewById(R.id.map);
        Bitmap bitmap = Bitmap.createBitmap(304,304, Bitmap.Config.ARGB_8888);

        // On calcule un facteur de redimensionnement de la carte
        int facteur = Math.max(((int) Collections.max(mPointX) - (int) Collections.min(mPointX))/304+1,((int) Collections.max(mPointY) - (int) Collections.min(mPointY))/304+1);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK); // La couleur du pinceau est noire

        // Tracé de la trajectoire :
        for (int j = 0; j < mPointX.size(); j++) { // Pour tous les points de la trajectoire
            if (j == 0) { // Si c'est le premier point
                paint.setColor(Color.RED); // On change la couleur du pinceau à rouge

                // On calcule l'angle de l'icône de départ
                int prevPointX=(int)mPointX.get(0);
                int prevPointY=(int)mPointY.get(0);
                int nextPointX=(int)mPointX.get(1);
                int nextPointY=(int)mPointY.get(1);
                Matrix matrix = new Matrix();
                int angle= 90-(int) (Math.atan2(nextPointY-prevPointY,nextPointX-prevPointX)*360/(2*3.14));

                // On fait pivoter l'icône
                matrix.postRotate(angle);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.start);
                Bitmap rotateIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);

                int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
                if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX)==0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                    canvas.drawCircle(0, 304, 1, paint);
                    paint.setColor(Color.BLACK);
                }else {
                    // On calcule les coordonnées à utiliser pour tracer l'icône
                    int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                    int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get(j)) * 304 / DeltaMax));
                    // On dessine l'icône
                    canvas.drawBitmap(rotateIcon, null, new Rect(x - 25, y - 25, x + 25, y + 25), null);
                }
            }
            if (j == mPointX.size() - 1) { // Si il s'agit du dernier point
                paint.setColor(Color.BLUE); // On change la couleur du pinceau à bleu

                // On calcule l'angle de l'icone de fin
                int prevPointX = (int) mPointX.get(mPointX.size() - 2);
                int prevPointY = (int) mPointY.get(mPointX.size() - 2);
                int nextPointX = (int) mPointX.get(mPointX.size() - 1);
                int nextPointY = (int) mPointY.get(mPointX.size() - 1);
                Matrix matrix = new Matrix();
                int angle = 90 - (int) (Math.atan2(nextPointY - prevPointY, nextPointX - prevPointX) * 360 / (2 * 3.14));

                // On pivote l'angle de fin
                matrix.postRotate(angle);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.finish);
                Bitmap rotateIcon = Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, true);

                int DeltaMax = Math.max((int) Collections.max(mPointX) - (int) Collections.min(mPointX), (int) Collections.max(mPointY) - (int) Collections.min(mPointY));
                if ((int) Collections.max(mPointX) - (int) Collections.min(mPointX) == 0 || (int) Collections.max(mPointY) - (int) Collections.min(mPointY) == 0) {
                    canvas.drawCircle(0, 304, 1, paint);
                    paint.setColor(Color.BLACK);
                } else {
                    // On calcule les coordonnées de l'icône à tracer
                    int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                    int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get(j)) * 304 / DeltaMax));
                    // On dessine l'icône
                    canvas.drawBitmap(rotateIcon, null, new Rect(x - 25, y - 25, x + 25, y + 25), null);
                }
            }

            int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX)==0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 1, paint);
                paint.setColor(Color.BLACK);
            }else {
                // On calcule les coordonées du point de la trajectoire
                int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get(j)) * 304 / DeltaMax));

                // On dessine le point sur la carte
                canvas.drawCircle(x, y, 1, paint);
                paint.setColor(Color.BLACK); // On fixe la couleur à noir
            }
        }

        // Tracé des obstacles :
        for (int i = 0; i < mObstaclesX.size(); i++) { // Pour tous les obstacles
            int typeObstacle = (int) mTypeObstacles.get(i); // On recupère le type de l'obstacle
            paint.setColor((Integer) mObstaclesCouleurs.get(typeObstacle)); // On récupère la couleur de l'obstacle
            int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX) == 0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 1, paint);
            }else {

                // On calcule la coordonnée de l'obsatcle
                int x = (int) (((int) mObstaclesX.get(i) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mObstaclesY.get(i)) * 304 / DeltaMax));

                // On fonction du type d'obstacle choisi on prend le bon icône que l'on oriente :
                if (typeObstacle==0) {
                    int prevPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)-1);
                    int prevPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)-1);
                    int nextPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)+1);
                    int nextPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)+1);
                    Matrix matrix = new Matrix();
                    int angle= 90-(int) (Math.atan2(nextPointY-prevPointY,nextPointX-prevPointX)*360/(2*3.14));
                    matrix.postRotate(angle);
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.droit);
                    Bitmap rotateIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);
                    canvas.drawBitmap(rotateIcon,null, new Rect(x-25,y-25,x+25,y+25) , null);
                }else{
                    if (typeObstacle==1) {
                        int prevPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)-1);
                        int prevPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)-1);
                        int nextPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)+1);
                        int nextPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)+1);
                        Matrix matrix = new Matrix();
                        int angle= 90-(int) (Math.atan2(nextPointY-prevPointY,nextPointX-prevPointX)*360/(2*3.14));
                        matrix.postRotate(angle);
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.oxer);
                        Bitmap rotateIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);
                        canvas.drawBitmap(rotateIcon,null, new Rect(x-25,y-25,x+25,y+25) , null);
                    }else{
                        if (typeObstacle==2) {
                            int prevPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)-1);
                            int prevPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)-1);
                            int nextPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)+1);
                            int nextPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)+1);
                            Matrix matrix = new Matrix();
                            int angle= 90-(int) (Math.atan2(nextPointY-prevPointY,nextPointX-prevPointX)*360/(2*3.14));
                            matrix.postRotate(angle);
                            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.riviere);
                            Bitmap rotateIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);
                            canvas.drawBitmap(rotateIcon,null, new Rect(x-25,y-25,x+25,y+25) , null);
                        }else{
                            if (typeObstacle==3) {
                                int prevPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)-1);
                                int prevPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)-1);
                                int nextPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)+1);
                                int nextPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)+1);
                                Matrix matrix = new Matrix();
                                int angle= 90-(int) (Math.atan2(nextPointY-prevPointY,nextPointX-prevPointX)*360/(2*3.14));
                                matrix.postRotate(angle);
                                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.obstacle_riviere);
                                Bitmap rotateIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);
                                canvas.drawBitmap(rotateIcon,null, new Rect(x-25,y-25,x+25,y+25) , null);
                            }else{
                                if (typeObstacle==4) {
                                    int prevPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)-1);
                                    int prevPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)-1);
                                    int nextPointX=(int)mPointX.get((int)mNumerosObstacles.get(i)+1);
                                    int nextPointY=(int)mPointY.get((int)mNumerosObstacles.get(i)+1);
                                    Matrix matrix = new Matrix();
                                    int angle= 90-(int) (Math.atan2(nextPointY-prevPointY,nextPointX-prevPointX)*360/(2*3.14));
                                    matrix.postRotate(angle);
                                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.triple);
                                    Bitmap rotateIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);
                                    canvas.drawBitmap(rotateIcon,null, new Rect(x-25,y-25,x+25,y+25) , null);
                                }
                            }
                        }
                    }
                }

                // Ajout des numéros
                paint.setColor(Color.BLUE); // On fixe le pinceau à la couleur bleue
                canvas.drawText(i+1+"",(float)x-10,(float)y-10,paint); // On affiche le numéro
            }
        }

        // Tracé des zones de reprise et accéleration
        for (int p = 1; p < mNumerosObstacles.size(); p++) { // Pour tous les obstacles
            int distAppel= (int) mDistAppel.get(p);
            int DeltaMax = Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            int distObst= (int) Math.round(((int) mNumerosObstacles.get(p)-(int) mNumerosObstacles.get(p-1))*stepLength/100);
            if ((distObst-(distAppel))%foulee==1){ // On accélère
                paint.setColor(Color.GREEN); //On fixe à vert la couleur du pinceau

                // On parcourt les 4=distAppel/2 points précedents l'obstacle actuel
                if ((int)mNumerosObstacles.get(p)-(4+distAppel/2)>0) { // On accélere avant l'obstacle
                    for (int k=1;k<=(4+distAppel/2);k++) {
                        int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p) - k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                        int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p) - k)) * 304 / DeltaMax));
                        canvas.drawCircle(x, y, 1, paint);
                    }
                    mAccelerationDist.add(((int) mNumerosObstacles.get(p) - 5) * stepLength);
                    mAccelerationDist.add((int) mNumerosObstacles.get(p) * stepLength);
                }
            }else{
                // On parcourt les 8+distAppel/2 points suivant l'obstacle précedent
                if ((distObst-distAppel)%foulee==2){ // On ralentit sur 2 foulees
                    paint.setColor(Color.RED);
                    if ((int)mNumerosObstacles.get(p-1)+(8+distAppel/2)<(int)mNumerosObstacles.get(p)){
                        for (int k=1;k<=(8+distAppel/2);k++) {
                            int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p-1) + k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                            int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p-1) + k)) * 304 / DeltaMax));
                            canvas.drawCircle(x, y, 1, paint);
                        }
                        mRalentissementDist.add(((int) mNumerosObstacles.get(p)) * stepLength);
                        mRalentissementDist.add(((int) mNumerosObstacles.get(p) + 9) * stepLength);
                    }
                }else{
                    // On parcourt les 4+distAppel/2 points suivant l'obstacle précedent
                    if ((distObst-distAppel)%foulee==3){ // On ralentit sur 1 foulee
                        paint.setColor(Color.RED);
                        if ((int)mNumerosObstacles.get(p-1)+(4+distAppel/2)<(int)mNumerosObstacles.get(p)) {
                            for (int k=1;k<=(4+distAppel);k++) {
                                int x = (int) (((int) mPointX.get((Integer) mNumerosObstacles.get(p-1) + k) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get((Integer) mNumerosObstacles.get(p-1) + k)) * 304 / DeltaMax));
                                canvas.drawCircle(x, y, 1, paint);
                            }
                            mRalentissementDist.add(((int) mNumerosObstacles.get(p)) * stepLength);
                            mRalentissementDist.add(((int) mNumerosObstacles.get(p) + 5) * stepLength);
                        }
                    }
                }

            }

        }
        imageView.setImageBitmap(bitmap); // On affiche la carte
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
                }
            }
        }).start();

    }

    public void accueil(View v){
        //Lorsqu'on clique sur le bouton accueil on retransmet toutes les données
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_POINTX, mPointX);
        intent.putExtra(EXTRA_POINTY, mPointY);
        intent.putExtra(EXTRA_TYPEOBSTACLES, mTypeObstacles);
        intent.putExtra(EXTRA_OBSTACLESX,mObstaclesX);
        intent.putExtra(EXTRA_OBSTACLESY,mObstaclesY);
        intent.putExtra(EXTRA_NUMEROSOBSTACLES,mNumerosObstacles);
        intent.putExtra(EXTRA_DISTANCE,stepLength);
        intent.putExtra(EXTRA_OBSTACLESTYPESNBR,nombreCouleursObstacles);
        startActivity(intent);
    }

    public void modifVitesse(View v){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Course.this);
        alertDialog.setTitle("Vitesse lente");
        alertDialog.setMessage("Entrez la vitesse lente en km/h : ");
        final EditText input = new EditText(Course.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        vitesseLente=Double.parseDouble(input.getText().toString())* 0.0277;
                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Course.this);
                        alertDialog2.setTitle("Vitesse moyenne");
                        alertDialog2.setMessage("Entrez la vitesse moyenne en km/h : ");
                        final EditText input2 = new EditText(Course.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input2.setLayoutParams(lp);
                        alertDialog2.setView(input2);
                        alertDialog2.setNeutralButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        vitesseNormale=Double.parseDouble(input2.getText().toString())* 0.0277;
                                        AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(Course.this);
                                        alertDialog3.setTitle("Vitesse rapide");
                                        alertDialog3.setMessage("Entrez la vitesse rapide en km/h : ");
                                        final EditText input3 = new EditText(Course.this);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                        input3.setLayoutParams(lp);
                                        alertDialog3.setView(input3);
                                        alertDialog3.setNeutralButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        vitesseAcceleration=Double.parseDouble(input3.getText().toString())* 0.0277;
                                                    }
                                                }
                                        );
                                        alertDialog3.show();
                                    }
                                }
                        );
                        alertDialog2.show();
                    }
                });
        alertDialog.show();
    }
}
