package com.spider.config;

import java.util.ResourceBundle;

/**
 * 静态字段
 */
public class ProxyConstant {

    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle("proxy");

    /**
     * 读取超时时间
     */
    public final static int TIMEOUT_READ = Integer.parseInt(BUNDLE.getString("TIMEOUT_READ"));
    /**
     * JS执行超时时间
     */
    public final static int JS_EXECUTE_TIMEOUT_READ = Integer.parseInt(BUNDLE.getString("JS_EXECUTE_TIMEOUT_READ"));
    /**
     * 连接超时时间
     */
    public final static int TIMEOUT_CONNECT = Integer.parseInt(BUNDLE.getString("TIMEOUT_CONNECT"));
    /**
     * 代理服务器
     */
    public final static String PROXY_URL = BUNDLE.getString("PROXY_URL");

    /**
     * 获取ip的接口
     */
    public final static String PROXY_GET = PROXY_URL + BUNDLE.getString("PROXY_GET");
    /**
     * 通知成功接口
     */
    public final static String PROXY_NOTICE_SUCCESS = PROXY_URL + BUNDLE.getString("PROXY_NOTICE_SUCCESS");

    /**
     * 通知失败接口
     */
    public final static String PROXY_NOTICE_FAILED = PROXY_URL + BUNDLE.getString("PROXY_NOTICE_FAILED");

}
