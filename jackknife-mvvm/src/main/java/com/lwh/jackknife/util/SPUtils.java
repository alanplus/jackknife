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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SPUtils {

    private SPUtils() {
    }

    private static SharedPreferences sPreferences;
    private static SPUtils sInstance;

    private SharedPreferences.Editor getEditor() {
        return sPreferences.edit();
    }

    private SPUtils(Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SPUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SPUtils.class) {
                if (sInstance == null) sInstance = new SPUtils(context);
            }
        }
        return sInstance;
    }

    private void _putString(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    public static void putString(Context context, String key, String value) {
        getInstance(context)._putString(key, value);
    }

    private String _obtainString(String key) {
        return sPreferences.getString(key, null);
    }

    public static String obtainString(Context context, String key) {
        return getInstance(context)._obtainString(key);
    }

    private void _putInteger(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public static void putInteger(Context context, String key, int value) {
        getInstance(context)._putInteger(key, value);
    }

    private int _obtainInteger(String key) {
        return sPreferences.getInt(key, 0);
    }

    public static int obtainInteger(Context context, String key) {
        return getInstance(context)._obtainInteger(key);
    }

    private void _putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getInstance(context)._putBoolean(key, value);
    }

    private boolean _obtainBoolean(String key, boolean defValue) {
        return sPreferences.getBoolean(key, defValue);
    }

    public static boolean obtainBoolean(Context context, String key, boolean defValue) {
        return getInstance(context)._obtainBoolean(key, defValue);
    }

    private <T> boolean _putObject(String key, T value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            // 将字节流编码成base64的字符串
            String base64 = new String(Base64.encode(baos
                    .toByteArray(), Base64.DEFAULT));
            getEditor().putString(key, base64).apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            IoUtils.close(baos);
            IoUtils.close(oos);
        }
    }

    public static <T> boolean putObject(Context context, String key, T value) {
        return getInstance(context)._putObject(key, value);
    }

    private <T> T _obtainObject(String key) {
        T value = null;
        String base64Val = sPreferences.getString(key, null);
        if (base64Val == null) {
            return null;
        }
        // 读取字节
        byte[] base64 = Base64.decode(base64Val.getBytes(), Base64.DEFAULT);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        ObjectInputStream bis;
        try {
            bis = new ObjectInputStream(bais);
            // 读取对象
            value = (T) bis.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.close(bais);
        }
        return value;
    }

    public static <T> T obtainObject(Context context, String key) {
        return getInstance(context)._obtainObject(key);
    }

    private void _remove(String key) {
        getEditor().remove(key).apply();
    }

    public static void remove(Context context, String key) {
        getInstance(context)._remove(key);
    }

    private void _clear() {
        getEditor().clear().apply();
    }

    public static void clear(Context context) {
        getInstance(context)._clear();
    }
}
