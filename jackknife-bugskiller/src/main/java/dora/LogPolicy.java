package dora;

import android.util.Log;

public class LogPolicy implements CrashReportPolicy {

    public static final int LOG_LEVEL_DEBUG = 0;
    public static final int LOG_LEVEL_INFO = 1;
    public static final int LOG_LEVEL_ERROR = 2;
    private int mLevel = LOG_LEVEL_DEBUG;

    public LogPolicy() {
    }

    public LogPolicy(int level) {
        this.mLevel = level;
    }

    @Override
    public void report(final CrashInfo info) {
        if (mLevel == LOG_LEVEL_DEBUG) {
            Log.d("dora", info.toString());
        }
        if (mLevel == LOG_LEVEL_INFO) {
            Log.i("dora", info.toString());
        }
        if (mLevel == LOG_LEVEL_ERROR) {
            Log.e("dora", info.toString());
        }
    }
}
