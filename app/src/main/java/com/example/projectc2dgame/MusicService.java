package com.example.projectc2dgame;



import android.app.Service;

import android.content.Context;

import android.content.Intent;

import android.content.SharedPreferences;

import android.media.MediaPlayer;

import android.os.IBinder;



public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    private boolean isMusicCurrentlyEnabledInPreferences; // Tercihlerdeki müzik ayarını tutar





    public static final String ACTION_PLAY_MUSIC = "com.example.projectc2dgame.ACTION_PLAY_MUSIC"; // Müziği çal komutu

    public static final String ACTION_PAUSE_MUSIC = "com.example.projectc2dgame.ACTION_PAUSE_MUSIC"; // Müziği duraklat komutu



    @Override

    public void onCreate() { // Servis ilk oluşturulduğunda bir kez çalışır

        super.onCreate();

        loadMusicPreference(); // Kayıtlı müzik tercihini yükle





        mediaPlayer = MediaPlayer.create(this, R.raw.menu_music); // menu_music: sizin müzik dosyanız

        if (mediaPlayer != null) {

            mediaPlayer.setLooping(true);

            mediaPlayer.setVolume(1.0f, 1.0f);



        }

    }



// SharedPreferences'dan müzik tercihini okur

    private void loadMusicPreference() {

        SharedPreferences prefs = getSharedPreferences(Options.PREFS_NAME, Context.MODE_PRIVATE); // Tercih dosyasını al



        isMusicCurrentlyEnabledInPreferences = prefs.getBoolean(Options.MUSIC_ENABLED_KEY, true);

    }



    @Override

    public int onStartCommand(Intent intent, int flags, int startId) { // Servis her başlatıldığında çalışır

        loadMusicPreference(); // En güncel müzik tercihini yükle



        String action = null;

        if (intent != null && intent.getAction() != null) {

            action = intent.getAction();

        }



// Gelen komuta göre işlem yap

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



        return START_STICKY; // Servis sistem tarafından sonlandırılırsa, mümkün olduğunda yeniden başlat (son intent olmadan)

    }



// MediaPlayer'ı güvenli bir şekilde başlatır

    private void startMusicPlayer() {

        if (mediaPlayer == null) { // MediaPlayer yoksa (örneğin, bir hata sonrası)

            recreateMediaPlayer(); // Yeniden oluşturmayı dene

            if (mediaPlayer == null) return; // Hala oluşturulamadıysa çık

        }

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) { // MediaPlayer varsa ve çalmıyorsa

            mediaPlayer.start(); // Müziği başlat

        }

    }



// MediaPlayer'ı güvenli bir şekilde duraklatır

    private void pauseMusicPlayer() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) { // MediaPlayer varsa ve çalıyorsa

            mediaPlayer.pause(); // Müziği duraklat

        }

    }



// MediaPlayer'ı yeniden oluşturur (kaynaklar serbest bırakılıp tekrar yüklenir)

    private void recreateMediaPlayer() {

        if (mediaPlayer != null) {

            mediaPlayer.release(); // Önceki MediaPlayer kaynaklarını serbest bırak

        }

        mediaPlayer = MediaPlayer.create(this, R.raw.menu_music); // Yeniden oluştur

        if (mediaPlayer != null) {

            mediaPlayer.setLooping(true); // Döngüyü ayarla

            mediaPlayer.setVolume(1.0f, 1.0f); // Sesi ayarla

// Yeniden oluşturulduktan sonra direkt başlatma, çağıran metod tercihe göre karar verir

        }

    }



    @Override

    public void onDestroy() { // Servis sonlandırılmadan hemen önce çalışır

        super.onDestroy();

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.stop(); // Çalıyorsa durdur

            }

            mediaPlayer.release(); // MediaPlayer kaynaklarını serbest bırak

            mediaPlayer = null; // Referansı temizle

        }

    }



    @Override

    public IBinder onBind(Intent intent) { // Servisi başka bir bileşene bağlamak için (bu serviste kullanılmıyor)

// Bu servis "started service" türünde, "bound service" değil.

// Bu yüzden onBind null döndürür.

        return null;

    }

}