package com.example.projectc2dgame;



import android.content.Intent;

// MediaPlayer importu kaldırıldı

import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;

import android.view.View;

import android.widget.TextView; // Eklendi: TextView için



import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;

import androidx.core.view.WindowInsetsCompat;



public class HowtoPlay extends AppCompatActivity {



    @Override

    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_howtoplay);





        TextView textView2 = findViewById(R.id.textView2);

        if (textView2 != null) { // Null kontrolü

            textView2.setMovementMethod(new ScrollingMovementMethod());

        }





    }



    public void goBack(View view) {



        Intent intent = new Intent(this, MainMenu.class);

        startActivity(intent);



    }





}