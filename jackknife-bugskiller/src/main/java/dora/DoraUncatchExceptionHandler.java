package dora;

import android.content.Context;
import android.os.Process;

public class DoraUncatchExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;//留作备用

    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;//系统的默认异常处理类

    private static DoraUncatchExceptionHandler instance = new DoraUncatchExceptionHandler();//用户自定义的异常处理类

    private DoraUncatchExceptionHandler() {
    }

    public static DoraUncatchExceptionHandler getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //收集异常信息，做我们自己的处理
        Collector collector = new CrashInfoCollector2();
        collector.collect(new CrashInfo());
        collector.report(new StoragePolicy());
        //如果系统提供了异常处理类，则交给系统去处理
        if (mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(t, e);
        } else {
            //否则我们自己处理，自己处理通常是让app退出
            Process.killProcess(Process.myPid());
        }
    }
}