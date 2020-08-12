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
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.lwh.jackknife.autosize.AutoSizeActivity;
import com.lwh.jackknife.cache.Cache;
import com.lwh.jackknife.cache.CacheType;
import com.lwh.jackknife.cache.LruCache;
import com.lwh.jackknife.log.Logger;
import com.lwh.jackknife.net.NetworkChangeObserver;
import com.lwh.jackknife.net.NetworkStateReceiver;
import com.lwh.jackknife.permission.Action;
import com.lwh.jackknife.permission.PermissionManager;
import com.lwh.jackknife.util.NetworkUtils;

import java.util.List;

public abstract class BaseAutoSizeActivity<T extends ViewDataBinding> extends AutoSizeActivity
        implements ActivityCache {

    protected final String TAG = this.getClass().getSimpleName();
    protected T mBinding;
    protected NetworkChangeObserver mNetworkChangeObserver = null;
    private Cache<String, Object> mCache;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mNetworkChangeObserver = new NetworkChangeObserver() {
            @Override
            public void onNetworkConnect(NetworkUtils.ApnType type) {
                onNetworkConnected(type);
            }

            @Override
            public void onNetworkDisconnect() {
                onNetworkDisconnected();
            }
        };
        NetworkStateReceiver.registerObserver(mNetworkChangeObserver);
        if (requirePermissions().length > 0) {
            PermissionManager.with(this)
                    .runtime()
                    .permission(requirePermissions())
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> permissions) {
                            initData(savedInstanceState);
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> permissions) {
                            for (String permission : permissions) {
                                Logger.error("未授予权限" + permission);
                            }
                        }
                    })
                    .start();
        } else {
            initData(savedInstanceState);
        }
    }

    protected String[] requirePermissions() {
        return new String[0];
    }

    @Override
    protected void onStart() {
        super.onStart();
        onPutExtras(getIntent());
    }

    @Override
    protected void onDestroy() {
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        onRemoveExtras(getIntent());
        NetworkStateReceiver.unregisterObserver(mNetworkChangeObserver);
        super.onDestroy();
    }

    protected void onPutExtras(Intent intent) {
    }

    protected void onRemoveExtras(Intent intent) {
    }

    protected void onNetworkConnected(NetworkUtils.ApnType type) {
    }

    protected void onNetworkDisconnected() {
    }

    protected abstract int getLayoutId();

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
