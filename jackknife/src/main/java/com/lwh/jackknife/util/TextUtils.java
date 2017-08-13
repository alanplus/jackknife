package com.lwh.jackknife.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private TextUtils(){
    }

    /**
     * 检查字符串是否为空或为空字符串。
     *
     * @param str 需要检测的字符串。
     * @return 是否为空或为空字符串。
     */
    public static boolean isEmpty(CharSequence str) {
        return android.text.TextUtils.isEmpty(str);
    }

    /**
     * 检查字符串是否不为空并且不为空字符串。
     *
     * @param str 需要检测的字符串。
     * @return 是否不为空并且不为空字符串。
     */
    public static boolean isNotEmpty(CharSequence str){
        return !isEmpty(str);
    }

    /**
     * 判断两个字符串是否相等。
     *
     * @param lhss 左手边的字符串。
     * @param rhss 右手边的字符串。
     * @return 是否相等。
     */
    public static boolean isEqualTo(String lhss, String rhss){
        return android.text.TextUtils.equals(lhss, rhss);
    }

    /**
     * 判断两个字符串是否不相等。
     *
     * @param lhss 左手边的字符串。
     * @param rhss 右手边的字符串。
     * @return 是否不相等。
     */
    public static boolean isNotEqualTo(String lhss, String rhss){
        return !isEqualTo(lhss, rhss);
    }

    /**
     * 获取32位的随机字符串。
     *
     * @return 32位的随机字符串。
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }

    /**
     * 读取文本文件的内容，比如txt文件。
     *
     * @param file 必须为文本文件。
     * @return 字符串。
     * @throws IOException 输入输出异常。
     */
    public static String getText(File file, String charsetName) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, charsetName));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 以UTF-8编码读取文本文件的内容，比如txt文件。
     *
     * @param file 必须为文本文件。
     * @return 字符串。
     * @throws IOException 输入输出异常。
     */
    public static String getText(File file) throws IOException{
        return getText(file, "UTF-8");
    }

    /**
     * 检测某个字符串是否匹配某个正则表达式。
     *
     * @param text 要检测的字符串。
     * @param regex 用什么正则表达式来检测。
     * @return 是否匹配。
     */
    public static boolean match(String text, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(text);
        if (matcher.matches()){
            return true;
        }
        return false;
    }
}
