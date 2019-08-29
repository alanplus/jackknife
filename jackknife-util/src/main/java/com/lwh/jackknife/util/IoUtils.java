/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

public class IoUtils {

    private IoUtils() {
    }

    public static String b2H(byte b) {
        String H = Integer.toHexString(b & 0xFF);
        if (H.length() == 1) {
            H = '0' + H;
        }
        return H;
    }

    public static String bs2H(byte[] src, String separator) {
        if (src == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int value = src[i] & 0xFF;
            String H = Math.D2H(value);
            if (H.length() < 2) {
                buffer.append(0);
            }
            buffer.append(H).append(separator);
        }
        return buffer.substring(0, buffer.length()-separator.length());
    }

    public static String bs2H(byte[] src) {
        return bs2H(src, "");
    }

    public static byte[] H2bs(String H, String separator) {
        if (separator != null) {
            String[] HS = H.split(separator);
            byte[] bs = new byte[HS.length];
            int i = 0;
            for (String b : HS) {
                bs[i++] = Integer.valueOf(b, 16).byteValue();
            }
            return bs;
        }
        throw new IllegalArgumentException("Separator can\'t be null.");
    }

    public static boolean checkMediaMounted() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean copyFile(File file, String target) {
        File targetFile = new File(target);
        if (!targetFile.exists() || !file.isFile() || !targetFile.isDirectory()) {
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

    private static boolean copyFolder(File file, String target) {
        File targetFile = new File(target);
        if (!targetFile.exists() || !file.isDirectory() || !targetFile.isDirectory()) {
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

    public static boolean copy(File file, String target) {
        File targetFile = new File(target);
        if (!targetFile.exists() || !targetFile.isDirectory()) {
            return false;
        }
        if (file.isFile()) {
            return copyFile(file, target);
        } else {
            return copyFolder(file, target);
        }
    }

    private static boolean deleteFile(File file) {
        return file.isFile() && file.delete();
    }

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

    public static boolean delete(File file) {
        if (file.isFile()) {
            return deleteFile(file);
        } else {
            return deleteFolder(file);
        }
    }

    private static boolean cutFile(File file, String target) {
        File targetFile = new File(target);
        if (!file.isFile() || !targetFile.exists() || !targetFile.isDirectory()) {
            return false;
        } else {
            copyFile(file, target);
            deleteFile(file);
            return true;
        }
    }

    private static boolean cutFolder(File file, String target) {
        File targetFile = new File(target);
        return !(!file.isDirectory()) || !targetFile.exists() || !targetFile.isDirectory()
                && copyFolder(file, target) && deleteFolder(file);
    }

    public static boolean cut(File file, String target) {
        File targetFile = new File(target);
        if (!targetFile.exists() || !targetFile.isDirectory()) {
            return false;
        }
        if (file.isFile()) {
            return cutFile(file, target);
        } else {
            return copyFolder(file, target) && delete(file);
        }
    }

    private static boolean renameFile(File file, String name) {
        if (!file.isFile()) {
            return false;
        } else {
            String parent = file.getParent();
            return file.renameTo(new File(parent, name));
        }
    }

    private static boolean renameFolder(File file, String name) {
        if (!file.isDirectory()) {
            return false;
        } else {
            String parent = file.getParent();
            return file.renameTo(new File(parent, name));
        }
    }

    public static boolean rename(File file, String name) {
        if (file.isFile()) {
            return renameFile(file, name);
        } else {
            return renameFolder(file, name);
        }
    }

    public static int getSubCount(File file) {
        if (file != null) {
            if (!file.isDirectory()) {
                return -1;
            } else {
                return file.list().length;
            }
        }
        throw new NullPointerException("File can\'t be null.");
    }

    public static byte[] read(File file) throws IOException {
        byte[] buffers = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream is = new FileInputStream(file);
        int len;
        while ((len = is.read(buffers)) > 0) {
            baos.write(buffers, 0, len);
        }
        byte[] fileBytes = baos.toByteArray();
        baos.close();
        is.close();
        return fileBytes;
    }

    public static String readText(String filePath) throws IOException {
        FileInputStream is = new FileInputStream(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] data = baos.toByteArray();
        return new String(data);
    }

    public static Map<String, ?> read(Context context, String fileNameNoExt) {
        SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
        return preferences.getAll();
    }

    public static void write(Context context, String fileNameNoExt, Map<String, ?> values) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext(); ) {
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
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(Context context, String fileName, byte[] bytes, int modeType) {
        try {
            FileOutputStream outStream = context.openFileOutput(fileName, modeType);
            outStream.write(bytes);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public static File write(InputStream inputStream, String filePath) {
        OutputStream outputStream = null;
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFile;
    }

    public static void createFolder(String[] dirs) {
        if (dirs != null) {
            for (String dir : dirs) {
                createFolder(dir);
            }
        }
    }

    public static void createFolder(String dir) {
        if (TextUtils.isNotEmpty(dir)) {
            File folder = new File(dir);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    public static String transferCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            byte[] bs = str.getBytes();
            return new String(bs, newCharset);
        }
        return null;
    }

    public static String getSdRoot() {
        if (checkMediaMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    public static byte[] bytes(Object obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    public static String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    public static String formatFileSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1 && size > 0) {
            return size + "B";
        } else if (size <= 0) {
            return "0B";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

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
        return "";
    }

    public static long getFolderTotalSize(File file) {
        long size = 0;
        try {
            File[] files = file.listFiles();
            int subCount;
            if (files != null) {
                subCount = files.length;
                for (int i = 0; i < subCount; i++) {
                    if (files[i].isDirectory()) {
                        size = size + getFolderTotalSize(files[i]);
                    } else {
                        size = size + files[i].length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getParentPath(String path) {
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length()-1);
        }
        int start = path.lastIndexOf(File.separator);
        return path.substring(0, start);
    }

    public static String getFileNameFromPath(String path, boolean withSuffix) {
        int start = path.lastIndexOf(File.separator) + 1;
        int end = path.lastIndexOf(".");
        if (withSuffix) {
            return path.substring(start);
        } else {
            return path.substring(start, end);
        }
    }
}
