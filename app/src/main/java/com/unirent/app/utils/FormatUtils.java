package com.unirent.app.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    public static String money(long v) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(v);
    }
}
