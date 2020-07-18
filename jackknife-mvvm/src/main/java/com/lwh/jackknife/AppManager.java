package com.lwh.jackknife;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.lwh.jackknife.util.AppProcessUtils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

public final class AppManager {

    private static AppManager sAppManager;
    private Application mApplication;

    /**
     * Only a mirror used to record the activity created.
     */
    protected Stack<WeakReference<? extends Activity>>
            mActivityStacks = new Stack<>();

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (sAppManager == null) {
            synchronized (AppManager.class) {
                if (sAppManager == null) {
                    sAppManager = new AppManager();
                }
            }
        }
        return sAppManager;
    }

    public AppManager init(Application application) {
        this.mApplication = application;
        return sAppManager;
    }

    public void pushTask(Activity activity) {
        synchronized (AppManager.class) {
            mActivityStacks.add(new WeakReference<>(activity));
        }
    }

    /**
     * Destroy and remove activity from the top of the task stack.
     */
    public void popTask() {
        synchronized (AppManager.class) {
            WeakReference<? extends Activity> ref = mActivityStacks.peek(); //只查看不移除
            if (ref != null) {
                mActivityStacks.pop();
            }
        }
    }

    /**
     * 让在栈顶的 {@link Activity} ,打开指定的 {@link Activity}
     *
     * @param activityClass
     */
    public void startActivity(Class activityClass) {
        startActivity(new Intent(mApplication, activityClass));
    }

    private void startActivity(Intent intent) {
        if (getTopActivity() == null) {
            //如果没有前台的activity就使用new_task模式启动activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
            return;
        }
        getTopActivity().startActivity(intent);
    }

    public Activity getTopActivity() {
        if (mActivityStacks.peek() != null) {
            return mActivityStacks.peek().get();
        }
        return null;
    }

    public void finishActivity(Class<?> activityClazz) {
        synchronized (AppManager.class) {
            Iterator<WeakReference<? extends Activity>> iterator = mActivityStacks.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next().get();
                if (next.getClass().equals(activityClazz)) {
                    iterator.remove();
                    next.finish();
                }
            }
        }
    }

    public void finishActivityUntilBottom() {
        synchronized (AppManager.class) {
            int size = mActivityStacks.size();
            for (int i = size - 1; i > 0; i--) {
                WeakReference<? extends Activity> ref = mActivityStacks.get(i);
                if (ref != null) {
                    Activity activity = ref.get();
                    if (activity != null) {
                        activity.finish();
                    }
                }
            }
        }
    }

    public void finishAllActivities() {
        synchronized (AppManager.class) {
            Iterator<WeakReference<? extends Activity>> iterator = mActivityStacks.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() != null && iterator.next().get() != null) {
                    Activity next = iterator.next().get();
                    iterator.remove();
                    next.finish();
                }
            }
        }
    }

    public void killAll() {
        synchronized (AppManager.class) {
            finishAllActivities();
            AppProcessUtils.killAllProcesses(mApplication);
        }
    }
}
