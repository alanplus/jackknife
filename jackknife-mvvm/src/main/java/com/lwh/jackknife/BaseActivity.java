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
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.lwh.jackknife.cache.Cache;
import com.lwh.jackknife.cache.CacheType;
import com.lwh.jackknife.cache.LruCache;
import com.lwh.jackknife.log.Logger;
import com.lwh.jackknife.net.NetworkChangeObserver;
import com.lwh.jackknife.net.NetworkStateReceiver;
import com.lwh.jackknife.permission.Action;
import com.lwh.jackknife.permission.PermissionManager;
import com.lwh.jackknife.util.NetworkUtils;
import com.lwh.jackknife.util.StatusBarUtils;

import java.util.List;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity
        implements ActivityCache {

    protected T mBinding;
    protected final String TAG = this.getClass().getSimpleName();
    private Cache<String, Object> mCache;
    protected NetworkChangeObserver mNetworkChangeObserver = null;

    public Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        onShowStatusBar();
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
        onNewIntent(getIntent());
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

    protected void onShowStatusBar() {
        StatusBarUtils.setStatusBarColor(this, Color.BLACK);
    }

    protected String[] requirePermissions() {
        return new String[0];
    }

    @Override
    protected void onDestroy() {
        NetworkStateReceiver.unregisterObserver(mNetworkChangeObserver);
        super.onDestroy();
    }

    /**
     * 网络已连接，需要使用到{@link com.lwh.jackknife.BaseApplication}，才会有回调。
     *
     * @param type
     */
    protected void onNetworkConnected(NetworkUtils.ApnType type) {
    }

    /**
     * 网络连接已断开，需要使用到{@link com.lwh.jackknife.BaseApplication}，才会有回调。
     *
     * @param type
     */
    protected void onNetworkDisconnected() {
    }

    protected abstract int getLayoutId();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        onGetExtras(bundle, intent);
    }

    protected void onGetExtras(@Nullable Bundle bundle, Intent intent) {
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
    public synchronized Cache<String, Object> loadCache() {
        if (mCache == null) {
            mCache = cacheFactory().build(CacheType.ACTIVITY_CACHE, this);
        }
        return mCache;
    }
}
