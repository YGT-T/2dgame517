package com.example.projectc2dgame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
// import android.util.Log; // Logları daha önce kaldırmıştık

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    // OptionsActivity'den gönderilecek komutlar için Action String'leri
    public static final String ACTION_PLAY_MUSIC = "com.example.projectc2dgame.ACTION_PLAY_MUSIC";
    public static final String ACTION_PAUSE_MUSIC = "com.example.projectc2dgame.ACTION_PAUSE_MUSIC";

    @Override
    public void onCreate() {
        super.onCreate();
        // MediaPlayer'ı oluştur ve sürekli döngüde çalmasını sağla
        mediaPlayer = MediaPlayer.create(this, R.raw.menu_music); // menu_music dosyasını kendi müziğinizle değiştirin
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true); // Müziğin sürekli döngüde çalmasını sağla
            mediaPlayer.setVolume(1.0f, 1.0f); // Maksimum ses seviyesi
        } else {
            // MediaPlayer oluşturulamadı
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (ACTION_PLAY_MUSIC.equals(action)) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                } else if (mediaPlayer == null) {
                    recreateMediaPlayer();
                    if (mediaPlayer != null) mediaPlayer.start();
                }
            } else if (ACTION_PAUSE_MUSIC.equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        } else {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            } else if (mediaPlayer == null) {
                recreateMediaPlayer();
                if (mediaPlayer != null) mediaPlayer.start();
            }
        }
        return START_STICKY;
    }

    private void recreateMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.menu_music);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
