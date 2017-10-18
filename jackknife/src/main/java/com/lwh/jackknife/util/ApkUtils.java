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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ApkUtils {

    public static final int UNKNOWN_VERSION = -1;

    private ApkUtils() {
    }

    public static PackageInfo getPackageInfo(Context context, String apkFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkgInfo;
    }

    public static Drawable getAppIcon(Context context, String apkFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilePath);
        if (pkgInfo == null) {
            return null;
        }
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            appInfo.sourceDir = apkFilePath;
            appInfo.publicSourceDir = apkFilePath;
        }
        return pm.getApplicationIcon(appInfo);
    }

    public static CharSequence getAppLabel(Context context, String apkFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilePath);
        if (pkgInfo == null) {
            return null;
        }
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            appInfo.sourceDir = apkFilePath;
            appInfo.publicSourceDir = apkFilePath;
        }
        return pm.getApplicationLabel(appInfo);
    }

    /**
     * 启动一个已安装的app。
     *
     * @param context 上下文。
     * @param packageName 包名。
     * @param className 全类名。
     */
    public static void launchApp(Context context, String packageName, String className){
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName(packageName, className));
        context.startActivity(intent);
    }

    /**
     * 安装一个apk。
     *
     * @param context 上下文。
     * @param apkPath apk文件的路径。
     */
    public static void install(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 得到一个apk文件的签名。
     *
     * @param apkPath apk文件的路径。
     * @return 签名。
     */
    public static Signature[] getUninstallApkSignatures(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage",
                    typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = PackageManager.GET_SIGNATURES;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = Integer.TYPE;
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates",
                    typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到本应用的应用签名。
     *
     * @param context 上下文。
     * @return 签名。
     */
    public static Signature[] getSignatures(Context context){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到版本名。
     *
     * @param context 上下文。
     * @return 版本名。
     */
    public static String getVersionName(Context context){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    /**
     * 得到版本号。
     *
     * @param context 上下文。
     * @return 版本号。
     */
    public static int getVersionCode(Context context){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return UNKNOWN_VERSION;
    }

    /**
     * 提取本应用的apk文件的路径，应用安装后，系统会自动在这个目录备份。
     *
     * @param context 上下文。
     * @return apk文件的路径。
     */
    public static String extractApk(Context context){
        ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
        String apkPath = applicationInfo.sourceDir;
        return apkPath;
    }

    /**
     * 通过SDK的r级别获取其版本名称。
     *
     * @param sdk r级别。
     * @return 版本名称。
     */
    public static String getAndroidVersion(int sdk){
        switch (sdk){
            case Build.VERSION_CODES.BASE:
                return "Android 1.0";
            case Build.VERSION_CODES.BASE_1_1:
                return "Android 1.1";
            case Build.VERSION_CODES.CUPCAKE:
                return "Android 1.5";
            case Build.VERSION_CODES.DONUT:
                return "Android 1.6";
            case Build.VERSION_CODES.ECLAIR:
                return "Android 2.0";
            case Build.VERSION_CODES.ECLAIR_0_1:
                return "Android 2.0.1";
            case Build.VERSION_CODES.ECLAIR_MR1:
                return "Android 2.1";
            case Build.VERSION_CODES.FROYO:
                return "Android 2.2";
            case Build.VERSION_CODES.GINGERBREAD:
                return "Android 2.3";
            case Build.VERSION_CODES.GINGERBREAD_MR1:
                return "Android 2.3.3";
            case Build.VERSION_CODES.HONEYCOMB:
                return "Android 3.0";
            case Build.VERSION_CODES.HONEYCOMB_MR1:
                return "Android 3.1";
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                return "Android 3.2";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                return "Android 4.0";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                return "Android 4.0.3";
            case Build.VERSION_CODES.JELLY_BEAN:
                return "Android 4.1";
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
                return "Android 4.2";
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                return "Android 4.3";
            case Build.VERSION_CODES.KITKAT:
                return "Android 4.4";
            case Build.VERSION_CODES.KITKAT_WATCH:
                return "Android 4.4W";
            case Build.VERSION_CODES.LOLLIPOP:
                return "Android 5.0";
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                return "Android 5.1";
            case Build.VERSION_CODES.M:
                return "Android 6.0";
        }
        throw new RuntimeException("不可知的Android系统版本。");
    }

    /**
     * 生成apk差分包。
     *
     * @param oldPath 旧版本apk文件的路径。
     * @param newPath 新版本apk文件的路径。
     * @param patchPath 差分包的路径。
     * @return 是否成功，0表示成功，非0表示失败。
     */
    public static int diffApk(String oldPath, String newPath, String patchPath) {
        return diffApkNative(oldPath, newPath, patchPath);
    }

    /**
     * 合并新版本的安装包。
     *
     * @param oldPath 旧版本apk文件的路径。
     * @param newPath 新版本apk文件的路径。
     * @param patchPath 差分包的路径。
     * @return 是否成功，0表示成功，非0表示失败。
     */
    public static int patchApk(String oldPath, String newPath, String patchPath) {
        return patchApkNative(oldPath, newPath, patchPath);
    }

    public native static int diffApkNative(String oldPath, String newPath, String patchPath);

    public native static int patchApkNative(String oldPath, String newPath, String patchPath);

    static{
        System.loadLibrary("apkutils");
    }
}
