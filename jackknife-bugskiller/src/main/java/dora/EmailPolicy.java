package dora;

public class EmailPolicy implements CrashReportPolicy {

    @Override
    public void report(CrashInfo info) {
        //1.可使用POP3协议
        //2.可使用iMap协议
    }
}