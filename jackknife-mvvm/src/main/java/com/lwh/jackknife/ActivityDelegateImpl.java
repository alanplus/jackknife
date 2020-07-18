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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lwh.jackknife.log.Logger;

public class ActivityDelegateImpl implements ActivityDelegate {

    private Activity mActivity;
    private ActivityCache mActivityCache;

    public ActivityDelegateImpl(Activity activity) {
        this.mActivity = activity;
        this.mActivityCache = (ActivityCache) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.info("%s - onCreate", mActivity.getLocalClassName());
    }

    @Override
    public void onStart() {
        Logger.info("%s - onStart", mActivity.getLocalClassName());
        onPutExtras(mActivity.getIntent());
    }

    @Override
    public void onResume() {
        Logger.info("%s - onResume", mActivity.getLocalClassName());
    }

    @Override
    public void onPause() {
        Logger.info("%s - onPause", mActivity.getLocalClassName());
    }

    @Override
    public void onStop() {
        Logger.info("%s - onStop", mActivity.getLocalClassName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Logger.info("%s - onSaveInstanceState", mActivity.getLocalClassName());
    }

    @Override
    public void onDestroy() {
        Logger.info("%s - onDestroy", mActivity.getLocalClassName());
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        onRemoveExtras(mActivity.getIntent());
        this.mActivityCache = null;
        this.mActivity = null;
    }

    @Override
    public void onPutExtras(Intent intent) {
    }

    @Override
    public void onRemoveExtras(Intent intent) {
    }
}
