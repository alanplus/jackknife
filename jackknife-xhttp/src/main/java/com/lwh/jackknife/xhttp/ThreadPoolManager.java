/*
 * Copyright (C) 2018 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.xhttp;

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
