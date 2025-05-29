package com.example.projectc2dgame;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isMusicCurrentlyEnabledInPreferences;

    public static final String ACTION_PLAY_MUSIC = "com.example.projectc2dgame.ACTION_PLAY_MUSIC";
    public static final String ACTION_PAUSE_MUSIC = "com.example.projectc2dgame.ACTION_PAUSE_MUSIC";

    @Override
    public void onCreate() {
        super.onCreate();
        loadMusicPreference();

        mediaPlayer = MediaPlayer.create(this, R.raw.menu_music);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f);
        }
    }

    private void loadMusicPreference() {
        SharedPreferences prefs = getSharedPreferences(Options.PREFS_NAME, Context.MODE_PRIVATE);
        isMusicCurrentlyEnabledInPreferences = prefs.getBoolean(Options.MUSIC_ENABLED_KEY, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadMusicPreference();

        String action = null;
        if (intent != null && intent.getAction() != null) {
            action = intent.getAction();
        }

        if (ACTION_PLAY_MUSIC.equals(action)) {
            if (isMusicCurrentlyEnabledInPreferences) {
                startMusicPlayer();
            } else {
                pauseMusicPlayer();
            }
        } else if (ACTION_PAUSE_MUSIC.equals(action)) {
            pauseMusicPlayer();
        } else {
            if (isMusicCurrentlyEnabledInPreferences) {
                startMusicPlayer();
            } else {
                pauseMusicPlayer();
            }
        }

        return START_STICKY;
    }

    private void startMusicPlayer() {
        if (mediaPlayer == null) {
            recreateMediaPlayer();
            if (mediaPlayer == null) return;
        }
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void pauseMusicPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
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