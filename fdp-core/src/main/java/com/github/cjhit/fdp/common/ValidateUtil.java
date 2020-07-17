package com.github.cjhit.fdp.common;

import java.util.regex.Pattern;

/**
 * 文件名：ValidateUtil.java
 * 说明：
 * 作者：水哥
 * 创建时间：2020-06-20
 */
public class ValidateUtil {
    public final static String REG_PHONE = "^[1][3-9][0-9]{9}$";
    public final static String REG_CHINESE_NAME = "^[\\u4e00-\\u9fa5.·\\u36c3\\u4DAE]{2,20}$";

    public static boolean isPhone(String phone) {
        return Pattern.matches(REG_PHONE, phone);
    }

    public static boolean isChineseName(String nickName) {
        return Pattern.matches(REG_CHINESE_NAME, nickName);
    }

    /**
     * 小数点验证正则
     *
     * @param decimalCount
     * @return
     */
    private static String getDecimalValidateReg(int decimalCount) {
        if (decimalCount == 1) {
            return "^(([1-9][0-9]*)|(([0]\\.\\d|[1-9][0-9]*\\.\\d)))$";
        }
        return "^(([1-9][0-9]*)|(([0]\\.\\d{1," + decimalCount + "}|[1-9][0-9]*\\.\\d{1," + decimalCount + "})))$";
    }

    public static boolean isDecimalValid(float val, int decimalSize) {
        String reg = getDecimalValidateReg(decimalSize);
        return Pattern.matches(reg, String.valueOf(val));
    }


    public static void main(String[] args) {
        System.out.println(isPhone("13800000001"));
        System.out.println(isChineseName("努尔麦麦提·卡卡西"));
        System.out.println(isDecimalValid(1f, 2));
        System.out.println(isDecimalValid(0.4f, 2));
        System.out.println(isDecimalValid(3.44f, 2));
        System.out.println(isDecimalValid(3.441f, 2));
        System.out.println(isDecimalValid(0.4111f, 2));
        System.out.println(isDecimalValid(1.2323232f, 2));


    }
}
