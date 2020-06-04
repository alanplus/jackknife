package dora;

import android.content.Context;
import android.os.Process;

class DoraUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private CrashConfig mConf;

    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;//系统的默认异常处理类

    private static DoraUncaughtExceptionHandler instance = new DoraUncaughtExceptionHandler();//用户自定义的异常处理类

    private DoraUncaughtExceptionHandler() {
    }

    static DoraUncaughtExceptionHandler getInstance() {
        return instance;
    }

    void init(Context context, CrashConfig config) {
        this.mContext = context.getApplicationContext();
        this.mConf = config;
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //收集异常信息，做我们自己的处理
        Collector collector = new CrashInfoCollector2();
        CrashInfo info = mConf.info;
        info.setException(e);
        collector.collect(info);
        collector.report(t, mConf.policy);
        //如果系统提供了异常处理类，则交给系统去处理
        if (mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(t, e);
        } else {
            //否则我们自己处理，自己处理通常是让app退出
            Process.killProcess(Process.myPid());
        }
    }
}