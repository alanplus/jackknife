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
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String time = simpleDateFormat.format(new Date());
            File folder = new File(path, "android-dora");
            folder.mkdirs();
            File file = new File(folder.getAbsolutePath(), "log" + time + ".txt");
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
            System.exit(0);
        }
    }
}