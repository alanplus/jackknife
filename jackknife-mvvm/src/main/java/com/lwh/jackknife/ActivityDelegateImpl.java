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

    public ActivityDelegateImpl(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.info("%s - onCreate", mActivity.getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        Logger.info("%s - onStart", mActivity.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        Logger.info("%s - onResume", mActivity.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        Logger.info("%s - onPause", mActivity.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        Logger.info("%s - onStop", mActivity.getClass().getSimpleName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Logger.info("%s - onSaveInstanceState", mActivity.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        Logger.info("%s - onDestroy", mActivity.getClass().getSimpleName());
        this.mActivity = null;
    }
}
