package me.star.tools.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RequestUtils {

    /**
     * 获取远程访问IP地址
     *
     * @return IP
     */
    public static String getRequestIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            String[] ipArray = ip.split(",");
            if (ipArray.length > 1) {
                ip = ipArray[0];
            }
        }
        return ip;
    }

    /**
     * 获取请求参数,同名参数只打印第一个
     *
     * @param request request
     * @return 请求参数
     */
    public static Map<String, String> getRequestParameterMap(HttpServletRequest request) {
        Map properties = request.getParameterMap();
        Iterator it = properties.entrySet().iterator();
        Map<String, String> paramMap = new HashMap<>();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String key = (String) entry.getKey();
            Object objectValue = entry.getValue();
            String value;
            if (null == objectValue) {
                value = "";
            } else if (objectValue instanceof String[]) {
                //只打印同名参数的第一个值
                String[] values = (String[]) objectValue;
                value = values[0];
            } else {
                value = objectValue.toString();
            }
            paramMap.put(key, value);
        }
        return paramMap;
    }

    /**
     * 获取所有请求参数,同名参数的值用-拼接
     *
     * @param request request
     * @return 所有请求参数
     */
    public static Map<String, String> getRequestAllParameterMap(HttpServletRequest request) {
        Map properties = request.getParameterMap();
        Iterator it = properties.entrySet().iterator();
        Map<String, String> paramMap = new HashMap<>();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String key = (String) entry.getKey();
            Object objectValue = entry.getValue();
            String value = "";
            if (null == objectValue) {
                value = "";
            } else if (objectValue instanceof String[]) {
                String[] values = (String[]) objectValue;
                int valuesLength = values.length;
                //同名参数的值用-拼接
                for (int i = 0; i < valuesLength; i++) {
                    if (i == (valuesLength - 1)) {
                        value = value + values[i];
                    } else {
                        value = value + values[i] + "-";
                    }
                }
            } else {
                value = objectValue.toString();
            }
            paramMap.put(key, value);
        }
        return paramMap;
    }

    /**
     * 获取请求数据
     *
     * @param request request
     * @return 请求数据
     */
    public static String getRequestData(HttpServletRequest request) {
        StringBuilder requestData = new StringBuilder();
        requestData.append("requestIP:").append(getRequestIP(request));
        requestData.append("--requestURL:").append(request.getRequestURL());
        requestData.append("--referer:").append(request.getHeader("referer"));
        //避免日志过长，不打印图片内容
        Map<String, String> paramMap = getRequestAllParameterMap(request);
        requestData.append("--allParameterMap:").append(paramMap);
        return requestData.toString();
    }
}