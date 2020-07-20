package com.lwh.jackknife.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

public final class LanguageUtils {

    private static final String PREFS_JKNF_LANGUAGE = "jackknife_language";
    public enum LanguageType {
        LANGUAGE_SIMPLIFIED_CHINESE, //简体中文
        LANGUAGE_ENGLISH,   //英语
        LANGUAGE_TRADITIONAL_CHINESE,    //繁体中文
    }

    private LanguageUtils() {
    }

    public static Locale getSystemLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static String getSystemLanguage(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    private static Locale getLanguageLocale(Context context) {
        int languageType = SPUtils.obtainInteger(context, PREFS_JKNF_LANGUAGE);
        if (languageType == LanguageType.LANGUAGE_SIMPLIFIED_CHINESE.ordinal()) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if (languageType == LanguageType.LANGUAGE_ENGLISH.ordinal()) {
            return Locale.ENGLISH;
        } else if (languageType == LanguageType.LANGUAGE_TRADITIONAL_CHINESE.ordinal()) {
            return Locale.TRADITIONAL_CHINESE;
        }
        return Locale.SIMPLIFIED_CHINESE;
    }

    public static void applyLanguage(Context context) {
        Locale targetLocale = getLanguageLocale(context);
        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //语言更换生效的代码
        resources.updateConfiguration(configuration, dm);
    }

    /**
     * 切换语言。
     */
    public static void changeLanguage(Context context, int languageType) {
        SPUtils.putInteger(context, PREFS_JKNF_LANGUAGE, languageType);
        applyLanguage(context);
    }

    /**
     * 在Application#attachBaseContext处调用。
     *
     * @param context
     * @return
     */
    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            Locale locale = getLanguageLocale(context);
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        } else {
            applyLanguage(context);
            return context;
        }
    }
}
