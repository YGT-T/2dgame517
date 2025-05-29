package com.example.projectc2dgame;


import android.graphics.Canvas;

import android.view.SurfaceHolder;


public class GameThread extends Thread {

    private SurfaceHolder surfaceHolder;

    private GameView gameView;

    private boolean running;


    public GameThread(SurfaceHolder holder, GameView view) {

        super();

        surfaceHolder = holder;

        gameView = view;

    }


    public void setRunning(boolean isRunning) {

        running = isRunning;

    }


    @Override

    public void run() {

        while (running) {

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

            } finally {

                if (canvas != null) {

                    surfaceHolder.unlockCanvasAndPost(canvas);

                }

            }


            try {


                sleep(16); // 60 FPS için

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

}