package me.star.tools.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;

public class HashUtils {

    /**
     * 随机字母数字
     *
     * @param length 长度
     * @return 随机字母数字
     */
    public static String getCharAndNum(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) { // 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 两次md5加salt散列
     *
     * @param source 源
     * @param salt   salt
     * @return 散列值
     */
    public static String md5HashAddSalt(String source, String salt) {
        return md5Hash(md5Hash(source) + salt).toUpperCase();
    }

    /**
     * md5散列，使用apache commons包实现，返回散列后的大写字母
     *
     * @param source 源
     * @return 散列值
     */
    public static String md5Hash(String source) {
        return DigestUtils.md5Hex(source).toUpperCase();
    }
}