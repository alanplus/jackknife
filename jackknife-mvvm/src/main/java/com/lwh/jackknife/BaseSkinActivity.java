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

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lwh.jackknife.cache.Cache;
import com.lwh.jackknife.cache.CacheType;
import com.lwh.jackknife.cache.LruCache;
import com.lwh.jackknife.net.NetworkChangeObserver;
import com.lwh.jackknife.net.NetworkStateReceiver;
import com.lwh.jackknife.skin.SkinActivity;
import com.lwh.jackknife.util.NetworkUtils;

public abstract class BaseSkinActivity<T extends ViewDataBinding> extends SkinActivity
        implements ActivityCache {

    protected T mBinding;
    protected final String TAG = this.getClass().getSimpleName();
    private Cache<String, Object> mCache;
    protected NetworkChangeObserver mNetworkChangeObserver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        initData(savedInstanceState);
        NetworkStateReceiver.registerObserver(mNetworkChangeObserver);
    }

    protected void onNetworkConnected(NetworkUtils.ApnType type) {
    }

    protected void onNetworkDisconnected() {
    }

    protected abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkStateReceiver.unregisterObserver(mNetworkChangeObserver);
    }

    @Override
    public Cache.Factory cacheFactory() {
        return new Cache.Factory() {
            @Override
            public Cache build(CacheType type, Context context) {
                return new LruCache(type.calculateCacheSize(context));
            }
        };
    }

    @NonNull
    @Override
    public Cache<String, Object> loadCache() {
        if (mCache == null) {
            mCache = cacheFactory().build(CacheType.ACTIVITY_CACHE, this);
        }
        return mCache;
    }
}
