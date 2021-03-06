/*
 * Copyright (C) 2019 The JackKnife Open Source Project
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

package com.lwh.jackknife.permission.runtime.option;

import android.support.annotation.NonNull;

import com.lwh.jackknife.permission.runtime.PermissionRequest;
import com.lwh.jackknife.permission.runtime.setting.SettingRequest;

public interface RuntimeOption {

    /**
     * One or more permissions.
     */
    PermissionRequest permission(@NonNull String... permissions);

    /**
     * One or more permission groups.
     */
    PermissionRequest permission(@NonNull String[]... groups);

    /**
     * Permission settings.
     */
    SettingRequest setting();
}