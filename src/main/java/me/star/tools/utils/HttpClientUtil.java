package me.star.tools.utils;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author Star Chou
 * @create 2021/12/25 22:10
 */
@Slf4j
public class HttpClientUtil {

    private static class SingletonHolder {
        private final static HttpClientUtil INSTANCE = new HttpClientUtil();
    }

    private HttpClientUtil() {
    }

    public static HttpClientUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //get形式
    public static String getMethod(String url, String token) {
        CharsetHandler handler = new CharsetHandler("UTF-8");
        CloseableHttpClient client = null;
        try {
            HttpGet httpget = new HttpGet(new URI(url));
            if (StringUtils.isNotBlank(token)) {
                httpget.addHeader("Authorization", token);
            }
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            client = httpClientBuilder.build();
            client = (CloseableHttpClient) wrapClient(client);
            return client.execute(httpget, handler);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //HttpClient模仿Basic Auth接口
    public static String httpClientWithBasicAuth(String userName, String password, String uri, Map<String, String> paramMap) {
        String result = "";
        try {
            // 创建HttpClientBuilder
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
            HttpPost httpPost = new HttpPost(uri);
            //添加http头信息
            httpPost.addHeader("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((userName + ":" + password).getBytes()));
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            paramMap.forEach((k, v) -> {
                builder.addPart(k, new StringBody(v, ContentType.MULTIPART_FORM_DATA));
            });
            HttpEntity postEntity = builder.build();
            httpPost.setEntity(postEntity);
            HttpResponse httpResponse;
            HttpEntity entity;
            try {
                httpResponse = closeableHttpClient.execute(httpPost);
                entity = httpResponse.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 关闭连接
            closeableHttpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    //适用于post请求并传送form-data数据（同样适用于post的Raw类型的application-json格式）
    public static String postParams(String url, Map<String, String> params) {
        SSLContext sslcontext = createIgnoreVerifySSL();
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        //创建自定义的httpclient对象
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse res = null;
        try {
            List<NameValuePair> nvps = new ArrayList<>();
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            res = client.execute(post);
            HttpEntity entity = res.getEntity();
            return EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert res != null;
                res.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //适用于post传对象（对象里有list）
    public static String postParams(String url, Object params) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse res = null;
        log.info("post发送请求，地址：{}，参数：{}", url, JSONUtil.toJsonStr(params));
        try {
            // 在传送复杂嵌套对象时，要把对象转成json字符串
            StringEntity requestEntity = new StringEntity(JSONUtil.toJsonStr(params), "utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setEntity(requestEntity);
            httpPost.addHeader("Content-Type", "application/json");
            res = httpclient.execute(httpPost);
            HttpEntity entity = res.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            log.info("post请求返回结果：{}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert res != null;
                res.close();
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String postMethod(String url, String params, String contentType, String head) {
        //创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient client = httpClientBuilder.build();
        client = (CloseableHttpClient) wrapClient(client);

        HttpPost post = new HttpPost(url);
        CloseableHttpResponse res = null;
        try {
            StringEntity s = new StringEntity(params, "UTF-8");
            if (StringUtils.isBlank(contentType)) {
                s.setContentType("application/json");
            }
            s.setContentType(contentType);
            s.setContentEncoding("utf-8");
            post.setEntity(s);

            if (StringUtils.isNotBlank(head)) {
                post.addHeader("Authorization", head);
            }

            assert client != null;
            res = client.execute(post);
            HttpEntity entity = res.getEntity();
            return EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert res != null;
                res.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String postMethod(String urlStr, String xmlInfo) {
        String line1 = "";
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            //con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con
                    .getOutputStream());
            out.write(new String(xmlInfo.getBytes("utf-8")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con
                    .getInputStream()));
            String line = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                line1 += line;
            }
            return new String(line1.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class CharsetHandler implements ResponseHandler<String> {
        private final String charset;

        public CharsetHandler(String charset) {
            this.charset = charset;
        }

        public String handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                if (!StringUtils.isBlank(charset)) {
                    return EntityUtils.toString(entity, charset);
                } else {
                    return EntityUtils.toString(entity);
                }
            } else {
                return null;
            }
        }
    }

    private static HttpClient wrapClient(HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, new String[]{"TLSv1"}, null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();

        } catch (Exception ex) {
            return null;
        }
    }

    public static SSLContext createIgnoreVerifySSL() {
        try {
            SSLContext sc = SSLContext.getInstance("SSLv3");

            // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sc.init(null, new TrustManager[]{trustManager}, null);
            return sc;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
