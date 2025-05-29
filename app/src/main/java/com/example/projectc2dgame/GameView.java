package com.example.projectc2dgame; // Paket adınız farklıysa bunu güncelleyin



import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.util.DisplayMetrics;

import android.view.GestureDetector;

import android.view.MotionEvent;

import android.view.SurfaceHolder;

import android.view.SurfaceView;

import android.app.Activity;



public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;

    private GestureDetector gestureDetector;

    private boolean isPaused = false;

    private boolean isGameOver = false; // Oyunun bitip bitmediğini kontrol eder



    private ObstacleManager obstacleManager;



    private Cat cat1, cat2;

    private long currentTime;

    private Background bg_1;

    private Bitmap bg_Image;



    private Bitmap heart_1, heart_2, heart_3;

    private long cat1LastHitTime = 0, cat2LastHitTime = 0;

    private static final long INVULNERABILITY_TIME = 1500;



    private Bitmap pauseButton;

    private int pauseButtonX, pauseButtonY, pauseButtonSize;



    private Bitmap resumeButton;

    private int buttonWidth = 208; // resume ve restart butonları için ortak genişlik

    private int buttonHeight = 64; // resume ve restart butonları için ortak yükseklik

    private int buttonSpacing = 86; // butonlar arası dikey boşluk

    private int resumeX, resumeY;



    private Bitmap restartButton; // Yeniden başlatma butonu için Bitmap

    private int restartButtonX, restartButtonY; // Yeniden başlatma butonunun koordinatları





    public GameView(Context context) {

        super(context);



        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        int screenHeight = displayMetrics.heightPixels;



        bg_Image = Bitmap.createScaledBitmap(

                BitmapFactory.decodeResource(getResources(), R.drawable.bg_1),

                screenWidth,

                screenHeight,

                true

        );



        heart_1 = BitmapFactory.decodeResource(getResources(), R.drawable.heart_1);

        heart_1 = Bitmap.createScaledBitmap(heart_1, 96, 64, false);



        heart_2 = BitmapFactory.decodeResource(getResources(), R.drawable.heart_2);

        heart_2 = Bitmap.createScaledBitmap(heart_2, 192, 64, false);



        heart_3 = BitmapFactory.decodeResource(getResources(), R.drawable.heart_3);

        heart_3 = Bitmap.createScaledBitmap(heart_3, 288, 64, false);



        getHolder().addCallback(this);

        gestureDetector = new GestureDetector(context, new GestureListener());



        thread = new GameThread(getHolder(), this);

        setFocusable(true);

    }



    public void togglePause() {

        isPaused = !isPaused;

    }



    public boolean isGamePaused() {

        return isPaused;

    }



    @Override

    public void surfaceCreated(SurfaceHolder holder) {

        int screenWidth = getWidth();

        int screenHeight = getHeight();



// Pause ve Resume butonları

        pauseButtonSize = 100;

        pauseButton = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);

        pauseButton = Bitmap.createScaledBitmap(pauseButton, pauseButtonSize, pauseButtonSize, false);

        pauseButtonX = (getWidth() - pauseButtonSize) / 2;

        pauseButtonY = 30;



        resumeButton = BitmapFactory.decodeResource(getResources(), R.drawable.resume_button);

        resumeButton = Bitmap.createScaledBitmap(resumeButton, buttonWidth, buttonHeight, false); // buttonWidth ve buttonHeight kullanılıyor

        resumeX = (screenWidth - buttonWidth) / 2;

        resumeY = (screenHeight - buttonHeight) / 2 - buttonSpacing / 2; // Ortalamak için biraz yukarı



// Yeniden Başlatma Butonu (Restart Button)

// res/drawable klasörünüze 'restart_button.png' adında bir resim eklediğinizi varsayıyoruz.

        try {

// Eğer R.drawable.restart_button tanımlı değilse projenize ekleyin.

            restartButton = BitmapFactory.decodeResource(getResources(), R.drawable.restart_button);

            restartButton = Bitmap.createScaledBitmap(restartButton, buttonWidth, buttonHeight, false);

        } catch (Exception e) {

            restartButton = null; // Resim bulunamazsa null ata, çizimde yazı kullanırız

            System.err.println("Yeniden başlatma butonu resmi yüklenemedi: " + e.getMessage() + ". Metin olarak çizilecek.");

        }

        restartButtonX = (screenWidth - buttonWidth) / 2;

        restartButtonY = resumeY + buttonHeight + buttonSpacing; // Resume butonunun altına yerleştir



        initializeGameEntities(); // Oyun varlıklarını başlatan metod



// thread ve focusable zaten constructor'da ayarlanmıştı, tekrarına gerek yok

// Sadece thread'i başlat

        if (thread.getState() == Thread.State.NEW) {

            thread.setRunning(true);

            thread.start();

        } else {

// Eğer thread zaten çalışıyorsa veya sonlanmışsa yeniden oluşturup başlatmak gerekebilir.

// Ancak genellikle surfaceCreated yeniden çağrıldığında thread ya yeni ya da join ile bitirilmiş olur.

// Güvenlik için:

            thread = new GameThread(getHolder(), this);

            thread.setRunning(true);

            thread.start();

        }

    }



    private void initializeGameEntities() {

        int screenWidth = getWidth();

        int screenHeight = getHeight();



// cat1 (Sağ oyuncu, isPlayerOne=false)

        cat1 = new Cat(getWidth(), getHeight(),

                Sections.characterRunMirrorSprite,

                Sections.characterJumpMirrorSprite,

                Sections.characterIdleMirrorSprite,

                BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_starting_mirror_sprite),

                Sections.characterAttackMirrorSprite,

                Sections.characterHurtMirrorSprite,

                Sections.characterDeadMirrorSprite,

                Sections.characterShieldMirrorSprite,

                3, 0, // initialLives, initialScore

                8, 12, 6, 6, 5, 2, 4, 4, // frame counts

                40, 280, 40, // spriteWidth, spriteHeight, an_idle_left_x_offset

                false, true, false, true); // isPlayerOne, loopIdle, loopStart, isGameStartInitially



// cat2 (Sol oyuncu, isPlayerOne=true)

        cat2 = new Cat(getWidth(), getHeight(),

                Sections.characterRunSprite,

                Sections.characterJumpSprite,

                Sections.characterIdleSprite,

                BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_starting_sprite),

                Sections.characterAttackSprite,

                Sections.characterHurtSprite,

                Sections.characterDeadSprite,

                Sections.characterShieldSprite,

                3, 0, // initialLives, initialScore

                8, 12, 6, 6, 5, 2, 4, 4, // frame counts

                40, 280, 30, // spriteWidth, spriteHeight, an_idle_left_x_offset

                true, true, false, false); // isPlayerOne, loopIdle, loopStart, isGameStartInitially



        bg_1 = new Background(bg_Image, 0, 0, 15);

        obstacleManager = new ObstacleManager(getContext(), screenHeight, screenWidth);



// Oyun durumu değişkenlerini sıfırla

        cat1LastHitTime = 0;

        cat2LastHitTime = 0;

        isPaused = false;

        isGameOver = false;

    }



    public void update() {

        if (isPaused || isGameOver) return; // Oyun duraklatılmışsa veya bitmişse güncelleme yapma



        currentTime = System.currentTimeMillis();

        cat1.update(currentTime);

        cat2.update(currentTime);



// cat1.isGameStart, oyunun başlangıç animasyonunda olup olmadığını belirtir.

// Eğer false ise, asıl oyun başlamıştır.

        if (!cat1.isGameStart) {

            cat1.score += 10; // Skorlar her iki kedi için de artıyor, bu oyun tasarımınıza bağlı

            cat2.score += 10;



            if (bg_1 != null) {

                bg_1.update();

            }



            if (obstacleManager != null) {

                obstacleManager.update();



                boolean cat1HitByObstacle = obstacleManager.checkCollision(cat1); // cat1'in engeli cat2'ye çarptı mı?

                boolean cat2HitByObstacle = obstacleManager.checkCollision(cat2); // cat2'nin engeli cat1'e çarptı mı?



// Engeller kedilere değil, diğer kediye zarar veriyor gibi görünüyor.

// Yani cat1'in fırlattığı engel cat2'ye, cat2'nin fırlattığı engel cat1'e.

                if (cat1HitByObstacle && currentTime - cat1LastHitTime > INVULNERABILITY_TIME) {

                    if (!cat2.isShield) cat2.catLives--; // cat1'in engeli cat2'ye hasar verdi

                    cat1LastHitTime = currentTime; // cat1'in son vuruş zamanı (aslında cat2'nin son vurulma zamanı olmalı)

// Bu değişkenlerin adlandırılması kafa karıştırıcı olabilir.

// cat2LastHitTime -> cat2'nin son darbe aldığı zaman

// cat1LastHitTime -> cat1'in son darbe aldığı zaman

                }



                if (cat2HitByObstacle && currentTime - cat2LastHitTime > INVULNERABILITY_TIME) {

                    if (!cat1.isShield) cat1.catLives--; // cat2'nin engeli cat1'e hasar verdi

                    cat2LastHitTime = currentTime;

                }



// Canları kontrol et ve oyunu bitir

                if (cat1.catLives <= 0 && !isGameOver) { // cat1 (sağdaki oyuncu) canı bitti

                    isGameOver = true;

// Kazanan cat2 (soldaki oyuncu)

                }

                if (cat2.catLives <= 0 && !isGameOver) { // cat2 (soldaki oyuncu) canı bitti

                    isGameOver = true;

// Kazanan cat1 (sağdaki oyuncu)

                }

            }

        }

    }



    @Override

    public void draw(Canvas canvas) {

        super.draw(canvas);

        if (canvas == null) return;



        Paint paint = new Paint();



// Arka Plan

        if (cat1.isGameStart) { // Oyunun başlangıç ekranı

            canvas.drawBitmap(bg_Image, 0, 0, null);

        } else { // Oyun sırasında kayan arka plan

            bg_1.drawScrollingBackgroundDual(canvas);

        }



// Kediler

        cat1.draw(canvas);

        cat2.draw(canvas);



        if (obstacleManager != null && !cat1.isGameStart) { // Engelleri oyun başladıktan sonra çiz

            obstacleManager.draw(canvas);

        }



// Can Göstergeleri ve Skorlar

        int heartDisplayX_Cat2 = canvas.getWidth() - 350; // cat2 (sağdaki) canları için X

        int heartDisplayY = 50; // Kalpler ve skorlar için Y konumu referansı



// cat1 (Sol oyuncu) canları

        if (cat1.catLives == 3) {

            canvas.drawBitmap(heart_3, 50, heartDisplayY, null);

        } else if (cat1.catLives == 2) {

            canvas.drawBitmap(heart_2, 50, heartDisplayY, null);

        } else if (cat1.catLives == 1){

            canvas.drawBitmap(heart_1, 50, heartDisplayY, null);

        }



// cat2 (Sağ oyuncu) canları

        if (cat2.catLives == 3) {

            canvas.drawBitmap(heart_3, heartDisplayX_Cat2, heartDisplayY, null);

        } else if (cat2.catLives == 2) {

            canvas.drawBitmap(heart_2, heartDisplayX_Cat2, heartDisplayY, null);

        } else if(cat2.catLives == 1) {

            canvas.drawBitmap(heart_1, heartDisplayX_Cat2, heartDisplayY, null);

        }



        paint.setColor(Color.WHITE);

        paint.setTextSize(40);

// Skorların pozisyonlarını canların altına ayarlayalım

        canvas.drawText("Score: " + cat2.score, 70, heartDisplayY + heart_1.getHeight() + 40, paint); // cat2 (sol) skoru

        canvas.drawText("Score: " + cat1.score, heartDisplayX_Cat2, heartDisplayY + heart_1.getHeight() + 40, paint); // cat1 (sağ) skoru





        if (isGameOver) {

// Oyun Sonu Ekranı

            Paint overlayPaint = new Paint();

            overlayPaint.setColor(Color.argb(180, 0, 0, 0)); // Yarı saydam siyah bir katman

            canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);



            Paint gameOverTextPaint = new Paint();

            gameOverTextPaint.setColor(Color.WHITE);

            gameOverTextPaint.setTextSize(100);

            gameOverTextPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2 - 150, gameOverTextPaint);



// Kazanan Yazısı

            Paint winnerPaint = new Paint();

            winnerPaint.setColor(Color.YELLOW);

            winnerPaint.setTextSize(120);

            winnerPaint.setTextAlign(Paint.Align.CENTER);



// cat1 sağdaki oyuncu, cat2 soldaki oyuncu.

            if (cat2.catLives <= 0) { // cat1 (sağdaki) kaybetti, cat2 (soldaki) KAZANDI

                canvas.drawText("WINNER!", canvas.getWidth() / 4, canvas.getHeight() / 3, winnerPaint);

            } else if (cat1.catLives <= 0) { // cat2 (soldaki) kaybetti, cat1 (sağdaki) KAZANDI

                canvas.drawText("WINNER!", canvas.getWidth() * 3 / 4, canvas.getHeight() / 3, winnerPaint);

            }



// Yeniden Başlatma Butonu/Yazısı

            if (restartButton != null) {

                canvas.drawBitmap(restartButton, restartButtonX, restartButtonY, null);

            } else {

                Paint restartTextPaint = new Paint();

                restartTextPaint.setColor(Color.GREEN);

                restartTextPaint.setTextSize(70);

                restartTextPaint.setTextAlign(Paint.Align.CENTER);

// Metni butonun ortasına gelecek şekilde ayarla

                canvas.drawText("RESTART", restartButtonX + (float)buttonWidth / 2, restartButtonY + (float)buttonHeight / 2 + 20, restartTextPaint);

            }



        } else if (isPaused) {

// Duraklatma Ekranı

            Paint pausePaint = new Paint();

            pausePaint.setColor(Color.WHITE);

            pausePaint.setTextSize(100);

            pausePaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("PAUSED", canvas.getWidth() / 2, resumeY - 100, pausePaint); // resumeY, resume butonunun Y'si

            canvas.drawBitmap(resumeButton, resumeX, resumeY, null);

        } else {

// Oyun devam ediyorsa ve duraklatılmadıysa Pause butonunu çiz

            canvas.drawBitmap(pauseButton, pauseButtonX, pauseButtonY, null);

        }

    }



    public void restartGame() {

        isGameOver = false;

        initializeGameEntities(); // Oyun varlıklarını ve durumlarını başlangıç değerlerine ayarla

    }



    @Override

    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();

        int y = (int) event.getY();



        if (isGameOver) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

// Yeniden başlatma butonuna dokunuldu mu kontrol et

                if (x >= restartButtonX && x <= restartButtonX + buttonWidth &&

                        y >= restartButtonY && y <= restartButtonY + buttonHeight) {

                    restartGame();

                    return true;

                }

            }

            return true; // Oyun sonu ekranında başka bir yere dokunulursa olayı tüket

        }



        if (isPaused) { // Sadece duraklatılmışsa resume butonunu kontrol et

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                if (x >= resumeX && x <= resumeX + buttonWidth &&

                        y >= resumeY && y <= resumeY + buttonHeight) {

                    togglePause();

                    return true;

                }

            }

            return true; // Duraklatma ekranında başka bir yere dokunulursa olayı tüket

        }



// Oyun duraklatılmadıysa ve bitmediyse Pause butonunu kontrol et (ACTION_DOWN için)

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (x >= pauseButtonX && x <= pauseButtonX + pauseButtonSize &&

                    y >= pauseButtonY && y <= pauseButtonY + pauseButtonSize) {

                togglePause();

                return true;

            }

        }



// Oyun duraklatılmadıysa ve bitmediyse oyun içi dokunmaları işle

// Bu blok `if (!isPaused && !isGameOver)` anlamına gelir, çünkü yukarıdaki kontrollerden geçti.

        boolean gestureHandled = gestureDetector.onTouchEvent(event); // Çift dokunma, tek dokunma (saldırı), uzun basma (kalkan)

        boolean catHandled = false; // Kediye özel zıplama gibi dokunmalar için



        int width = getWidth();



// Kalkanı bırakma (ACTION_UP)

// GestureDetector zaten ACTION_UP'ı kendi içinde işleyebilir,

// ama burada explicit olarak kalkanı bırakma mantığı var.

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (x < width / 2) { // Sol taraf (cat2)

                if(cat2 != null) cat2.isShield = false;

            } else { // Sağ taraf (cat1)

                if(cat1 != null) cat1.isShield = false;

            }

        }



// Kediye özel dokunma (zıplama vs.). Cat sınıfındaki handleTouch'a bağlı.

// Genellikle zıplama gibi şeyler ACTION_DOWN ile tetiklenir.

// GestureDetector onSingleTapConfirmed, onDoubleTap, onLongPress gibi olayları yakalar.

// Cat.handleTouch neyi işliyor? Eğer bu sadece zıplama ise ve GestureListener'da yoksa burada kalmalı.

// Mevcut kodda cat.handleTouch() çağrılıyor, bunun içeriği önemli.

// Eğer Cat.handleTouch() sadece GestureDetector'ın kapsamadığı spesifik dokunmaları (örn: swipe ile zıplama)

// işliyorsa, bu mantık kalabilir.

// Ancak, eğer GestureDetector zaten tüm gerekli dokunmaları (tek, çift, uzun basma) ele alıyorsa

// ve Cat.handleTouch da bunlardan birini (örn: tek dokunma ile zıplama) farklı şekilde ele alıyorsa

// çakışma olabilir veya gereksiz olabilir.

// Orijinal yapıyı koruyarak devam ediyorum:

        if (x > width / 2) { // Sağ taraf (cat1)

            if(cat1 != null) catHandled = cat1.handleTouch(event);

        } else { // Sol taraf (cat2)

            if(cat2 != null) catHandled = cat2.handleTouch(event);

        }



        return gestureHandled || catHandled;

    }



    @Override

    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;

        thread.setRunning(false);

        while (retry) {

            try {

                thread.join();

                retry = false;

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }



    @Override

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}



    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override

        public boolean onDoubleTap(MotionEvent e) {

            if (isGameOver || isPaused) return false; // Oyun bittiyse veya durakladıysa işlem yapma



            int x = (int) e.getX();

            int width = getWidth();

            if (cat1.isGameStart) { // Oyunun başlangıç aşamasıysa (kediler hareketsiz)

                cat1.handleDoubleTap(); // Muhtemelen cat1.isGameStart = false yapar

                cat2.handleDoubleTap(); // Muhtemelen cat2.isGameStart = false yapar (veya senkronize olur)

            } else { // Oyun zaten başladıysa, çift dokunma farklı bir işlev görebilir (veya hiç)

// Mevcut kodda oyun başladıktan sonraki çift dokunma için özel bir şey yok gibi duruyor.

// Ama karakterlere özel bir yetenek olabilir.

                if (x > width / 2) { // Sağ taraf (cat1)

                    cat1.handleDoubleTap(); // Cat sınıfında bu durum için bir mantık varsa

                } else { // Sol taraf (cat2)

                    cat2.handleDoubleTap(); // Cat sınıfında bu durum için bir mantık varsa

                }

            }

            return true;

        }



        @Override

        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (isGameOver || isPaused || cat1.isGameStart) return false; // Oyun bittiyse, durakladıysa veya başlangıçtaysa saldırı yapma



            int x = (int) e.getX();

            int width = getWidth();



// ThrowableObstacle.x ve ThrowableObstacle2.x statik mi yoksa instance değişkenleri mi?

// Eğer instance ise, obstacleManager üzerinden erişilmeli veya Cat içinde yönetilmeli.

// Kodda doğrudan sınıf adıyla erişilmiş, bu yüzden statik olduklarını varsayıyorum.

// Bu, problem yaratabilir eğer birden fazla engel varsa veya engeller dinamikse.

// Şimdilik orijinal mantığı koruyorum.

            if (x > width / 2) { // Sağ taraf (cat1 saldırıyor)

                cat1.attackCount = 0;

                cat1.isAttack = true;

// Engel fırlatma mantığı. Bu engel koordinatları (ThrowableObstacle.x) güncel olmalı.

                if (obstacleManager != null /*&& (cat1.CatX) - (ThrowableObstacle.x) <= 300 */) { // Mesafe kontrolü yoruma alındı, çünkü ThrowableObstacle.x'in kaynağı belirsiz

                    obstacleManager.throwFrontObstacle(cat1, cat2); // cat1 fırlatır, hedef cat2

                }

            } else { // Sol taraf (cat2 saldırıyor)

                cat2.attackCount = 0;

                cat2.isAttack = true;

                if (obstacleManager != null /*&& (ThrowableObstacle2.x) - (cat2.CatX) <= 300 */) { // Mesafe kontrolü yoruma alındı

                    obstacleManager.throwFrontObstacle(cat2, cat1); // cat2 fırlatır, hedef cat1

                }

            }

            return true;

        }



        @Override

        public void onLongPress(MotionEvent e) {

            if (isGameOver || isPaused || cat1.isGameStart) return; // Oyun bittiyse, durakladıysa veya başlangıçtaysa kalkan açma



            int x = (int) e.getX();

            int screenWidth = getWidth();



            if (x < screenWidth / 2) { // Sol taraf (cat2)

                cat2.isShield = true;

            } else { // Sağ taraf (cat1)

                cat1.isShield = true;

            }

        }

    }

}