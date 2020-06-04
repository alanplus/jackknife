package dora;

public class CrashInfoCollector2 extends Collector {

    CrashInfo mInfo;

    @Override
    public void collect(CrashInfo info) {
        mInfo = info;
    }

    @Override
    public void report(Thread thread, CrashReportPolicy policy) {
        if (mInfo != null) {
            mInfo.setThread(thread);
            policy.report(mInfo);
        }
    }
}
