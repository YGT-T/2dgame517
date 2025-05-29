package com.example.projectc2dgame;



import android.graphics.Bitmap;

import android.graphics.Canvas;



public class Background {



    private int bgXLeft; // Arka planın sol kopyasının X eksenindeki başlangıç pozisyonu

    private int bgXRight; // Arka planın sağ kopyasının X eksenindeki başlangıç pozisyonu

    private Bitmap bgImage;

    private int bgScrollSpeed = 25;







    public Background(Bitmap bgImage, int bgXLeft, int bgXRight, int bgScrollSpeed) {

        this.bgImage = bgImage;

        this.bgXLeft = bgXLeft;

        this.bgXRight = bgXRight;

        this.bgScrollSpeed = bgScrollSpeed;

    }






//arka planın ekrandaki konumu günceller
    public void update() {

        bgXLeft -= bgScrollSpeed;


        //eğer arkaplan ekranın dışına taştıysa konumunu sıfırlar
        if (bgXLeft <= -bgImage.getWidth()) {

            bgXLeft += bgImage.getWidth();

        }



        bgXRight += bgScrollSpeed;



        if (bgXRight >= bgImage.getWidth()) {

            bgXRight -= bgImage.getWidth();

        }

    }




        //güncellenen değerlere göre arkaplanı ekrana çizer
    public void drawScrollingBackgroundDual(Canvas canvas) {

        int bgWidth = bgImage.getWidth();

        int canvasWidth = canvas.getWidth(); // Çizim yapılacak alanın (Canvas) genişliğini alır.

        int canvasHeight = canvas.getHeight(); // Çizim yapılacak alanın (Canvas) yüksekliğini alır.

        int halfWidth = canvasWidth / 2; // Ekran genişliğinin yarısını hesaplar.



// SOL TARAFIN ÇİZİMİ

        canvas.save();

        canvas.clipRect(0, 0, halfWidth, canvasHeight); // Çizim yapılacak alanı ekranın sol yarısıyla sınırlar

        canvas.drawBitmap(bgImage, bgXLeft, 0, null);

        canvas.drawBitmap(bgImage, bgXLeft + bgWidth, 0, null);

        canvas.restore(); // Canvası bir önceki kaydedilmiş durumuna geri yükler



// SAĞ TARAFIN ÇİZİMİ

        canvas.save(); // Canvas'ın mevcut durumunu tekrar kaydeder.

        canvas.clipRect(halfWidth, 0, canvasWidth, canvasHeight);

        canvas.drawBitmap(bgImage, bgXRight - bgWidth, 0, null);

        canvas.drawBitmap(bgImage, bgXRight, 0, null);

        canvas.restore();

    }

}