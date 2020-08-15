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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public final class IntentUtils {

    private IntentUtils() {
    }

    public static boolean hasExtra(Intent intent, String key) {
        return intent.hasExtra(key);
    }

    public static boolean getBooleanExtra(Intent intent, String name, boolean defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getBooleanExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static byte getByteExtra(Intent intent, String name, byte defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getByteExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static short getShortExtra(Intent intent, String name, short defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getShortExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static char getCharExtra(Intent intent, String name, char defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getCharExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static int getIntExtra(Intent intent, String name, int defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getIntExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public long getLongExtra(Intent intent, String name, long defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getLongExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static float getFloatExtra(Intent intent, String name, float defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getFloatExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static double getDoubleExtra(Intent intent, String name, double defaultValue) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return defaultValue;
            }
            return intent.getDoubleExtra(name, defaultValue);
        }
        return defaultValue;
    }

    public static String getStringExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getStringExtra(name);
        }
        return null;
    }

    public static CharSequence getCharSequenceExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getCharSequenceExtra(name);
        }
        return null;
    }

    public static <T extends Parcelable> T getParcelableExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getParcelableExtra(name);
        }
        return null;
    }

    public static Parcelable[] getParcelableArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getParcelableArrayExtra(name);
        }
        return null;
    }

    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getParcelableArrayListExtra(name);
        }
        return null;
    }

    public static Serializable getSerializableExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getSerializableExtra(name);
        }
        return null;
    }

    public static ArrayList<Integer> getIntegerArrayListExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getIntegerArrayListExtra(name);
        }
        return null;
    }

    public ArrayList<String> getStringArrayListExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getStringArrayListExtra(name);
        }
        return null;
    }

    public static ArrayList<CharSequence> getCharSequenceArrayListExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getCharSequenceArrayListExtra(name);
        }
        return null;
    }

    public static String[] getStringArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getStringArrayExtra(name);
        }
        return null;
    }

    public static boolean[] getBooleanArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getBooleanArrayExtra(name);
        }
        return null;
    }

    public static byte[] getByteArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getByteArrayExtra(name);
        }
        return null;
    }

    public static short[] getShortArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getShortArrayExtra(name);
        }
        return null;
    }

    public static char[] getCharArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getCharArrayExtra(name);
        }
        return null;
    }

    public static int[] getIntArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getIntArrayExtra(name);
        }
        return null;
    }

    public static long[] getLongArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getLongArrayExtra(name);
        }
        return null;
    }

    public static float[] getFloatArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getFloatArrayExtra(name);
        }
        return null;
    }

    public static double[] getDoubleArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getDoubleArrayExtra(name);
        }
        return null;
    }

    public static CharSequence[] getCharSequenceArrayExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getCharSequenceArrayExtra(name);
        }
        return null;
    }

    public static Bundle getBundleExtra(Intent intent, String name) {
        if (intent != null) {
            if (!hasExtra(intent, name)) {
                return null;
            }
            return intent.getBundleExtra(name);
        }
        return null;
    }
}
