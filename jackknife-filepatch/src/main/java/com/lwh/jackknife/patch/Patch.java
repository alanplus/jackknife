package com.lwh.jackknife.patch;

public class Patch {

    /**
     * 差分新旧文件生成差异补丁。
     *
     * @param oldFilePath 旧文件路径（文件存在）
     * @param newFilePath 新文件路径（文件存在）
     * @param patchPath 补丁路径（生成文件的输出路径）
     * @return
     */
    public native static boolean diffPatch(String oldFilePath, String newFilePath, String patchPath);

    /**
     * 合并旧文件和补丁文件生成原文件。
     *
     * @param oldFilePath 旧文件路径（文件存在）
     * @param newFilePath 新文件路径（生成文件的输出路径）
     * @param patchPath 补丁路径（文件存在）
     * @return
     */
    public native static boolean fixPatch(String oldFilePath, String newFilePath, String patchPath);

    static {
        System.loadLibrary("jknfpatch");
    }
}
