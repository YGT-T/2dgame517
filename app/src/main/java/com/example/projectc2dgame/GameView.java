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
    // Dokunulmazlık zamanı değişkenleri kaldırıldı: cat1LastHitTime, cat2LastHitTime, INVULNERABILITY_TIME
    // private long cat1LastHitTime = 0, cat2LastHitTime = 0;
    // private static final long INVULNERABILITY_TIME = 200; // Bu satır kaldırıldı

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
        try {
            restartButton = BitmapFactory.decodeResource(getResources(), R.drawable.restart_button);
            restartButton = Bitmap.createScaledBitmap(restartButton, buttonWidth, buttonHeight, false);
        } catch (Exception e) {
            restartButton = null; // Resim bulunamazsa null ata, çizimde yazı kullanırız
            System.err.println("Yeniden başlatma butonu resmi yüklenemedi: " + e.getMessage() + ". Metin olarak çizilecek.");
        }
        restartButtonX = (screenWidth - buttonWidth) / 2;
        restartButtonY = resumeY + buttonHeight + buttonSpacing; // Resume butonunun altına yerleştir

        initializeGameEntities(); // Oyun varlıklarını başlatan metod

        if (thread.getState() == Thread.State.NEW) {
            thread.setRunning(true);
            thread.start();
        } else {
            thread = new GameThread(getHolder(), this);
            thread.setRunning(true);
            thread.start();
        }
    }

    private void initializeGameEntities() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        // cat1 (Sağ oyuncu)
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
        // Dokunulmazlık zamanı sıfırlamaları kaldırıldı
        // cat1LastHitTime = 0;
        // cat2LastHitTime = 0;
        isPaused = false;
        isGameOver = false;
    }

    public void update() {
        if (isPaused || isGameOver) return; // Oyun duraklatılmışsa veya bitmişse güncelleme yapma

        currentTime = System.currentTimeMillis();
        cat1.update(currentTime);
        cat2.update(currentTime);

        if (!cat1.isGameStart) {
            cat1.score += 10;
            cat2.score += 10;

            if (bg_1 != null) {
                bg_1.update();
            }

            if (obstacleManager != null) {
                obstacleManager.update();

                boolean cat1HitByObstacle = obstacleManager.checkCollision(cat1);
                boolean cat2HitByObstacle = obstacleManager.checkCollision(cat2);

                // Dokunulmazlık kontrolü kaldırıldı, direkt can azaltma
                if (cat1HitByObstacle) {
                    if (!cat2.isShield) cat2.catLives--; // cat1'in engeli cat2'ye hasar verdi
                }

                if (cat2HitByObstacle) {
                    if (!cat1.isShield) cat1.catLives--; // cat2'nin engeli cat1'e hasar verdi
                }

                // Canları kontrol et ve oyunu bitir
                if (cat1.catLives <= 0 && !isGameOver) {
                    isGameOver = true;
                }
                if (cat2.catLives <= 0 && !isGameOver) {
                    isGameOver = true;
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
        if (cat1.isGameStart) {
            canvas.drawBitmap(bg_Image, 0, 0, null);
        } else {
            bg_1.drawScrollingBackgroundDual(canvas);
        }

        // Kediler
        cat1.draw(canvas);
        cat2.draw(canvas);

        if (obstacleManager != null && !cat1.isGameStart) {
            obstacleManager.draw(canvas);
        }

        // Can Göstergeleri ve Skorlar
        int heartDisplayX_Cat2 = canvas.getWidth() - 350;
        int heartDisplayY = 50;

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
        canvas.drawText("Score: " + cat2.score, 70, heartDisplayY + heart_1.getHeight() + 40, paint);
        canvas.drawText("Score: " + cat1.score, heartDisplayX_Cat2, heartDisplayY + heart_1.getHeight() + 40, paint);

        if (isGameOver) {
            Paint overlayPaint = new Paint();
            overlayPaint.setColor(Color.argb(180, 0, 0, 0));
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

            Paint gameOverTextPaint = new Paint();
            gameOverTextPaint.setColor(Color.WHITE);
            gameOverTextPaint.setTextSize(100);
            gameOverTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2 - 150, gameOverTextPaint);

            Paint winnerPaint = new Paint();
            winnerPaint.setColor(Color.YELLOW);
            winnerPaint.setTextSize(120);
            winnerPaint.setTextAlign(Paint.Align.CENTER);

            if (cat2.catLives <= 0) {
                canvas.drawText("WINNER!", canvas.getWidth() / 4, canvas.getHeight() / 3, winnerPaint);
            } else if (cat1.catLives <= 0) {
                canvas.drawText("WINNER!", canvas.getWidth() * 3 / 4, canvas.getHeight() / 3, winnerPaint);
            }

            if (restartButton != null) {
                canvas.drawBitmap(restartButton, restartButtonX, restartButtonY, null);
            } else {
                Paint restartTextPaint = new Paint();
                restartTextPaint.setColor(Color.GREEN);
                restartTextPaint.setTextSize(70);
                restartTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("RESTART", restartButtonX + (float)buttonWidth / 2, restartButtonY + (float)buttonHeight / 2 + 20, restartTextPaint);
            }

        } else if (isPaused) {
            Paint pausePaint = new Paint();
            pausePaint.setColor(Color.WHITE);
            pausePaint.setTextSize(100);
            pausePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("PAUSED", canvas.getWidth() / 2, resumeY - 100, pausePaint);
            canvas.drawBitmap(resumeButton, resumeX, resumeY, null);
        } else {
            canvas.drawBitmap(pauseButton, pauseButtonX, pauseButtonY, null);
        }
    }

    public void restartGame() {
        isGameOver = false;
        initializeGameEntities();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isGameOver) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (x >= restartButtonX && x <= restartButtonX + buttonWidth &&
                        y >= restartButtonY && y <= restartButtonY + buttonHeight) {
                    restartGame();
                    return true;
                }
            }
            return true;
        }

        if (isPaused) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (x >= resumeX && x <= resumeX + buttonWidth &&
                        y >= resumeY && y <= resumeY + buttonHeight) {
                    togglePause();
                    return true;
                }
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (x >= pauseButtonX && x <= pauseButtonX + pauseButtonSize &&
                    y >= pauseButtonY && y <= pauseButtonY + pauseButtonSize) {
                togglePause();
                return true;
            }
        }

        boolean gestureHandled = gestureDetector.onTouchEvent(event);
        boolean catHandled = false;

        int width = getWidth();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (x < width / 2) {
                if(cat2 != null) cat2.isShield = false;
            } else {
                if(cat1 != null) cat1.isShield = false;
            }
        }

        if (x > width / 2) {
            if(cat1 != null) catHandled = cat1.handleTouch(event);
        } else {
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
            if (isGameOver || isPaused) return false;

            int x = (int) e.getX();
            int width = getWidth();
            if (cat1.isGameStart) {
                cat1.handleDoubleTap();
                cat2.handleDoubleTap();
            } else {
                if (x > width / 2) {
                    cat1.handleDoubleTap();
                } else {
                    cat2.handleDoubleTap();
                }
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isGameOver || isPaused || cat1.isGameStart) return false;

            int x = (int) e.getX();
            int width = getWidth();

            if (x > width / 2) {
                cat1.attackCount = 0;
                cat1.isAttack = true;
                if (obstacleManager != null) {
                    obstacleManager.throwFrontObstacle(cat1, cat2);
                }
            } else {
                cat2.attackCount = 0;
                cat2.isAttack = true;
                if (obstacleManager != null) {
                    obstacleManager.throwFrontObstacle(cat2, cat1);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isGameOver || isPaused || cat1.isGameStart) return;

            int x = (int) e.getX();
            int screenWidth = getWidth();

            if (x < screenWidth / 2) {
                cat2.isShield = true;
            } else {
                cat1.isShield = true;
            }
        }
    }
}