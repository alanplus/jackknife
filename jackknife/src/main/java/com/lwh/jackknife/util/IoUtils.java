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
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

public class IoUtils {

    public static final String US_ASCII = "US-ASCII";

    public static final String ISO_8859_1 = "ISO-8859-1";

    public static final String UTF_8 = "UTF-8";

    public static final String UTF_16BE = "UTF-16BE";

    public static final String UTF_16LE = "UTF-16LE";

    public static final String UTF_16 = "UTF-16";

    public static final String GBK = "GBK";

    public static final String GB_2312 = "GB2312";

    private IoUtils(){
    }

    public static String b2H(byte b){
        String H = Integer.toHexString(b & 0xFF);
        if (H.length() == 1){
            H = '0'+H;
        }
        return H;
    }

    public static String bs2H(byte[] src, String separator){
        StringBuffer buff = new StringBuffer();
        if (src != null && src.length > 0){
            for (int i=0;i<src.length;i++){
                int value = src[i] & 0xFF;
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

    public static byte[] H2bs(String H, String separator){
        if (separator != null){
            String[] HS = H.split(separator);
            byte[] bs = new byte[HS.length];
            int i = 0;
            for (String b : HS){
                bs[i++] = Integer.valueOf(b, 16).byteValue();
            }
            return bs;
        }
        throw new IllegalArgumentException("分隔符不能为空");
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

    private static boolean deleteFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
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
        if (!file.isFile() || !targetFile.isDirectory()) {
            return false;
        } else {
            copyFile(file, target);
            deleteFile(file);
            return true;
        }
    }

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

    public static Map<String, ?> read(Context context, String fileNameNoExt) {
        SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
        return preferences.getAll();
    }

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

    public String toASCII(String str) throws UnsupportedEncodingException {
        return this.transcode(str, US_ASCII);
    }

    public String toISO88591(String str) throws UnsupportedEncodingException {
        return this.transcode(str, ISO_8859_1);
    }

    public String toUTF8(String str) throws UnsupportedEncodingException {
        return this.transcode(str, UTF_8);
    }

    public String toUTF16BE(String str) throws UnsupportedEncodingException {
        return this.transcode(str, UTF_16BE);
    }

    public String toUTF16LE(String str) throws UnsupportedEncodingException {
        return this.transcode(str, UTF_16LE);
    }

    public String toUTF16(String str) throws UnsupportedEncodingException {
        return this.transcode(str, UTF_16);
    }

    public String toGBK(String str) throws UnsupportedEncodingException {
        return transcode(str, GBK);
    }

    public String toGB2312(String str) throws UnsupportedEncodingException {
        return transcode(str, GB_2312);
    }

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

    public static void createFolder(String[] dirs){
        if (dirs != null){
            for (String dir:dirs){
                createFolder(dir);
            }
        }
    }

    public static void createFolder(String dir){
        if (TextUtils.isNotEmpty(dir)){
            File folder = new File(dir);
            if (!folder.exists()){
                folder.mkdirs();
            }
        }
    }

    public static String getSdRoot(){
        if (checkMediaMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    public static byte[] bytes(Object obj){
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
}
