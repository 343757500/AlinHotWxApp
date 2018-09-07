package com.mikuwxc.autoreply.common.util;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 2017/5/8 0008 下午 4:29.
 * Desc:
 */

public class RegularUtils {
    /**
     * 验证邮箱的正则表达式
     */
    public static final String REGULAR_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 验证国内手机号码的正则表达式
     */
    public static final String REGULAR_TELEPHONE = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

    /**
     * 验证数字的正则表达式
     */
    public static final String REGULAR_NUMERIC = "[0-9]*";

    public static final String REGULAR_USERNAME = "[a-zA-Z0-9_\\u4e00-\\u9fa5]{2,39}";

    public static final String REGULAR_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$";//^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$    请填写8-20位数字加字母的密码组合

    public static String SENSITIVE_WORD = null;
    public static String SENSITIVE_WORDVOISE = null;

    /**
     * 检查字符串是否符合规范
     *
     * @param input   需要检查的字符串
     * @param regular 规则
     * @return true or false
     */
    public static String isMatchRegular(String input, String regular) {
        if (null == input || null == regular) {
            Log.i(RegularUtils.class.getSimpleName(), "传入了null值, 验证失败");
            return null;
        }
        Pattern p = Pattern.compile(regular);
        Matcher m = p.matcher(input);

        Set<String> word = new HashSet<String>();
        while(m.find()){
//                                word.append(matcher.group()).append(",");
            word.add(m.group());
        }

        if(word != null && !word.isEmpty()){
            String sensitive = StringUtils.join(word, ",");
            return sensitive;
        }

        return null;
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
