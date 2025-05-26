package com.example.projectc2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private int bgXLeft;
    private int bgXRight;
    private Bitmap bgImage;
    private int bgScrollSpeed = 25;

    public Background(Bitmap bgImage, int bgXLeft, int bgXRight, int bgScrollSpeed) {
        this.bgImage = bgImage;
        this.bgXLeft = bgXLeft;
        this.bgXRight = bgXRight;
        this.bgScrollSpeed = bgScrollSpeed;
    }

    // Arka plan konumunu güncelle
    public void update() {
        bgXLeft -= bgScrollSpeed;
        if (bgXLeft <= -bgImage.getWidth()) {
            bgXLeft += bgImage.getWidth();
        }

        bgXRight += bgScrollSpeed;
        if (bgXRight >= bgImage.getWidth()) {
            bgXRight -= bgImage.getWidth();
        }
    }

    // Ekrana çiz
    public void drawScrollingBackgroundDual(Canvas canvas) {
        int bgWidth = bgImage.getWidth();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int halfWidth = canvasWidth / 2;

        // SOL TARAF
        canvas.save();
        canvas.clipRect(0, 0, halfWidth, canvasHeight);
        canvas.drawBitmap(bgImage, bgXLeft, 0, null);
        canvas.drawBitmap(bgImage, bgXLeft + bgWidth, 0, null);
        canvas.restore();

        // SAĞ TARAF
        canvas.save();
        canvas.clipRect(halfWidth, 0, canvasWidth, canvasHeight);
        canvas.drawBitmap(bgImage, bgXRight - bgWidth, 0, null);
        canvas.drawBitmap(bgImage, bgXRight, 0, null);
        canvas.restore();
    }
}