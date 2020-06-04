package dora;

public abstract class Collector {

    /**
     * 收集崩溃信息。
     *
     * @param info
     */
    public abstract void collect(CrashInfo info);

    /**
     * 根据需要将收集到的崩溃信息反馈给开发者。
     *
     * @param policy
     */
    public abstract void report(CrashReportPolicy policy);
}