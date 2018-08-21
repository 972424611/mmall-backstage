package com.aekc.mmall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Random;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private final static String[] WORD = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n", "p", "q",
            "r", "s", "t", "u", "v", "w", "x",
            "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"
    };

    private final static String[] NUM = {
            "2", "3", "4", "5", "6", "7", "8", "9"
    };

    public static String randomPassword() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        boolean flag = false;
        int length = random.nextInt(3) + 8;
        for(int i = 0; i < length; i++) {
            if(flag) {
                stringBuilder.append(NUM[random.nextInt(NUM.length)]);
            } else {
                stringBuilder.append(WORD[random.nextInt(WORD.length)]);
            }
            flag = !flag;
        }
        return stringBuilder.toString();
    }

    public static String encrypt(String s) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("generate md5 error, {}", s, e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(encrypt("123456"));
    }
}
