package com.example.timotemalherbe.compass;

import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class credit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        ToneGenerator son = new ToneGenerator(0, (int)(ToneGenerator.MAX_VOLUME + ToneGenerator.MIN_VOLUME )/2);
        son.startTone(ToneGenerator.TONE_DTMF_0,1000);
    }

    //Fonction qui permet de retourner à l'activity précédente
    public void accueil(View v){
        finish();
    }
}
