package com.example.timotemalherbe.compass;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Carte extends AppCompatActivity {


    ArrayList mPointX;
    ArrayList mPointY;
    ArrayList mTypeObstacles;
    ArrayList mObstaclesX;
    ArrayList mObstaclesY;
    ArrayList mObstaclesCouleurs;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        mPointX=intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_POINTX);
        mPointY=intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_POINTY);
        mTypeObstacles=intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_TYPEOBSTACLES);
        mObstaclesX=intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_OBSTACLESX);
        mObstaclesY=intent.getIntegerArrayListExtra(EnregistrementParcours.EXTRA_OBSTACLESY);
        int nombreCouleursObstacles=intent.getIntExtra(EnregistrementParcours.EXTRA_OBSTACLESTYPESNBR,4);
        mObstaclesCouleurs=new ArrayList();
        for (int k=0;k<nombreCouleursObstacles;k++){
            Random rand = new Random();
            int r=rand.nextInt(256);
            int g=rand.nextInt(256);
            int b=rand.nextInt(256);
            int c= Color.rgb(r,g,b);
            mObstaclesCouleurs.add(c);
        }
        setContentView(R.layout.activity_carte);
        ImageView imageView=(ImageView) findViewById(R.id.image);
        Bitmap bitmap = Bitmap.createBitmap(304,304, Bitmap.Config.ARGB_8888);
        int facteur=Math.max(((int) Collections.max(mPointX) - (int) Collections.min(mPointX))/304+1,((int) Collections.max(mPointY) - (int) Collections.min(mPointY))/304+1);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        for (int j=0;j<mPointX.size();j++) {
            if (j == 0) {
                paint.setColor(Color.RED);
            }
            if (j == mPointX.size() - 1) {
                paint.setColor(Color.BLUE);
            }
            //canvas.drawCircle(152 * facteur - (int) mPointY.get(j), 152 * facteur + (int) mPointX.get(j), 3, paint);
            //paint.setColor(Color.BLACK);
            int DeltaMax=Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX)==0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 3, paint);
                paint.setColor(Color.BLACK);
            }else {
                int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mPointY.get(j)) * 304 / DeltaMax));
                // test
                //int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / ((int) Collections.max(mPointX) - (int) Collections.min(mPointX)));
                //int y = (int) ((304+((int) Collections.min(mPointY)- (int) mPointY.get(j)) * 304 / ((int) Collections.max(mPointY) - (int) Collections.min(mPointY))));
                canvas.drawCircle(x, y, 3, paint);
                paint.setColor(Color.BLACK);
            }
        }
        for (int i=0;i<mObstaclesX.size();i++) {
            int typeObstacle= (int) mTypeObstacles.get(i);
            paint.setColor((Integer) mObstaclesCouleurs.get(typeObstacle));
            int DeltaMax=Math.max((int)Collections.max(mPointX)-(int)Collections.min(mPointX),(int)Collections.max(mPointY)-(int)Collections.min(mPointY));
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX)==0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 3, paint);
            }else {
                int x = (int) (((int) mObstaclesX.get(i) - (int) Collections.min(mPointX)) * 304 / DeltaMax);
                int y = (int) ((304 + ((int) Collections.min(mPointY) - (int) mObstaclesY.get(i)) * 304 / DeltaMax));
                // test
                //int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / ((int) Collections.max(mPointX) - (int) Collections.min(mPointX)));
                //int y = (int) ((304+((int) Collections.min(mPointY)- (int) mPointY.get(j)) * 304 / ((int) Collections.max(mPointY) - (int) Collections.min(mPointY))));
                canvas.drawCircle(x,y, 3, paint);
            }
        }
        imageView.setImageBitmap(bitmap);

        ListView mListView2;

        List<Obstacle> legends= new ArrayList<Obstacle>();

        mListView2 = (ListView) findViewById(R.id.listView2);
        mListView2.setClickable(false);

        Obstacle oVerticaux=new Obstacle(R.drawable.rouge,"Obstacles verticaux :"," ");
        Obstacle oLarges=new Obstacle(R.drawable.bleu,"Obstacles larges : "," ");
        Obstacle oVolee=new Obstacle(R.drawable.vert,"Obstacles de volée : "," ");
        Obstacle oNaturels=new Obstacle(R.drawable.jaune,"Obstacles naturels : "," ");

        legends.add(oVerticaux);
        legends.add(oLarges);
        legends.add(oVolee);
        legends.add(oNaturels);

        ObstacleViewAdapter adapter2 = new ObstacleViewAdapter(this, legends);
        mListView2.setAdapter(adapter2);
    }

    public void accueil(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
