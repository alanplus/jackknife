package com.lwh.jackknife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

/**
 * 输入输出相关的工具类。
 */
public class IoUtils {

    /**
     * 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块。
     */
    public static final String US_ASCII = "US-ASCII";

    /**
     * ISO拉丁字母表No.1，也叫作 ISO-LATIN-1。
     */
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * 8位UCS转换格式。
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 16位UCS转换格式，Big Endian（最低地址存放高位字节）字节顺序。
     *
     */
    public static final String UTF_16BE = "UTF-16BE";

    /**
     * 16位UCS转换格式，Little-endian（最高地址存放低位字节）字节顺序。
     */
    public static final String UTF_16LE = "UTF-16LE";

    /**
     * 16位UCS 转换格式，字节顺序由可选的字节顺序标记来标识。
     */
    public static final String UTF_16 = "UTF-16";

    /**
     * 中文超大字符集。
     */
    public static final String GBK = "GBK";

    private IoUtils(){
    }

    public static String b2H(byte b){
        String H = Integer.toHexString(b & 0xFF);
        if (H.length() == 1){
            H = '0'+H;
        }
        return H;
    }

    /**
     * 字节数组转换十六进制字符串。
     *
     * @param src 字节数组。
     * @param separator 分隔符，如"："。
     * @return 如"00:AA:FF"。
     */
    public static String bs2H(byte[] src, String separator){
        StringBuffer buff = new StringBuffer();
        if (src != null && src.length > 0){
            for (int i=0;i<src.length;i++){
                int value = src[i] & 255;
                String H = NumberUtils.D2H(value);
                if (H.length() < 2){
                    buff.append(0);
                }
                buff.append(H).append(separator);
            }
            return buff.substring(0, buff.length()-1);
        }else {
            return null;
        }
    }

    /**
     * 十六进制字符串转换字节数组。
     *
     * @param H 十六进制字符串，带分隔符，如"00:AA:FF"。
     * @param separator 分隔符，如":"。
     * @return 字节数组。
     */
    public static byte[] H2bs(String H, String separator){
        if (separator != null){
            String[] HS = H.split(separator);
            byte[] bs = new byte[H.length()];
            int i = 0;
            for (String b : HS){
                bs[i++] = Integer.valueOf(b, 16).byteValue();
            }
            return bs;
        }
        throw new IllegalArgumentException("分隔符不能为空");
    }

    /**
     * 检测SD卡是否准备就绪。
     *
     * @return true表示准备就绪，false表示未准备就绪。
     */
    public static boolean checkMediaMounted() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 复制单个文件。
     *
     * @param file 一个具体的文件。
     * @param target 目标。
     * @return 是否复制成功。
     */
    private static boolean copyFile(File file, String target) {
        File targetFile = new File(target);
        if (!file.isFile() || !targetFile.isDirectory()) {
            return false;
        }
        try {
            InputStream inStream = new FileInputStream(file);
            OutputStream outStream = new BufferedOutputStream(
                    new FileOutputStream(new File(targetFile, file.getName())));
            int len;
            byte[] buf = new byte[1024];
            while ((len = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, len);
            }
            outStream.flush();
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 复制单个文件夹（带子目录）。
     *
     * @param file 文件夹。
     * @param target 目标。
     * @return 是否复制成功。
     */
    private static boolean copyFolder(File file, String target) {
        File targetFile = new File(target);
        if (!file.isDirectory() || !targetFile.isDirectory()) {
            return false;
        }
        if (file.list() != null) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                File newDirFile = new File(target + File.separator + file.getName());
                if (!newDirFile.exists()) {
                    newDirFile.mkdir();
                }
                copy(new File(file, children[i]), target + File.separator + file.getName());
            }
        }
        return true;
    }

    /**
     * 复制单个文件或文件夹。
     *
     * @param file 可能是一个文件，也可能是一个文件夹。
     * @param target 目标。
     * @return 是否复制成功。
     */
    public static boolean copy(File file, String target) {
        File targetFile = new File(target);
        if (!targetFile.isDirectory()) {
            return false;
        }
        if (file.isFile()) {
            return copyFile(file, target);
        } else {
            return copyFolder(file, target);
        }
    }

    /**
     * 删除单个文件。
     *
     * @param file 一个文件。
     * @return 是否删除成功。
     */
    private static boolean deleteFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
    }

    /**
     * 删除单个文件夹。
     *
     * @param file 一个文件夹。
     * @return 是否删除成功。
     */
    private static boolean deleteFolder(File file) {
        if (!file.isDirectory()) {
            return false;
        } else {
            if (file.list() != null) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    delete(new File(file, children[i]));
                }
            }
            return file.delete();
        }
    }

    /**
     * 删除单个文件或文件夹。
     *
     * @param file 一个文件或文件夹。
     * @return 是否删除成功。
     */
    public static boolean delete(File file) {
        if (file.isFile()) {
            return deleteFile(file);
        } else {
            return deleteFolder(file);
        }
    }

    /**
     * 剪切单个文件。
     *
     * @param file 一个文件。
     * @param target 目标。
     * @return 是否剪切成功。
     */
    private static boolean cutFile(File file, String target) {
        File targetFile = new File(target);
        if (!file.isFile() || !targetFile.isDirectory()) {
            return false;
        } else {
            copyFile(file, target);
            deleteFile(file);
            return true;
        }
    }

    /**
     * 剪切单个文件夹。
     *
     * @param file 一个文件夹。
     * @param target 目标。
     * @return 是否剪切成功。
     */
    private static boolean cutFolder(File file, String target) {
        File targetFile = new File(target);
        if (!file.isDirectory() || !targetFile.isDirectory()) {
            return false;
        } else {
            if (copyFolder(file, target)) {
                return deleteFolder(file);
            } else {
                return false;
            }
        }
    }

    /**
     * 剪切单个文件或文件夹。
     *
     * @param file 一个文件或文件夹。
     * @param target 目标。
     * @return 是否剪切成功。
     */
    public static boolean cut(File file, String target) {
        File targetFile = new File(target);
        if (!targetFile.isDirectory()) {
            return false;
        }
        if (file.isFile()) {
            return cutFile(file, target);
        } else {
            if (copy(file, target)) {
                return delete(file);
            } else {
                return false;
            }
        }
    }

    /**
     * 重命名单个文件。
     *
     * @param file 一个文件。
     * @param name 新文件名。
     * @return 是否重命名成功。
     */
    private static boolean renameFile(File file, String name) {
        if (!file.isFile()) {
            return false;
        } else {
            String parent = file.getParent();
            return file.renameTo(new File(parent, name));
        }
    }

    /**
     * 重命名单个文件夹。
     *
     * @param file 一个文件夹。
     * @param name 新文件夹名。
     * @return 是否重命名成功。
     */
    private static boolean renameFolder(File file, String name) {
        if (!file.isDirectory()) {
            return false;
        } else {
            String parent = file.getParent();
            return file.renameTo(new File(parent, name));
        }
    }

    /**
     * 重命名单个文件或文件夹。
     *
     * @param file 一个文件或文件夹。
     * @param name 新文件名或新文件夹名。
     * @return 是否重命名成功。
     */
    public static boolean rename(File file, String name) {
        if (file.isFile()) {
            return renameFile(file, name);
        } else {
            return renameFolder(file, name);
        }
    }

    /**
     * 获取机身内存（ROM）。
     *
     * @param context 上下文。
     * @return 机身内存。
     */
    public static String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获取机身可用内存（ROM）。
     *
     * @param context 上下文。
     * @return 机身可用内存。
     */
    public static String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获取文件大小。
     *
     * @param context 上下文。
     * @param file 要测试的文件。
     * @return 文件 大小。
     */
    public static String getFileSize(Context context, File file) {
        FileChannel fc = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fc = fis.getChannel();
            long size = fc.size();
            return Formatter.formatFileSize(context, size);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fc.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "<unknown>";
    }

    /**
     * 获取一个目录下的文件或文件夹数目。
     *
     * @param file 一个文件夹。
     * @return 一个目录下的文件或文件夹数目。
     */
    public static int getSubCount(File file) {
        if (file != null) {
            if (!file.isDirectory()) {
                return -1;
            } else {
                return file.list().length;
            }
        }
        throw new NullPointerException("file can\'t be null.");
    }

    /**
     * 读取指定目录文件的文件内容。
     *
     * @param fileName 文件名称。
     * @return 文件内容。
     * @throws IOException 输入输出异常。
     */
    public static String read(String fileName) throws IOException {
        FileInputStream is = new FileInputStream(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] data = baos.toByteArray();
        return new String(data);
    }

    /**
     * 获取SharedPreferences文件内容。
     *
     * @param context 上下文。
     * @param fileNameNoExt 文件名称（不带后缀名）。
     * @return 键值对集合。
     */
    public static Map<String, ?> read(Context context, String fileNameNoExt) {
        SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
        return preferences.getAll();
    }

    /**
     * 写入内容到SharedPreferences文件。
     *
     * @param context 上下文。
     * @param fileNameNoExt 文件名称（不带后缀名）。
     * @param values 需要写入的数据Map(String,Boolean,Float,Long,Integer)。
     */
    public static void write(Context context, String fileNameNoExt, Map<String, ?> values) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String, ?> entry = (Map.Entry<String, ?>) iterator.next();
                if (entry.getValue() instanceof String) {
                    editor.putString(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
                } else if (entry.getValue() instanceof Float) {
                    editor.putFloat(entry.getKey(), (Float) entry.getValue());
                } else if (entry.getValue() instanceof Long) {
                    editor.putLong(entry.getKey(), (Long) entry.getValue());
                } else if (entry.getValue() instanceof Integer) {
                    editor.putInt(entry.getKey(), (Integer) entry.getValue());
                }
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入应用程序包files目录下文件。
     *
     * @param context 上下文。
     * @param fileName 文件名称。
     * @param modeType 文件写入模式{@link Context#MODE_PRIVATE}、{@link Context#MODE_APPEND}、
     *                 {@link Context#MODE_WORLD_READABLE}、{@link Context#MODE_WORLD_WRITEABLE}。
     * @param bytes 字节数组。
     */
    public static void write(Context context, String fileName, byte[] bytes, int modeType) {
        try {
            FileOutputStream outStream = context.openFileOutput(fileName, modeType);
            outStream.write(bytes);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将byte数据写入文件。
     *
     * @param bytes 字节数组。
     * @param filePath 文件路径。
     * @throws IOException 输入输出异常。
     */
    public static void write(byte[] bytes, String filePath) throws IOException {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 将文件流数据写入文件。
     *
     * @param inputStream 输入流。
     * @param filePath 文件路径。
     * @return 文件。
     * @throws IOException 输入输出异常。
     */
    public static File write(InputStream inputStream, String filePath) throws IOException {
        OutputStream outputStream = null;
        // 在指定目录创建一个空文件并获取文件对象
        File mFile = new File(filePath);
        if (!mFile.getParentFile().exists())
            mFile.getParentFile().mkdirs();
        try {
            outputStream = new FileOutputStream(mFile);
            byte buffer[] = new byte[4 * 1024];
            int lenght = 0;
            while ((lenght = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, lenght);
            }
            outputStream.flush();
            return mFile;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 将字符编码转换成US-ASCII码。
     */
    public String toASCII(String str) throws UnsupportedEncodingException {
        return this.transcode(str, US_ASCII);
    }

    /**
     * 将字符编码转换成ISO-8859-1码。
     */
    public String toISO88591(String str) throws UnsupportedEncodingException{
        return this.transcode(str, ISO_8859_1);
    }

    /**
     * 将字符编码转换成UTF-8码。
     */
    public String toUTF8(String str) throws UnsupportedEncodingException{
        return this.transcode(str, UTF_8);
    }

    /**
     * 将字符编码转换成UTF-16BE码。
     */
    public String toUTF16BE(String str) throws UnsupportedEncodingException{
        return this.transcode(str, UTF_16BE);
    }

    /**
     * 将字符编码转换成UTF-16LE码。
     */
    public String toUTF16LE(String str) throws UnsupportedEncodingException{
        return this.transcode(str, UTF_16LE);
    }

    /**
     * 将字符编码转换成UTF-16码。
     */
    public String toUTF16(String str) throws UnsupportedEncodingException{
        return this.transcode(str, UTF_16);
    }

    /**
     * 将字符编码转换成GBK码
     *
     */
    public String toGBK(String str) throws UnsupportedEncodingException{
        return transcode(str, GBK);
    }

    /**
     * 转换字符串编码。
     *
     * @param str  待转换编码的字符串。
     * @param newCharset 目标编码。
     * @return 新编码的字符串
     * @throws UnsupportedEncodingException 不支持的编码异常。
     */
    public String transcode(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            //用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            //用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }
}
