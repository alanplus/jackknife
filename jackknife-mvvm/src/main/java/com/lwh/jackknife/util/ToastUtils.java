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
 * WITHOUT WARRANTIES OR CONDITIONS IN ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;

    public static void showShort(final String msg) {
        if (!ThreadUtils.isMainThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showShortInternal(msg);
                }
            });
        } else {
            showShortInternal(msg);
        }
    }

    public static void showLong(final String msg) {
        if (!ThreadUtils.isMainThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showLongInternal(msg);
                }
            });
        } else {
            showLongInternal(msg);
        }
    }

    private static void showShortInternal(String msg) {
        if (mToast == null) {
            synchronized (ToastUtils.class) {
                if (mToast == null)
                    mToast = Toast.makeText(GlobalContext.get(), msg, Toast.LENGTH_SHORT);
            }
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    private static void showLongInternal(String msg) {
        if (mToast == null) {
            synchronized (ToastUtils.class) {
                if (mToast == null)
                    mToast = Toast.makeText(GlobalContext.get(), msg, Toast.LENGTH_LONG);
            }
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
