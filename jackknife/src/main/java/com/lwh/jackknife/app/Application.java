package com.lwh.jackknife.app;

import com.lwh.jackknife.orm.helper.OrmSQLiteOpenHelper;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * 如果使用了ORM模块，需要继承此类。
 */
public class Application extends android.app.Application {
    
    private Stack<WeakReference<Activity>> mActivityStack;
    private static Application sApp;
    private OrmSQLiteOpenHelper mOrmSQLiteOpenHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        mActivityStack = new Stack<>();
    }

    public void attach(OrmSQLiteOpenHelper helper){
        this.mOrmSQLiteOpenHelper = helper;
    }

    public boolean isSQLiteOpenHelperAttached(){
        if (mOrmSQLiteOpenHelper != null){
            return true;
        }
        return false;
    }

    public OrmSQLiteOpenHelper getSQLiteOpenHelper(){
        return mOrmSQLiteOpenHelper;
    }

    public static Application getInstance(){
        return sApp;
    }

    /* package */ void pushTask(Activity activity){
        mActivityStack.add(new WeakReference<>(activity));
    }

    /* package */ void popTask(){
        WeakReference<Activity> ref = mActivityStack.pop();
        Activity activity = ref.get();
        activity.finish();
        mActivityStack.remove(activity);
    }

    protected void removeAll(){
        for (WeakReference<Activity> ref:mActivityStack){
            Activity activity = ref.get();
            activity.finish();
        }
        mActivityStack.removeAllElements();
    }
}
