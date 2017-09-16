package com.lwh.jackknife.app;

import java.lang.ref.WeakReference;
import java.util.Stack;

public class Application extends android.app.Application {
    
    private Stack<WeakReference<Activity>> mActivityStack;
    private static Application sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        mActivityStack = new Stack<>();
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

    /* package */ void removeAll(){
        for (WeakReference<Activity> ref:mActivityStack){
            Activity activity = ref.get();
            activity.finish();
        }
        mActivityStack.removeAllElements();
    }
}
