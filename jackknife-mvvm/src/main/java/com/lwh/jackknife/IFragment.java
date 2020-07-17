package com.lwh.jackknife;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.lwh.jackknife.cache.Cache;

public interface IFragment {

    /**
     * 提供在 {@link Activity} 生命周期内的缓存容器, 可向此 {@link Activity} 存取一些必要的数据
     * 此缓存容器和 {@link Activity} 的生命周期绑定, 如果 {@link Activity} 在屏幕旋转或者配置更改的情况下
     * 重新创建, 那此缓存容器中的数据也会被清空
     *
     * @return like {@link android.util.LruCache}
     */
    @NonNull
    Cache<String, Object> loadCache();

    void initData(Bundle savedInstanceState);
}
