package com.example.timotemalherbe.compass;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Carte extends AppCompatActivity {

    ArrayList mPointX;
    ArrayList mPointY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        mPointX=intent.getIntegerArrayListExtra(Reconnaissance.EXTRA_POINTX);
        mPointY=intent.getIntegerArrayListExtra(Reconnaissance.EXTRA_POINTY);
        setContentView(R.layout.activity_carte);
        ImageView imageView=(ImageView) findViewById(R.id.image);
        Bitmap bitmap = Bitmap.createBitmap(304,304, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        for (int j=0;j<mPointX.size();j++){
            if (j==0){
                paint.setColor(Color.RED);
            }
            if (j==mPointX.size()-1){
                paint.setColor(Color.BLUE);
            }
            if ((int)Collections.max(mPointX)-(int)Collections.min(mPointX)==0 ||(int)Collections.max(mPointY)-(int)Collections.min(mPointY)==0){
                canvas.drawCircle(0, 304, 3, paint);
                paint.setColor(Color.BLACK);
            }else{
                int x = (int) (((int) mPointX.get(j) - (int) Collections.min(mPointX)) * 304 / ((int) Collections.max(mPointX) - (int) Collections.min(mPointX)));
                int y = (int) ((304+((int) Collections.min(mPointY)- (int) mPointY.get(j)) * 304 / ((int) Collections.max(mPointY) - (int) Collections.min(mPointY))));
                canvas.drawCircle(y, x, 3, paint);
                paint.setColor(Color.BLACK);
            }
        }
        imageView.setImageBitmap(bitmap);
    }

}