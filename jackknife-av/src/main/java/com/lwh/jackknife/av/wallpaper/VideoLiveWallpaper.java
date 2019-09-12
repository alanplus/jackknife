package com.lwh.jackknife.av.wallpaper;

import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;

public abstract class VideoLiveWallpaper extends WallpaperService {

    // 实现WallpaperService必须实现的抽象方法  
    public Engine onCreateEngine() {
        // 返回自定义的CameraEngine
        return new VideoEngine();
    }

    public abstract String getVideoFilePath();

    class VideoEngine extends Engine {

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    play(getSurfaceHolder().getSurface(), getVideoFilePath());

                }
            }, 500);

            // 设置处理触摸事件  
            setTouchEventsEnabled(true);

        }



        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {

            } else {

            }
        }
    }

    public native int play(Surface surface, String path);
}