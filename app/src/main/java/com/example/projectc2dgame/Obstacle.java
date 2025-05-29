package com.example.projectc2dgame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;



public class Obstacle {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 25;

    public Obstacle(Context context, int startY) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);


        x = (Resources.getSystem().getDisplayMetrics().widthPixels) / 2;


        y = startY;
    }

    // Engelin sağa doğru hareket etmesi için x+
    public void update() {
        x += speed;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);

    }


    public int getX() {
        return x;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    //Hitbox için
    public Rect getRect() {
        int padding = 10;
        return new Rect(
                x + padding,
                y + padding,
                x + bitmap.getWidth() - padding,
                y + bitmap.getHeight() - padding
        );
    }

    //Kedi ile çarpışma anı için
    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}