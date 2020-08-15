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

package com.lwh.jackknife.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.lwh.jackknife.R;

public final class DialogUtils {

    private DialogUtils() {
    }

    public interface Callback {
        void onConfirm(String tips);
    }

    public static void showTips(final String tips, final Callback callback) {
        Context context = GlobalContext.get().getApplicationContext();
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.jk_tips))
                .setMessage(tips)
                .setCancelable(false)
                .setNeutralButton(context.getString(R.string.jk_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (callback != null) {
                                    callback.onConfirm(tips);
                                }
                            }
                        })
                .create()
                .show();
    }

    public static void showTips(String tips) {
        showTips(tips, null);
    }
}
