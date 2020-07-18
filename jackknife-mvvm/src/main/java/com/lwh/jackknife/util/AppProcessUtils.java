/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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

package com.lwh.jackknife.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public final class AppProcessUtils {

    private AppProcessUtils() {
    }

    public static boolean isRunInBackground(Context context) {
        ActivityManager activityManager = ServiceUtils.getActivityManager(context);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.processName.equals(context.getPackageName())) {
                return processInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    public static void killAllProcesses(Context context) {
        //杀死相关进程
        ActivityManager activityManager = ServiceUtils.getActivityManager(context);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.uid == android.os.Process.myUid() && processInfo.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(processInfo.pid);
            }
        }
        //杀死本进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
