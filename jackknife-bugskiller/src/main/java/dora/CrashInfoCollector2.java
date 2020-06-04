package dora;

public class CrashInfoCollector2 extends Collector {

    CrashInfo mInfo;

    @Override
    public void collect(CrashInfo info) {
        mInfo = info;
    }

    @Override
    public void report(CrashReportPolicy policy) {
        if (mInfo != null) {
            policy.report(mInfo);
        }
    }
}
