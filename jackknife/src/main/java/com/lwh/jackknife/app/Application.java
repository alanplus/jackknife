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
    private Stack<WeakReference<Activity>> mActivityStack;

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
    /* package */ void pushTask(Activity activity){
        mActivityStack.add(new WeakReference<>(activity));
    }

    /**
     * 把顶部的Activity弹出栈。
     */
    /* package */ void popTask(){
        WeakReference<Activity> ref = mActivityStack.pop();
        Activity activity = ref.get();
        activity.finish();
        mActivityStack.remove(activity);
    }

    /**
     * 移除所有任务栈的Activity弱引用。
     */
    protected void removeAll(){
        for (WeakReference<Activity> ref:mActivityStack){
            Activity activity = ref.get();
            activity.finish();
        }
        mActivityStack.removeAllElements();
    }
}
