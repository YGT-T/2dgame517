package com.example.projectc2dgame;



import android.content.Intent;

import android.media.MediaPlayer; // MediaPlayer sınıfını dahil et

import android.os.Bundle;

import android.view.View;



import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;

import androidx.core.view.WindowInsetsCompat;



public class MainMenu extends AppCompatActivity {



    private MediaPlayer buttonEffectPlayer; // Buton sesi için MediaPlayer



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main_menu); // Kendi layout'unuzu kullanın









        Intent musicIntent = new Intent(this, MusicService.class);

        startService(musicIntent);





// Buton sesi için MediaPlayer oluştur

        buttonEffectPlayer = MediaPlayer.create(this, R.raw.button_effect);

        if (buttonEffectPlayer != null) {

            buttonEffectPlayer.setVolume(1.0f, 1.0f); // Ses seviyesini ayarla

        }

    }



// Buton tıklama sesini çalan yardımcı metot

    private void playButtonEffect() {

        if (buttonEffectPlayer != null) {

// Eğer daha önce çalmışsa ve durmuşsa sıfırla ve tekrar hazırla

            if (buttonEffectPlayer.isPlaying()) {

                buttonEffectPlayer.stop();

            }

            buttonEffectPlayer.seekTo(0); // Sesin en başından çalmasını sağlar

            buttonEffectPlayer.start();

        }

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

// Buton sesi MediaPlayer kaynaklarını serbest bırak

        if (buttonEffectPlayer != null) {

            buttonEffectPlayer.release();

            buttonEffectPlayer = null;

        }

// NOT: MusicService burada durdurulmuyor, çünkü uygulamanın diğer bölümlerinde de çalması isteniyor.

// Sadece uygulama tamamen kapatıldığında durdurulmalı.

// Örneğin quit() metodunda veya uygulamanın sonlandığı yerde.

    }



    public void goOptions(View view) {

        playButtonEffect(); // Buton sesini çal

        Intent intent = new Intent(this, Options.class);

        startActivity(intent);

    }

    public void Play(View view) {

        playButtonEffect(); // Buton sesini çal

        Intent intent = new Intent(this, Sections.class);

        startActivity(intent);

    }

    public void goLeader(View view) {

        playButtonEffect(); // Buton sesini çal

        Intent intent = new Intent(this, HowtoPlay.class);

        startActivity(intent);

    }

    public void quit(View view) {



// Uygulamadan çıkarken MusicService'i durdur

        Intent musicIntent = new Intent(this, MusicService.class);

        stopService(musicIntent);



        finishAffinity();

        System.exit(0);

    }

}