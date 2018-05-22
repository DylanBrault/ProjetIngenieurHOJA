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
    }

    //Fonction qui permet de retourner à l'activity précédente
    public void accueil(View v){
        finish();
    }
}
