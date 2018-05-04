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

public class NumberUtils implements N {

    private NumberUtils() {
    }

    public static int H2D(String hexadecimal){
        return Integer.parseInt(hexadecimal, SIXTEEN);
    }

    public static int O2D(String octal){
        return Integer.parseInt(octal, EIGHT);
    }

    public static int B2D(String binary){
        return Integer.parseInt(binary, TWO);
    }

    public static String D2B(int decimal){
        return Integer.toBinaryString(decimal);
    }

    public static String D2O(int decimal){
        return Integer.toOctalString(decimal);
    }

    public static String D2H(int decimal){
        return Integer.toHexString(decimal);
    }

    public static String H2B(String hexadecimal){
        return D2B(H2D(hexadecimal));
    }

    public static String H2O(String hexadecimal){
        return D2O(H2D(hexadecimal));
    }

    public static String O2H(String octal){
        return D2H(O2D(octal));
    }

    public static String O2B(String octal){
        return D2B(O2D(octal));
    }

    public static String B2O(String binary){
        return D2O(B2D(binary));
    }

    public static String B2H(String binary){
        return D2H(B2D(binary));
    }

    public static String zeroH(String num, int requiredLength) {
        if (requiredLength < 0) {
            throw new IllegalArgumentException("requiredLength must be more than 0.");
        }
        StringBuffer sb = new StringBuffer();
        if (requiredLength > num.length()) {
            int length = requiredLength - num.length();
            for (int i=0;i<length;i++) {
                sb.append(0);
            }
        }
        return sb.append(num).toString();
    }

    public static String zeroL(String num, int requiredLength) {
        if (requiredLength < 0) {
            throw new IllegalArgumentException("requiredLength must be more than 0.");
        }
        StringBuffer sb = new StringBuffer(num);
        if (requiredLength > num.length()) {
            int length = requiredLength - num.length();
            for (int i=0;i<length;i++) {
                sb.append(0);
            }
        }
        return sb.toString();
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, max), min);
    }
}
