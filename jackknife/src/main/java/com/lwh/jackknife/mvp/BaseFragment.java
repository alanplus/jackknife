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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lwh.jackknife.app.Fragment;
import com.lwh.jackknife.util.Logger;

public abstract class BaseFragment<V extends IBaseView, P extends BasePresenter<V>> extends Fragment {

    protected P mPresenter;

    protected final String TAG = getClass().getSimpleName();

    protected abstract P createPresenter();

    public void onActivityCreated(Bundle savedInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attachView((V)this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        Logger.info(TAG, "onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Logger.info(TAG, "onDetach()");
        super.onDetach();
    }

    @Override
    public void onStart() {
        Logger.info(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.info(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onDestroy() {
        Logger.info(TAG, "onDestroy()");
        super.onDestroy();
        mPresenter.detachView();
    }
}
