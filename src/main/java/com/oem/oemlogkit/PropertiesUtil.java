package com.oem.oemlogkit;

import android.os.SystemProperties;

public class PropertiesUtil {
    public static String get(String key, String val) {
        return SystemProperties.get(key, val);
    }

    public static void set(String key, String val) {
        SystemProperties.set(key, val);
    }

    public static Boolean getBoolean(String key, Boolean bool) {
        return Boolean.valueOf(SystemProperties.getBoolean(key, bool.booleanValue()));
    }
}
