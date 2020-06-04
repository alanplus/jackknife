package dora;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 开发者可以扩展此类来自定义崩溃信息，这样的话，必须重写toString()方法生效。
 */
public class CrashInfo {

    private String versionName;
    private int versionCode;
    private int sdkVersion;
    //Android版本号
    private String release;
    //手机型号
    private String model;
    //手机制造商
    private String mobileName;
    private Thread thread;
    private Throwable throwable;

    protected CrashInfo(Context context) {
        //获取手机的一些信息
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo;
        try {
            pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionName = pkgInfo.versionName;
            versionCode = pkgInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "unknown";
            versionCode = -1;
        }
        sdkVersion = Build.VERSION.SDK_INT;
        release = Build.VERSION.RELEASE;
        model = Build.MODEL;
        mobileName = Build.MANUFACTURER;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setException(Throwable e) {
        this.throwable = e;
    }

    @Override
    public String toString() {
        return "\n线程"+thread.getName()+"#"+thread.getId()
                +"\nMobile型号：" + model + "\nMobileName：" + mobileName + "\nSDK版本：" + sdkVersion +
                "\nAndroid版本：" + release +
            "\n版本名称：" + versionName + "\n版本号：" + versionCode + "\n异常信息：" + throwable.toString();
    }
}