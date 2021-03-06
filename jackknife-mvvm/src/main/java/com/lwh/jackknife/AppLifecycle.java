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

import android.app.Application;
import android.content.Context;

import com.lwh.jackknife.net.NetworkStateReceiver;

public class AppLifecycle implements ApplicationLifecycleCallbacks {

    @Override
    public void attachBaseContext(Context base) {
    }

    @Override
    public void onCreate(Application application) {
        NetworkStateReceiver.registerNetworkStateReceiver(application);
    }

    @Override
    public void onTerminate(Application application) {
        NetworkStateReceiver.unregisterNetworkStateReceiver(application);
    }
}
