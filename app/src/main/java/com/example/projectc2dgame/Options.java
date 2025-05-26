package com.example.projectc2dgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Options extends AppCompatActivity {

    private SwitchCompat switchMusic;
    public static final String PREFS_NAME = "GameSettingsPrefs";
    public static final String MUSIC_ENABLED_KEY = "musicEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_options); // XML dosyanızın adı

        // XML'inizde root layout ID'sinin "settings_main" olduğundan emin olun.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        switchMusic = findViewById(R.id.switchMusic); // XML'deki switchMusic ID'li SwitchCompat

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isMusicCurrentlyEnabled = prefs.getBoolean(MUSIC_ENABLED_KEY, true); // Varsayılan: müzik açık
        switchMusic.setChecked(isMusicCurrentlyEnabled);

        // Aktivite ilk oluşturulduğunda MusicService'i mevcut tercihe göre bilgilendir.
        // Bu, özellikle servisin START_STICKY nedeniyle yeniden başlayıp
        // varsayılan olarak müzik çalması durumunu ele alır.
        sendMusicCommand(isMusicCurrentlyEnabled);


        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(MUSIC_ENABLED_KEY, isChecked);
                editor.apply();

                sendMusicCommand(isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Aktivite yeniden görünür olduğunda Switch'in durumunu SharedPreferences'a göre güncelle.
        // onCreate'de zaten servise komut gönderildiği için burada tekrar servise komut göndermeye
        // genellikle gerek yoktur, ancak uygulamanızın akışına göre gerekebilir.
        // Şimdilik sadece switch'i güncelliyoruz.
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean musicShouldBeOn = prefs.getBoolean(MUSIC_ENABLED_KEY, true);
        if (switchMusic != null) { // switchMusic null olabilir eğer layout'ta yoksa veya ID yanlışsa
            switchMusic.setChecked(musicShouldBeOn);
        }
    }

    private void sendMusicCommand(boolean play) {
        Intent serviceIntent = new Intent(Options.this, MusicService.class);
        if (play) {
            serviceIntent.setAction(MusicService.ACTION_PLAY_MUSIC);
        } else {
            serviceIntent.setAction(MusicService.ACTION_PAUSE_MUSIC);
        }
        startService(serviceIntent);
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
    }
}
