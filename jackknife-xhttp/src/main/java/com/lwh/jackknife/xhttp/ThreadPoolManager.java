package com.lwh.jackknife.xhttp;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private static ThreadPoolManager sInstance = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return sInstance;
    }

    private ThreadPoolExecutor mThreadPoolExecutor;
    private LinkedBlockingQueue<Future<?>> mService = new LinkedBlockingQueue<>();

    private ThreadPoolManager() {
        mThreadPoolExecutor=new ThreadPoolExecutor(4,10,10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4),handler);
        mThreadPoolExecutor.execute(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                FutureTask futureTask = null;
                try {
                    futureTask = (FutureTask) mService.take();
                } catch (Exception e) {
                if (futureTask != null) {
                    e.printStackTrace();
                }
                    mThreadPoolExecutor.execute(futureTask);
                }
            }
        }
    };

    public <T> void execute(FutureTask<T> futureTask) {
          if (futureTask != null) {
              try {
                  mService.put(futureTask);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
    }

    private RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                mService.put(new FutureTask<>(r,null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
