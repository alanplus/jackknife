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

package com.lwh.jackknife.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.lwh.jackknife.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginManager {

    private Context mContext;
    private String mPathPluginInstalled;
    private final String METHOD_ADD_ASSET_PATH = "addAssetPath";

    public PluginManager(Context context){
        this.mContext = context;
        this.mPathPluginInstalled = context.getFilesDir().getAbsolutePath();
    }

    public void installPlugin(String pluginName, String dexPath){
        installPlugin(pluginName, dexPath, null);
    }

    public void installPlugin(String pluginName, String dexPath, String libSearchPath){
        applyTheme();
        DexClassLoader classLoader = new DexClassLoader(dexPath, mPathPluginInstalled, libSearchPath, mContext.getClassLoader());
        if (mContext.getApplicationContext() instanceof Application) {
            Application.getInstance().installPlugin(pluginName, classLoader);
        }
    }

    public void applyTheme(){
        try {
            AssetManager am = AssetManager.class.newInstance();
            Method method = am.getClass().getMethod(METHOD_ADD_ASSET_PATH, String.class);
            Resources resources = mContext.getResources();
            Resources newRes = new Resources(am, resources.getDisplayMetrics(), resources.getConfiguration());
            Resources.Theme theme = mContext.getTheme();
            Resources.Theme newTheme = newRes.newTheme();
            int cookie = (int) method.invoke(am, mPathPluginInstalled);
            if (cookie != 0) {
                newTheme.setTo(theme);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
