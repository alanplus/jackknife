package com.lwh.jackknife;

import android.app.Application;
import android.content.Context;

import androidx.fragment.app.FragmentManager;

import java.util.List;

public interface GlobalConfig {

    /**
     * 使用 {@link GlobalConfig.Builder} 给框架配置一些配置参数
     *
     * @param context {@link Context}
     * @param builder {@link GlobalConfig.Builder}
     */
    void applyOptions(Context context, GlobalConfig.Builder builder);

    /**
     * 使用 {@link AppLifecycle} 在 {@link Application} 的生命周期中注入一些操作
     *
     * @param context    {@link Context}
     * @param lifecycles {@link Application} 的生命周期容器, 可向框架中添加多个 {@link Application} 的生命周期类
     */
    void injectAppLifecycle(Context context, List<AppLifecycle> lifecycles);

    /**
     * 使用 {@link Application.ActivityLifecycleCallbacks} 在 {@link androidx.appcompat.app.AppCompatActivity} 的生命周期中注入一些操作
     *
     * @param context    {@link Context}
     * @param lifecycles {@link androidx.appcompat.app.AppCompatActivity} 的生命周期容器, 可向框架中添加多个 {@link androidx.appcompat.app.AppCompatActivity} 的生命周期类
     */
    void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles);

    /**
     * 使用 {@link FragmentManager.FragmentLifecycleCallbacks} 在 {@link androidx.fragment.app.Fragment} 的生命周期中注入一些操作
     *
     * @param context    {@link Context}
     * @param lifecycles {@link androidx.fragment.app.Fragment} 的生命周期容器, 可向框架中添加多个 {@link androidx.fragment.app.Fragment} 的生命周期类
     */
    void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles);

    public static class Builder {

        GlobalConfig build() {
            return null;
        }
    }
}