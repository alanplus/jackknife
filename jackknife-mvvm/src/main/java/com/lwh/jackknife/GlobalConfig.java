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

import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.List;

public interface GlobalConfig {

    void applyOptions(Context context, GlobalConfig.Builder builder);

    void injectApplicationLifecycle(Context context, List<ApplicationLifecycleCallbacks> lifecycles);

    void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles);

    void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles);

    class Builder {

        private String url;
        private File cache;

        Builder baseUrl(String url) {
            this.url = url;
            return this;
        }

        Builder cacheFile(File cache) {
            this.cache = cache;
            return this;
        }

        GlobalConfig build() {
            return null;
        }
    }
}