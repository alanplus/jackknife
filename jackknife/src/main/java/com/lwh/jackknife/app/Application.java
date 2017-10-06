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

package com.lwh.jackknife.app;

import android.database.sqlite.SQLiteOpenHelper;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * 如果使用了ORM模块，你需要继承此类。
 */
public class Application extends android.app.Application {

    /**
     * 存放Activity弱引用的栈。
     */
    private Stack<WeakReference<android.app.Activity>> mActivityStack;

    /**
     * Application的单例。
     */
    private static Application sApp;

    /**
     * SQLite数据库打开助手。
     */
    private SQLiteOpenHelper mSQLiteOpenHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        mActivityStack = new Stack<>();
    }

    /**
     * 依附SQLite打开助手。
     *
     * @param helper SQLite打开助手。
     */
    public void attach(SQLiteOpenHelper helper){
        this.mSQLiteOpenHelper = helper;
    }

    /**
     * 检测SQLite打开助手是否依附上。
     *
     * @return 是否依附成功。
     */
    public boolean isSQLiteOpenHelperAttached(){
        if (mSQLiteOpenHelper != null){
            return true;
        }
        return false;
    }

    public SQLiteOpenHelper getSQLiteOpenHelper(){
        return mSQLiteOpenHelper;
    }

    public static Application getInstance(){
        return sApp;
    }

    /**
     * 把Activity压入栈。
     *
     * @param activity
     */
    /* package */ void pushTask(android.app.Activity activity){
        mActivityStack.add(new WeakReference<>(activity));
    }

    /**
     * 把顶部的Activity弹出栈。
     */
    /* package */ void popTask(){
        WeakReference<android.app.Activity> ref = mActivityStack.pop();
        android.app.Activity activity = ref.get();
        activity.finish();
        mActivityStack.remove(activity);
    }

    /**
     * 移除所有任务栈的Activity弱引用。
     */
    protected void removeAll(){
        for (WeakReference<android.app.Activity> ref:mActivityStack){
            android.app.Activity activity = ref.get();
            activity.finish();
        }
        mActivityStack.removeAllElements();
    }
}
