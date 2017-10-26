/*
 *
 *  * Copyright (C) 2017 The JackKnife Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.lwh.jackknife.plugin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;

import com.lwh.jackknife.R;
import com.lwh.jackknife.app.Activity;
import com.lwh.jackknife.util.IoUtils;
import com.lwh.jackknife.util.Logger;
import com.lwh.jackknife.util.TextUtils;

import java.io.File;

import dalvik.system.DexClassLoader;

public class PluginActivity extends Activity {

    private ClassLoader mClassLoader;
    private Class<AssetManager> mAssetManagerClass;
    private AssetManager mAssetManager;
    private Theme mTheme;
    private Resources mBundleResources;
    private final String METHOD_ADD_ASSET_PATH = "addAssetPath";
    private String mApkPath = "";
    private String mPluginPath = "";

    public void installPlugin(String path){
        File pluginFolder = new File(getFilesDir().getAbsolutePath()+"/plugins");
        File apkFile = new File(path);
        if (!pluginFolder.exists()) {
            IoUtils.createFolder(pluginFolder.getAbsolutePath());
        }
        IoUtils.copy(apkFile, pluginFolder.getAbsolutePath());
        File pluginFile = new File(pluginFolder.getAbsolutePath(), "plugin.apk");
        if (pluginFile.exists()) {
            mPluginPath = pluginFile.getAbsolutePath();
        }else{
            Logger.error("插件文件不存在");
            mPluginPath = "";
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        String sdRoot = IoUtils.getSdRoot();
        if (TextUtils.isNotEmpty(sdRoot)){
            mApkPath = sdRoot+"/plugin.apk";
            installPlugin(mApkPath);
        }
    }

    @Override
    public Resources getResources() {
        return mBundleResources == null ? super.getResources() : mBundleResources;
    }

    @Override
    public ClassLoader getClassLoader() {
        return mClassLoader == null ? super.getClassLoader() : mClassLoader;
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            File optimizedFile = new File(getFilesDir(), "dex");
            if (!optimizedFile.exists()) {
                IoUtils.createFolder(optimizedFile.getAbsolutePath());
            }
            mClassLoader = new DexClassLoader(mPluginPath,
                    optimizedFile.getAbsolutePath(),
                    null,
                    super.getClassLoader());
            mAssetManagerClass = AssetManager.class;
            mAssetManager = mAssetManagerClass.newInstance();
            mAssetManagerClass.getDeclaredMethod(METHOD_ADD_ASSET_PATH, String.class).invoke(mAssetManager, mPluginPath);
            Resources resources = super.getResources();
            mBundleResources = new Resources(mAssetManager, resources.getDisplayMetrics(), resources.getConfiguration());
            mTheme = mBundleResources.newTheme();
//            mTheme.setTo(super.getTheme());
            Class<?> fragmentClass = getClassLoader().loadClass("com.lwh.jackknife.plugindemo.plugin.CountFragment");
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().add(R.id.jackknife_framelayout_plugin, fragment).show(fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
