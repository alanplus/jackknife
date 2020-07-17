/*
 * Copyright (C) 2020 The JackKnife Open Source Project
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

package com.lwh.jackknife;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.lwh.jackknife.log.Logger;

public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Logger.info("%s - onActivityCreated", activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Logger.info("%s - onActivityStarted", activity.getLocalClassName());
        onPutExtras(activity.getIntent());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Logger.info("%s - onActivityResumed", activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Logger.info("%s - onActivityPaused", activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Logger.info("%s - onActivityStopped", activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Logger.info("%s - onActivitySaveInstanceState", activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Logger.info("%s - onActivityDestroyed", activity.getLocalClassName());
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        onRemoveExtras(activity.getIntent());
    }

    protected void onPutExtras(Intent intent) {
    }

    protected void onRemoveExtras(Intent intent) {
    }
}
