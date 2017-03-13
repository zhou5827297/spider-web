package com.spider.httpclient;

import com.spider.config.ProxyConstant;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * http连接池
 */
public class HttpPoolManage {

    private static final Logger LOG = LogManager.getLogger(HttpPoolManage.class);

    private static final int TIME_OUT = ProxyConstant.TIMEOUT_READ;

    private static final int CONNECT_TIME_OUT = ProxyConstant.TIMEOUT_CONNECT;

    private static MultiThreadedHttpConnectionManager CONNECTIONMANAGER;

    private static HttpClientParams CLIENTPARAMS;

    private static final String CONTENTTYPE_JSON = "application/json; charset=utf-8";

    private static final String CHARSET_UTF_8 = "UTF-8";

    private static HttpClient HTTPCLIENTPROXY;

    private static HttpClient HTTPCLIENTPUSH;


    static {
        init();
    }

    private static void init() {
        try {
            CONNECTIONMANAGER = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = CONNECTIONMANAGER.getParams();
            params.setDefaultMaxConnectionsPerHost(8000);
            params.setConnectionTimeout(CONNECT_TIME_OUT);
            params.setSoTimeout(TIME_OUT);
            params.setMaxTotalConnections(8000);
            params.setStaleCheckingEnabled(true);
            CLIENTPARAMS = new HttpClientParams();
            CLIENTPARAMS.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            CLIENTPARAMS.setSoTimeout(TIME_OUT);
            CLIENTPARAMS.setConnectionManagerTimeout(CONNECT_TIME_OUT);
            CLIENTPARAMS.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CHARSET_UTF_8);
            HTTPCLIENTPROXY = new HttpClient(CLIENTPARAMS, CONNECTIONMANAGER);
            HTTPCLIENTPUSH = new HttpClient(CLIENTPARAMS, CONNECTIONMANAGER);
        } catch (Exception e) {
            LOG.error("http连接池加载失败", e);
        }
    }

    /**
     * 释放连接池中连接,其实释放不了
     */
    public static void dispose(HttpClient httpClient) {
        httpClient.getHttpConnectionManager().closeIdleConnections(30000);
    }

    /**
     * 强制释放连接池中连接
     */
    public static void dispose() {
        CONNECTIONMANAGER.closeIdleConnections(30000);
    }

    /**
     * 关闭连接池
     */
    public static void shutdown() {
        CONNECTIONMANAGER.shutdown();
    }


    /**
     * 释放当前连接到连接池中
     *
     * @param httpMethod 请求对象
     */
    public static void disposeHttpClient(HttpMethod httpMethod) {
        if (httpMethod != null) {
            httpMethod.releaseConnection();
        }
    }


    /**
     * 简单发送get请求
     */
    public static String sendGet(String url) {
        GetMethod method = null;
        String response = null;
        try {
//            HttpClient httpClient = getHttpClient(null);
            method = new CustomGetMethod(url);
            method.addRequestHeader("Accept-Encoding", "gzip, deflate, sdch");
            int statusCode = HTTPCLIENTPROXY.executeMethod(method);
            if (statusCode == HttpStatus.SC_OK) {
                response = method.getResponseBodyAsString();
            }
        } catch (Exception ex) {
            LOG.error("deal http-get error", ex);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
        return response;
    }

}
