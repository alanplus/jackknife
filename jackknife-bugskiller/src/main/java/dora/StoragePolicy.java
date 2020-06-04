package dora;

import android.os.Environment;
import android.os.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoragePolicy implements CrashReportPolicy {

    @Override
    public void report(CrashInfo info) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //彻底退出
            Process.killProcess(Process.myPid());
            return;
        }
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String time = simpleDateFormat.format(new Date());
            //找到文件夹路径，创建dora文件夹
            File f = new File(path, "dora");
            f.mkdirs();
            File file = new File(f.getAbsolutePath(), "dora" + time + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] buffer = info.toString().trim().getBytes();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buffer, 0, buffer.length);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Process.killProcess(Process.myPid());
        }
    }
}