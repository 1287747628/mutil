package com.custom.mutil;

public class StringUtil {

    public static final boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }

    public static final boolean isNotEmpty(final String s) {
        return !isEmpty(s);
    }

}
