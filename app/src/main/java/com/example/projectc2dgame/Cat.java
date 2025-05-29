package com.example.projectc2dgame;



import android.graphics.Bitmap;

import android.graphics.Canvas;

import android.graphics.Rect;

import android.view.MotionEvent;



public class Cat {




//karakterin konumu
    public int CatX;

    public int CatY;




//zıplama yol değiştirme
    public int jumpHeight;

    public int jumpSpeed;

    public int jumpProgress = 0; // Zıplama ilerlemesi

    public boolean isJumping = false;

    public boolean isFalling = false;

    public boolean isJumpingThrough = false;

    public int jumpThroughProgress = 0; // zıplama ilerlemesi



//animasyon resimleri

    public Bitmap runSpriteSheet;

    public Bitmap jumpSpriteSheet;

    public Bitmap sleepSpriteSheet;

    public Bitmap scareSpriteSheet;

    public Bitmap attackSpriteSheet;

    public Bitmap hurtSpriteSheet;

    public Bitmap deadSpriteSheet;

    public Bitmap shieldSpriteSheet;




//resim boyutları
    public int runFrameWidth, runFrameHeight;

    public int jumpFrameWidth, jumpFrameHeight;




// update ve draw için her kare için değişimler
    public int frameDelay;

    public int currentRunFrame = 0; // Mevcut animasyon karesi

    //resmin kaç parçaya bölüneceği akıcı animasyon için

    public int runFrameCount;

    public int jumpFrameCount;

    public int startingFrameCount;

    public int idleFrameCount;

    public int attackFrameCount;

    public int hurtFrameCount;

    public int deadFrameCount;

    public int shieldFrameCount;



    public long lastFrameChangeTime = 0; // Son kare değişim zamanı

    private float touchStartY = 0; // Dokunma başlangıç Y konumu

    public int location = 0; // Kedinin mantıksal dikey konumu (-1 alt, 0 orta, 1 üst)

    public boolean isReversed; // Animasyon ters mi sağ ve sola aynı animasyonu koyabilmek için
//hangi animasyonun kullanılcağını seçer
    public boolean isGameStart;

    public boolean isScare;

    public boolean isAttack = false;

    public boolean isDead = false;

    public boolean isHurt = false;

    public boolean isShield=false;




//animasyonun 1 kere çalışıp durması için
    public int scareCount;

    public int attackCount = 0;

    public int shieldCount=0;



// Oyun istatistikleri

    public int catLives;

    public int score;





    public Cat(int screenWidth, int screenHeight,

               Bitmap runSpriteSheet, Bitmap jumpSpriteSheet, Bitmap sleepSpriteSheet,

               Bitmap scareSpriteSheet, Bitmap attackSpriteSheet, Bitmap hurtSpriteSheet, Bitmap deadSpriteSheet,Bitmap shieldSpriteSheet,

               int catLives, int score, int runFrameCount, int jumpFrameCount,

               int startingFrameCount, int idleFrameCount, int attackFrameCount, int hurtFrameCount, int deadFrameCount,int shieldFrameCount,

               int jumpSpeed, int jumpHeight, int frameDelay, boolean isReversed,

               boolean isGameStart, boolean isScare, boolean isRightSide) {



// Sprite sheet atamaları

        this.runSpriteSheet = runSpriteSheet;

        this.jumpSpriteSheet = jumpSpriteSheet;

        this.sleepSpriteSheet = sleepSpriteSheet;

        this.scareSpriteSheet = scareSpriteSheet;

        this.attackSpriteSheet = attackSpriteSheet;

        this.hurtSpriteSheet = hurtSpriteSheet;

        this.deadSpriteSheet = deadSpriteSheet;

        this.shieldSpriteSheet=shieldSpriteSheet;



// Değer atamaları

        this.catLives = catLives;

        this.runFrameCount = runFrameCount;

        this.jumpFrameCount = jumpFrameCount;

        this.idleFrameCount = idleFrameCount;

        this.attackFrameCount = attackFrameCount;

        this.hurtFrameCount = hurtFrameCount;

        this.deadFrameCount = deadFrameCount;

        this.startingFrameCount=startingFrameCount;







        this.runFrameWidth = (runSpriteSheet.getWidth() / runFrameCount);

        this.runFrameHeight = runSpriteSheet.getHeight();

        this.jumpFrameWidth = jumpSpriteSheet.getWidth() / jumpFrameCount;

        this.jumpFrameHeight = jumpSpriteSheet.getHeight();





        this.jumpSpeed = jumpSpeed;

        this.jumpHeight = jumpHeight;

        this.frameDelay = frameDelay;

        this.isReversed = isReversed;

        this.isGameStart = isGameStart;

        this.isScare = isScare;

        this.score = score;





        int catWidth = runFrameWidth; // Kedi genişliği

        int catHeight = runFrameHeight; // Kedi yüksekliği

        this.CatY = (screenHeight - catHeight) /2; // Dikeyde ortala

        this.CatX = isRightSide ? (screenWidth - catWidth) : 0; // Yatayda sağ veya sol kenara ata

    }





    public void update(long currentTime) {



        if (isGameStart) {

            updateFrame(currentTime, idleFrameCount);

        } else if (isScare) {



        } else if (isShield) {

            updateFrame(currentTime, 4);

        } else if (isHurt) {

            updateFrame(currentTime, hurtFrameCount);

        } else if (catLives==123) {

            updateFrame(currentTime, deadFrameCount);

        } else if (isAttack) {

            updateFrame(currentTime, attackFrameCount);

        } else if (isJumping || isFalling || isJumpingThrough) {

            updateFrame(currentTime, jumpFrameCount);

        } else {

            updateFrame(currentTime, runFrameCount);

        }



// Zıplama/Düşme hareketleri

        if (isJumpingThrough) { // Özel zıplama (içinden geçme)

            if (jumpThroughProgress < jumpHeight) { // Yukarı çıkış

                CatY -= jumpSpeed;

                jumpThroughProgress += jumpSpeed;

            } else if (jumpThroughProgress < jumpHeight * 2) { // Aşağı iniş

                CatY += jumpSpeed;

                jumpThroughProgress += jumpSpeed;

            } else { // Zıplama bitti

                isJumpingThrough = false;

                jumpThroughProgress = 0;

            }

        }



        if (isJumping) {

            if (jumpProgress < jumpHeight) {

                CatY -= jumpSpeed;

                jumpProgress += jumpSpeed;

            } else {

                isJumping = false;

                jumpProgress = 0;

                location++;

            }

        } else if (isFalling) {

            if (jumpProgress < jumpHeight) {

                CatY += jumpSpeed;

                jumpProgress += jumpSpeed;

            } else {

                isFalling = false;

                jumpProgress = 0;

                location--;

            }

        }

    }



// Animasyon karesini zamanlamaya göre günceller

    private void updateFrame(long currentTime, int frameCount) {

        int delay = frameDelay;



        if (isScare || isAttack || isHurt || isDead) {

            delay = 80;

        } else if (isJumping || isFalling || isJumpingThrough) {

            delay = 100;

        } else if (isGameStart) {

            delay = 20;

        } else if(catLives==0){

            delay=100;

        }



        if (currentTime > lastFrameChangeTime + delay) { // Gecikme süresi dolduysa

            if (isReversed) {

                currentRunFrame = (currentRunFrame + 1) % frameCount;

            } else {

                currentRunFrame = (currentRunFrame - 1 + frameCount) % frameCount;

            }

            lastFrameChangeTime = currentTime;

        }

    }



// karakteri update değerlerine göre ekrana çizer

    public void draw(Canvas canvas) {

// Sprite sheetten hangi karenin alınacağı

        Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);

// Hedef Ekranda nereye çizileceği

        Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth, CatY + runFrameHeight);




// hangi animasyon çizilcek
        if (isGameStart) {

            canvas.drawBitmap(sleepSpriteSheet, src, dst, null);

        } else if (isScare) {

            canvas.drawBitmap(scareSpriteSheet, src, dst, null);

            if (++scareCount == 8) isScare = false;

        } else if (isShield) {

            canvas.drawBitmap(shieldSpriteSheet, src, dst, null);

        } else if (isHurt) {

            canvas.drawBitmap(hurtSpriteSheet, src, dst, null);

        } else if (catLives==123) { // Özel ölüm

            canvas.drawBitmap(deadSpriteSheet, src, dst, null);

        } else if (isAttack) {

            canvas.drawBitmap(attackSpriteSheet, src, dst, null);

            if (++attackCount == 10) isAttack = false;

        } else if (isJumping || isFalling || isJumpingThrough) {

            canvas.drawBitmap(jumpSpriteSheet, src, dst, null);

        } else {

            canvas.drawBitmap(runSpriteSheet, src, dst, null);

        }

    }



// Dokunmatik ekran girdileri

    public boolean handleTouch(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: // Ekrana dokunulduğunda

                touchStartY = event.getY(); // Başlangıç Y'sini kaydet

                return true;

            case MotionEvent.ACTION_UP: // Parmak kaldırıldığında

                float endY = event.getY(); // Bitiş Y'sini al

// Yukarı kaydırma

                if (touchStartY - endY > 100 && !isJumping && (location == 0 || location == -1) && !isGameStart) {

                    isJumping = true;

                    return true;

                }

// Aşağı kaydırma

                else if (endY - touchStartY > 100 && !isFalling && (location == 0 || location == 1) && !isGameStart) {

                    isFalling = true;

                    return true;

                }

                return false;

        }

        return false;

    }



// Çift tıklama

    public void handleDoubleTap() {

        if (isGameStart) {

            isGameStart = false;

            isScare = true;

            scareCount = 0;

        } else {

            if (!isJumpingThrough && !isJumping && !isFalling) {

                isJumpingThrough = true;

                jumpThroughProgress = 0;

            }

        }

    }



// Çarpışma tespiti için hitbox

    public Rect getRect() {

// Hitbox için içten boşluk padding ve dikey kaydırma offset

        int paddingX = runFrameWidth / 2; // X ekseninde çok fazla padding, hitbox'ı daraltır.

        int paddingY = (runFrameHeight / 5) + 5;

        int offsetY = 70; // Hitbox'ı dikeyde aşağı kaydırır



// Hitbox koordinatları

        int top = CatY + paddingY + offsetY;

        int bottom = top + (runFrameHeight - 2 * paddingY); // Yükseklik korunur

        return new Rect(

                CatX + paddingX,

                top,

                CatX + runFrameWidth - paddingX,

                bottom

        );

    }





    public int getHeight() {

        return runFrameHeight;

    }



    public int getWidth() {

        return runFrameWidth;

    }



    public int getX() {

        return CatX;

    }



    public int getY() {

        return CatY;

    }

}