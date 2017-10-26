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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.lwh.jackknife.R;
import com.lwh.jackknife.app.Activity;
import com.lwh.jackknife.util.IoUtils;
import com.lwh.jackknife.util.TextUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;

public abstract class BasePluginActivity extends Activity {

    private ClassLoader mClassLoader;
    private Class<AssetManager> mAssetManagerClass;
    private AssetManager mAssetManager;
    private Theme mTheme;
    private Resources mBundleResources;
    private final String METHOD_ADD_ASSET_PATH = "addAssetPath";
    private final String DEX = "dex";

    protected abstract String loadPlugin();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        String sdRoot = IoUtils.getSdRoot();
        if (TextUtils.isNotEmpty(sdRoot)){
            PluginManager manager = new PluginManager(this);
            File pluginFile = new File(loadPlugin());
            if (pluginFile != null && pluginFile.exists()) {
                manager.installPlugin(pluginFile.getAbsolutePath());
                File optimizedFile = new File(getFilesDir(), DEX);
                if (!optimizedFile.exists()) {
                    IoUtils.createFolder(optimizedFile.getAbsolutePath());
                }
                mClassLoader = new DexClassLoader(pluginFile.getAbsolutePath(),
                        optimizedFile.getAbsolutePath(),
                        null,
                        super.getClassLoader());
                mAssetManagerClass = AssetManager.class;
                try {
                    mAssetManager = mAssetManagerClass.newInstance();
                    mAssetManagerClass.getDeclaredMethod(METHOD_ADD_ASSET_PATH, String.class).invoke(mAssetManager, pluginFile.getAbsolutePath());
                    Resources resources = super.getResources();
                    mBundleResources = new Resources(mAssetManager, resources.getDisplayMetrics(), resources.getConfiguration());
                    mTheme = mBundleResources.newTheme();
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

    protected void loadFragment(String fragmentClassName) {
        Class<?> fragmentClass;
        try {
            fragmentClass = getClassLoader().loadClass(fragmentClassName);
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fm = getFragmentManager();
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setId(R.id.jackknife_framelayout_plugin);
            frameLayout.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            fm.beginTransaction().add(R.id.jackknife_framelayout_plugin, fragment).show(fragment).commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
