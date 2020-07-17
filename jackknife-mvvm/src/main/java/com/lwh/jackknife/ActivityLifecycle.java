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
    Cache<String, Object> mExtras;
    FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycle;
    List<FragmentManager.FragmentLifecycleCallbacks> mFragmentLifecycles;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {


        //配置ActivityDelegate
        if (activity instanceof IActivity) {
            ActivityDelegate activityDelegate = fetchActivityDelegate(activity);
            if (activityDelegate == null) {
                Cache<String, Object> cache = getCacheFromActivity((IActivity) activity);
                activityDelegate = new ActivityDelegateImpl(activity);
                //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
                //否则存储在 LRU 算法的存储空间中, 前提是 Activity 使用的是 IntelligentCache (框架默认使用)
                cache.put(IntelligentCache.getKeyOfKeep(ActivityDelegate.ACTIVITY_DELEGATE), activityDelegate);
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
            getCacheFromActivity((IActivity) activity).clear();
        }
    }

    /**
     * 给每个 Activity 的所有 Fragment 设置监听其生命周期, Activity 可以通过 {@link IActivity#useFragment()}
     * 设置是否使用监听,如果这个 Activity 返回 false 的话,这个 Activity 下面的所有 Fragment 将不能使用 {@link FragmentDelegate}
     * 意味着 {@link BaseFragment} 也不能使用
     *
     * @param activity
     */
    private void registerFragmentCallbacks(Activity activity) {

        if (activity instanceof AppCompatActivity) {

            //mFragmentLifecycle 为 Fragment 生命周期实现类, 用于框架内部对每个 Fragment 的必要操作, 如给每个 Fragment 配置 FragmentDelegate
            //注册框架内部已实现的 Fragment 生命周期逻辑
            ((AppCompatActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycle, true);

            if (mExtras.containsKey(IntelligentCache.getKeyOfKeep(GlobalConfig.class.getName()))) {
                List<GlobalConfig> modules = (List<GlobalConfig>) mExtras.get(IntelligentCache.getKeyOfKeep(GlobalConfig.class.getName()));
                if (modules != null) {
                    for (GlobalConfig module : modules) {
                        module.injectFragmentLifecycle(mApplication, mFragmentLifecycles);
                    }
                }
                mExtras.remove(IntelligentCache.getKeyOfKeep(GlobalConfig.class.getName()));
            }

            //注册框架外部, 开发者扩展的 Fragment 生命周期逻辑
            for (FragmentManager.FragmentLifecycleCallbacks fragmentLifecycle : mFragmentLifecycles) {
                ((AppCompatActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycle, true);
            }
        }
    }

    private ActivityDelegate fetchActivityDelegate(Activity activity) {
        ActivityDelegate activityDelegate = null;
        if (activity instanceof IActivity) {
            Cache<String, Object> cache = getCacheFromActivity((IActivity) activity);
            activityDelegate = (ActivityDelegate) cache.get(IntelligentCache.getKeyOfKeep(ActivityDelegate.ACTIVITY_DELEGATE));
        }
        return activityDelegate;
    }

    private Cache<String, Object> getCacheFromActivity(IActivity activity) {
        Cache<String, Object> cache = activity.loadCache();
        return cache;
    }
}