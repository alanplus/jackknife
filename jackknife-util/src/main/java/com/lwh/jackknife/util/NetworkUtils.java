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

package com.lwh.jackknife.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean checkNetwork(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context, ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            return networkInfo.isAvailable() && networkInfo.isConnected();
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context, ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            return networkInfo.isAvailable() && networkInfo.isConnected();
        }
        return false;
    }

    private static NetworkInfo getNetworkInfo(Context context, int networkType) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(networkType);
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }
}
