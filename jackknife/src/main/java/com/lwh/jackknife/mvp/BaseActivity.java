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

package com.lwh.jackknife.mvp;

import android.os.Bundle;

import com.lwh.jackknife.app.Activity;
import com.lwh.jackknife.util.Logger;

public abstract class BaseActivity<V extends IBaseView, P extends BasePresenter<V>> extends Activity {

    protected P mPresenter;

    protected final String TAG = getClass().getSimpleName();

    protected abstract P createPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.info(TAG, "onCreate()");
        mPresenter = createPresenter();
        mPresenter.attachView((V)this);
    }

    @Override
    protected void onResume() {
        Logger.info(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Logger.info(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Logger.info(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Logger.info(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Logger.info(TAG, "onDestroy()");
        super.onDestroy();
        mPresenter.detachView();
    }
}
