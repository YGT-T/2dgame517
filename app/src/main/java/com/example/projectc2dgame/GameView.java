package com.example.projectc2dgame;

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
    private int buttonWidth = 208;
    private int buttonHeight = 64;
    private int buttonSpacing = 86;
    private int resumeX, resumeY;

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

        cat1 = new Cat(getWidth(), getHeight(),

                Sections.characterRunMirrorSprite,

                Sections.characterJumpMirrorSprite,

                Sections.characterIdleMirrorSprite,

                BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_starting_mirror_sprite),

                Sections.characterAttackMirrorSprite,

                Sections.characterHurtMirrorSprite,

                Sections.characterDeadMirrorSprite,

                Sections.characterShieldMirrorSprite,

                3, 0,

                8, 12, 6, 6,

                5,2 ,4,4,

                40, 280, 40,

                false, true, false, true);



        cat2 = new Cat(getWidth(), getHeight(),

                Sections.characterRunSprite,

                Sections.characterJumpSprite,

                Sections.characterIdleSprite,

                BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_starting_sprite),

                Sections.characterAttackSprite,

                Sections.characterHurtSprite,

                Sections.characterDeadSprite,

                Sections.characterShieldSprite,

                3, 0,

                8, 12, 6,

                6,5,2,4,4,

                40, 280, 30,

                true, true, false, false);

        bg_1 = new Background(bg_Image, 0, 0, 15);

        bg_1 = new Background(bg_Image, 0, 0, 15);

        pauseButtonSize = 100;
        pauseButton = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);
        pauseButton = Bitmap.createScaledBitmap(pauseButton, pauseButtonSize, pauseButtonSize, false);

        pauseButtonX = (getWidth() - pauseButtonSize) / 2;
        pauseButtonY = 30;

        resumeButton = BitmapFactory.decodeResource(getResources(), R.drawable.resume_button);
        resumeButton = Bitmap.createScaledBitmap(resumeButton, buttonWidth, buttonHeight, false);

        resumeX = (screenWidth - buttonWidth) / 2;
        resumeY = (screenHeight - buttonHeight) / 2;

        obstacleManager = new ObstacleManager(getContext(), screenHeight, screenWidth);

        thread.setRunning(true);
        thread.start();
    }

    public void update() {
        if (isPaused) return;

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

                boolean cat1Hit = obstacleManager.checkCollision(cat1);
                boolean cat2Hit = obstacleManager.checkCollision(cat2);

                if (cat1Hit && currentTime - cat1LastHitTime > INVULNERABILITY_TIME) {
                    if (!cat2.isShield) cat2.catLives--;
                    cat1LastHitTime = currentTime;
                }

                if (cat2Hit && currentTime - cat2LastHitTime > INVULNERABILITY_TIME) {
                    if (!cat1.isShield) cat1.catLives--;
                    cat2LastHitTime = currentTime;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        Paint paint = new Paint();

        if (cat1.isGameStart) {
            canvas.drawBitmap(bg_Image, 0, 0, null);
        } else {
            bg_1.drawScrollingBackgroundDual(canvas);
        }

        cat1.draw(canvas);
        cat2.draw(canvas);

        if (obstacleManager != null) {
            obstacleManager.draw(canvas);
        }

        int heartX = canvas.getWidth() - 350;
        int heartY = 30;

        if (cat1.catLives == 3) {
            canvas.drawBitmap(heart_3, 50, 50, null);
        } else if (cat1.catLives == 2) {
            canvas.drawBitmap(heart_2, 50, 50, null);
        } else {
            canvas.drawBitmap(heart_1, 50, 50, null);
        }

        if (cat2.catLives == 3) {
            canvas.drawBitmap(heart_3, heartX, heartY, null);
        } else if (cat2.catLives == 2) {
            canvas.drawBitmap(heart_2, heartX, heartY, null);
        } else {
            canvas.drawBitmap(heart_1, heartX, heartY, null);
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("Score: " + cat1.score, heartX, heartY + 110, paint);
        canvas.drawText("Score: " + cat2.score, 70, heartY + 120, paint);


        if (!isPaused) {
            canvas.drawBitmap(pauseButton, pauseButtonX, pauseButtonY, null);
        }

        if (isPaused) {
            Paint pausePaint = new Paint();
            pausePaint.setColor(Color.WHITE);
            pausePaint.setTextSize(100);
            pausePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("PAUSED", canvas.getWidth() / 2, resumeY - 100, pausePaint);

            canvas.drawBitmap(resumeButton, resumeX, resumeY, null);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();


        if (isPaused && event.getAction() == MotionEvent.ACTION_DOWN) {

            if (x >= resumeX && x <= resumeX + buttonWidth &&
                    y >= resumeY && y <= resumeY + buttonHeight) {
                togglePause();
                return true;
            }

        }

        if (!isPaused && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (x >= pauseButtonX && x <= pauseButtonX + pauseButtonSize &&
                    y >= pauseButtonY && y <= pauseButtonY + pauseButtonSize) {
                togglePause();
                return true;
            }
        }


        if (!isPaused) {
            boolean gestureHandled = gestureDetector.onTouchEvent(event);
            boolean catHandled = false;

            int width = getWidth();


            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (x < width / 2) {
                    cat2.isShield = false;
                } else {
                    cat1.isShield = false;
                }
            }


            if (x > width / 2) {
                catHandled = cat1.handleTouch(event);
            } else {
                catHandled = cat2.handleTouch(event);
            }


            return gestureHandled || catHandled;
        }


        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
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
            int x = (int) e.getX();
            int width = getWidth();

            if (x > width / 2) {
                cat1.attackCount = 0;
                cat1.isAttack = true;
                if ((cat1.CatX) - (ThrowableObstacle.x) <= 300) {
                    obstacleManager.throwFrontObstacle(cat1, cat2);
                }
            } else {
                cat2.attackCount = 0;
                cat2.isAttack = true;
                if ((ThrowableObstacle2.x) - (cat2.CatX) <= 300) {
                    obstacleManager.throwFrontObstacle(cat2, cat1);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
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