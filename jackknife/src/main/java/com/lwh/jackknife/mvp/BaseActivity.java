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

import com.lwh.jackknife.app.AppCompatActivity;
import com.lwh.jackknife.util.Logger;

/**
 * Activity的通用基类，本项目所有Activity必须继承此类，通过把自己的子类绑定在Presenter上，来实现Presenter层对
 * View层的代理，Activity被销毁的时候，绑定了此Activity的Presenter也会被自动销毁。所以凡是生命周期可能比
 * Activity长的操作都应该放在Presenter中实现，比如在子线程中执行的操作。
 *
 * @param <V> 视图，Activity、Fragment等。
 * @param <P> 主导器。
 */
public abstract class BaseActivity<V extends IBaseView, P extends BasePresenter<V>> extends AppCompatActivity {

    /**
     * 业务逻辑主导器。
     */
    protected P mPresenter;

    protected final String TAG = getClass().getSimpleName();

    /**
     * 创建出相关业务逻辑的主导器。
     *
     * @return 具体业务逻辑主导器。
     */
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
        super.onResume();
        Logger.info(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.info(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.info(TAG, "onStop()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.info(TAG, "onStart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.info(TAG, "onDestroy()");
        mPresenter.detachView();
    }
}
