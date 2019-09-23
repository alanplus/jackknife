/*
 * Copyright (C) 2019 The JackKnife Open Source Project
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

package com.lwh.jackknife.av.ffmpeg;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FFmpegExecutors {

    private static class AppExecutorsHolder {
        private static final FFmpegExecutors instance = new FFmpegExecutors();
    }

    public static FFmpegExecutors get() {
        return AppExecutorsHolder.instance;
    }

    private final MainThreadExecutor mMainExecutor;//主线程
    private final ExecutorService mDiskExecutor;//文件读写
    private final ExecutorService mNetworkExecutor;//网络请求
    private final ExecutorService mDbExecutor;//数据库读写
    private final ExecutorService mWorkExecutor;//其他耗时操作

    private FFmpegExecutors() {
        this.mMainExecutor = new MainThreadExecutor();
        this.mDiskExecutor = Executors.newSingleThreadExecutor(new AppExecutorsThreadFactory("disk-io"));
        this.mNetworkExecutor = Executors.newFixedThreadPool(5, new AppExecutorsThreadFactory("network"));
        this.mDbExecutor = Executors.newFixedThreadPool(3, new AppExecutorsThreadFactory("db"));
        this.mWorkExecutor = Executors.newCachedThreadPool(new AppExecutorsThreadFactory("bg-work"));
    }

    /* ******** 直接执行 *********/

    /**
     * 主线程操作
     */
    public static void executeMain(Runnable runnable) {
        getMainExecutor().execute(runnable);
    }

    /**
     * 文件读写操作
     */
    public static void executeDisk(Runnable runnable) {
        getDiskExecutor().execute(runnable);
    }

    /**
     * 网络操作
     */
    public static void executeNetwork(Runnable runnable) {
        getNetworkExecutor().execute(runnable);
    }

    /**
     * 数据库操作
     */
    public static void executeDb(Runnable runnable) {
        getDbExecutor().execute(runnable);
    }

    /**
     * 其他耗时操作
     */
    public static void executeWork(Runnable runnable) {
        getWorkExecutor().execute(runnable);
    }


    /* ******** 获取线程池 *********/

    /**
     * 文件读写线程池
     */
    public static ExecutorService getDiskExecutor() {
        return get().mDiskExecutor;
    }

    /**
     * 网络操作线程池
     */
    public static ExecutorService getNetworkExecutor() {
        return get().mNetworkExecutor;
    }

    /**
     * 数据库操作线程池
     */
    public static ExecutorService getDbExecutor() {
        return get().mDbExecutor;
    }

    /**
     * 其他耗时操作线程池
     */
    public static ExecutorService getWorkExecutor() {
        return get().mWorkExecutor;
    }

    /**
     * 主线程
     */
    public static MainThreadExecutor getMainExecutor() {
        return get().mMainExecutor;
    }


    //主线程
    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }

        public Handler getMainThreadHandler() {
            return mainThreadHandler;
        }
    }

    //其他线程工厂
    private static class AppExecutorsThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(1);
        private String name;

        AppExecutorsThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "FFmpegExecutors-" + name + "-Thread-" + count.getAndIncrement());
        }
    }
}
