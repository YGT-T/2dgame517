package com.example.projectc2dgame;



import android.graphics.Bitmap;

import android.graphics.Canvas;



public class Background {



    private int bgXLeft; // Arka planın sol kopyasının X eksenindeki başlangıç pozisyonu

    private int bgXRight; // Arka planın sağ kopyasının X eksenindeki başlangıç pozisyonu

    private Bitmap bgImage;

    private int bgScrollSpeed = 25;







    public Background(Bitmap bgImage, int bgXLeft, int bgXRight, int bgScrollSpeed) {

        this.bgImage = bgImage; // Parametre olarak gelen bgImage değerini, bu sınıfa ait bgImage değişkenine atar. 'this' anahtar kelimesi, sınıfın kendi değişkenlerini işaret eder.

        this.bgXLeft = bgXLeft; // Parametre olarak gelen bgXLeft değerini, bu sınıfa ait bgXLeft değişkenine atar.

        this.bgXRight = bgXRight; // Parametre olarak gelen bgXRight değerini, bu sınıfa ait bgXRight değişkenine atar.

        this.bgScrollSpeed = bgScrollSpeed; // Parametre olarak gelen bgScrollSpeed değerini, bu sınıfa ait bgScrollSpeed değişkenine atar.

    }







    public void update() {

        bgXLeft -= bgScrollSpeed;



        if (bgXLeft <= -bgImage.getWidth()) { // Eğer sol arka planın sol kenarı, ekranın solundan resim genişliği kadar dışarı taştıysa...

            bgXLeft += bgImage.getWidth(); // ...sol arka planı, bir resim genişliği kadar sağa kaydırarak tekrar görünür alana getir.

        }



        bgXRight += bgScrollSpeed;



        if (bgXRight >= bgImage.getWidth()) {

            bgXRight -= bgImage.getWidth();

        }

    }





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

        canvas.restore(); // Canvas'ı bir önceki kaydedilmiş durumuna geri yükler. Yani clipRect ile yapılan sınırlama kaldırılır.



// SAĞ TARAFIN ÇİZİMİ

        canvas.save(); // Canvas'ın mevcut durumunu tekrar kaydeder.

        canvas.clipRect(halfWidth, 0, canvasWidth, canvasHeight); // Çizim yapılacak alanı ekranın sağ yarısıyla sınırlar.

        canvas.drawBitmap(bgImage, bgXRight - bgWidth, 0, null);

        canvas.drawBitmap(bgImage, bgXRight, 0, null);

        canvas.restore();

    }

}