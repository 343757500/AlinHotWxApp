package com.mikuwxc.autoreply.wcutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchUtil {
    public static String getValue(String str, String str2) {
        String str3 = null;
        if (OtherUtils.isEmpty(str)) {
            return "";
        }
        try {
            Matcher matcher = Pattern.compile(str2).matcher(str);
            while (matcher.find()) {
                str3 = matcher.group(1);
            }
            return str3 != null ? str3.trim() : str3;
        } catch (Throwable e) {
            //ThrowableExtension.printStackTrace(e);
            return null;
        }
    }
}