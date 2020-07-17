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

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lwh.jackknife.cache.Cache;
import com.lwh.jackknife.cache.CacheType;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements IActivity {

    protected T mBinding;
    protected final String TAG = this.getClass().getSimpleName();
    private Cache<String, Object> mCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    protected abstract int getLayoutId();

    @NonNull
    @Override
    public Cache<String, Object> loadCache() {
        if (mCache == null) {
            mCache = ((App)getApplicationContext()).getAppComponent().cacheFactory().build(CacheType.ACTIVITY_CACHE);
        }
        return mCache;
    }
}
