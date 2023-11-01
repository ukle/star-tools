package me.star.tools.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author: zhou_sd
 * @date: 2022/12/22
 * @description: /
 */
public class RSAUtils {

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = String.valueOf(sArray.get(key));
            if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key)
                    || "sign_type".equalsIgnoreCase(key)) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
    
    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder content = new StringBuilder();
        int index = 0;
        for (String key : keys) {
            String value = params.get(key);
            //参数不为空的按照key=value用&拼接
            if (StringUtils.isNoneBlank(key, value)) {
                content.append((index == 0 ? "" : "&")).append(key).append("=").append(value);
                index++;
            }
        }

        return content.toString();
    }
}
