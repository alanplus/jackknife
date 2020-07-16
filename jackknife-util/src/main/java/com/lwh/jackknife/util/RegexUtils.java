/*
 * Copyright (C) 2018 The JackKnife Open Source Project
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {

    private static final String REGEX_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";

    private static final String REGEX_URL = "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";

    private static final String REGEX_COLOR_VALUE = "^#[a-fA-F0-9]{3,6,8}$";

    private static final String REGEX_IPV4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";

    private RegexUtils() {
    }

    public static boolean isEmail(String value) {
        return match(REGEX_EMAIL, value);
    }

    public static boolean isUrl(String value) {
        return match(REGEX_URL, value);
    }

    public static boolean isColorValue(String value) {
        return match(REGEX_COLOR_VALUE, value);
    }

    public static boolean isIPv4(String value) {
        return match(REGEX_IPV4, value);
    }

    public static boolean match(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
