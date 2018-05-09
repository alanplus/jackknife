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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A tool that can transform arrays and collections.
 */
public class AC {

    private static AC sInstance;

    private AC() {
    }

    public static AC getInstance() {
        if (sInstance == null) {
            synchronized (AC.class) {
                if (sInstance == null) {
                    sInstance = new AC();
                }
            }
        }
        return sInstance;
    }

    /**
     * Converts an array into a collection.
     */
    public <T> List<T> toC(T[] array) {
        return Arrays.asList(array);
    }

    /**
     * Converts a collection into an array.
     */
    public <T> T[] toA(Collection<T> collection) {
        return (T[]) collection.toArray();
    }
}
