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
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.lwh.jackknife.cache.Cache;
import com.lwh.jackknife.cache.IntelligentCache;

import java.util.List;

public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    Application mApplication;
    Cache<String, Object> mConfigCache;
    FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycle;
    List<FragmentManager.FragmentLifecycleCallbacks> mFragmentLifecycles;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //配置ActivityDelegate
        if (activity instanceof ActivityCache) {
            ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
            if (activityDelegate == null) {
                Cache<String, Object> cache = ((ActivityCache) activity).loadCache();
                activityDelegate = new ActivityDelegateImpl(activity);
                //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
                //否则存储在 LRU 算法的存储空间中, 前提是 Activity 使用的是 IntelligentCache (框架默认使用)
                cache.put(IntelligentCache.getKeyOfKeep(ActivityDelegate.CACHE_KEY), activityDelegate);
            }
            activityDelegate.onCreate(savedInstanceState);
        }
        registerFragmentCallbacks(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
        if (activityDelegate != null) {
            activityDelegate.onStart();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
        if (activityDelegate != null) {
            activityDelegate.onResume();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
        if (activityDelegate != null) {
            activityDelegate.onPause();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
        if (activityDelegate != null) {
            activityDelegate.onStop();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
        if (activityDelegate != null) {
            activityDelegate.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
        if (activityDelegate != null) {
            activityDelegate.onDestroy();
            if (activity instanceof ActivityCache) {
                ((ActivityCache) activity).loadCache().clear();
            }
        }
    }

    private void registerFragmentCallbacks(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycle, true);
            if (mConfigCache.containsKey(IntelligentCache.getKeyOfKeep(GlobalConfig.CACHE_KEY))) {
                List<GlobalConfig> modules = (List<GlobalConfig>) mConfigCache.get(IntelligentCache.getKeyOfKeep(GlobalConfig.CACHE_KEY));
                if (modules != null) {
                    for (GlobalConfig module : modules) {
                        module.injectFragmentLifecycle(mApplication, mFragmentLifecycles);
                    }
                }
                mConfigCache.remove(IntelligentCache.getKeyOfKeep(GlobalConfig.CACHE_KEY));
            }
            for (FragmentManager.FragmentLifecycleCallbacks fragmentLifecycle : mFragmentLifecycles) {
                ((AppCompatActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycle, true);
            }
        }
    }

    private ActivityDelegate fetchActivityDelegate(Activity activity) {
        ActivityDelegate activityDelegate = null;
        if (activity instanceof ActivityCache) {
            Cache<String, Object> cache = ((ActivityCache) activity).loadCache();
            activityDelegate = (ActivityDelegate) cache.get(IntelligentCache.getKeyOfKeep(ActivityDelegate.CACHE_KEY));
        }
        return activityDelegate;
    }
}