/*
 * Copyright (C) 2019 The JackKnife Open Source Project
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

package com.lwh.jackknife.av.util;

public class EffectUtils {

    /**
     * 音效的类型：正常。
     */
    public static final int MODE_NORMAL = 0x0;

    /**
     * 音效的类型：萝莉。
     */
    public static final int MODE_LUOLI = 0x1;

    /**
     * 音效的类型：大叔。
     */
    public static final int MODE_DASHU = 0x2;

    /**
     * 音效的类型：惊悚。
     */
    public static final int MODE_JINGSONG = 0x3;

    /**
     * 音效的类型：搞怪。
     */
    public static final int MODE_GAOGUAI = 0x4;

    /**
     * 音效的类型：空灵。
     */
    public static final int MODE_KONGLING = 0x5;

    /**
     * 音效处理。
     *
     * @param path 需要变声的原音频文件
     * @param type 音效的类型
     */
    public native static void fix(String path, int type);

    static {
        System.loadLibrary("fmodL");
        System.loadLibrary("fmod");
        System.loadLibrary("jknfav");
    }
}
