package com.example.projectc2dgame; // Paket adı



import android.graphics.Canvas;

import android.view.SurfaceHolder;



public class GameThread extends Thread {

    private SurfaceHolder surfaceHolder;

    private GameView gameView;

    private boolean running; // Oyun döngüsünün çalışıp çalışmadığını kontrol eder







    public GameThread(SurfaceHolder holder, GameView view) {

        super(); // Üst sınıf olan Thread'in yapıcı metodunu çağırır

        surfaceHolder = holder; // SurfaceHolder'ı atar

        gameView = view; // GameView'ı atar

    }









    public void setRunning(boolean isRunning) {

        running = isRunning;

    }



    @Override

    public void run() { // Thread başlatıldığında bu metot çalışır

        while (running) { // running true olduğu sürece döngü devam eder

            Canvas canvas = null;



            try {

                canvas = surfaceHolder.lockCanvas();

                synchronized (surfaceHolder) {



                    if (gameView != null) {

                        gameView.update();

                        if (canvas != null) {

                            gameView.draw(canvas);

                        }

                    }

                }

            } finally { // Hata olsa da olmasa da bu blok çalışır

                if (canvas != null) { // Canvas null değilse (yani lockCanvas başarılı olduysa)

                    surfaceHolder.unlockCanvasAndPost(canvas); // Canvas'ı serbest bırak ve çizilenleri ekranda göster

                }

            }



            try {

// Döngüyü kısa bir süre beklet FPS kontrolü için

                sleep(16); // Yaklaşık 62.5 FPS (1000ms / 16ms). Oyunun saniyedeki kare hız

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

}