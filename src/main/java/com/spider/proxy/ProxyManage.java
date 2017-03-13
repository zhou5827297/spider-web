package com.spider.proxy;

/**
 * 代理管理
 */
public interface ProxyManage {

    /**
     * 通知代理ip，可使用
     */
    public void noticeSuccess();

    /**
     * 通知代理ip，不可使用
     */

    public void noticeFail(String errorMessage);

    /**
     * 获取一条代理ip
     */
    public ProxyBean getProxyBean();

}
