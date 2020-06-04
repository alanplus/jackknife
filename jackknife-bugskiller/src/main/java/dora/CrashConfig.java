package dora;

import android.content.Context;

public class CrashConfig {

    CrashReportPolicy policy;
    CrashInfo info;

    public static class Builder {

        CrashReportPolicy policy = new StoragePolicy();
        CrashInfo info;
        Context context;

        public Builder(Context context) {
            this.context = context;
            this.info = new CrashInfo(context);
        }

        public Builder crashReportPolicy(CrashReportPolicy policy) {
            this.policy = policy;
            return this;
        }

        public Builder crashInfo(CrashInfo info) {
            this.info = info;
            return this;
        }

        public CrashConfig build() {
            CrashConfig config = new CrashConfig();
            Builder builder = new Builder(context);
            policy = builder.policy;
            info = builder.info;
            DoraUncaughtExceptionHandler.getInstance().init(context, config);
            return config;
        }
    }
}
