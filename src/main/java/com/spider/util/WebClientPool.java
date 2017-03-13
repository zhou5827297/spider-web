package com.spider.util;

import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * 浏览器池
 */
public class WebClientPool {

    private static final WebClientPool INSTANCE = new WebClientPool();
    private final GenericObjectPool clientPool = new GenericObjectPool();
    private final WebClientFactory webClientFactory = new WebClientFactory();


    public WebClientPool() {
        clientPool.setMaxWait(10000);
        clientPool.setMaxActive(20);
        clientPool.setMaxIdle(5);
        clientPool.setMinIdle(3);
        clientPool.setFactory(new PoolableObjectFactory() {
            @Override
            public Object makeObject() throws Exception {
                WebClient client = webClientFactory.getWebClient(null);
                return client;
            }

            @Override
            public void destroyObject(Object obj) throws Exception {
                WebClient client = (WebClient) obj;
                client.closeAllWindows();
                client = null;
            }

            @Override
            public boolean validateObject(Object obj) {
                return false;
            }

            @Override
            public void activateObject(Object obj) throws Exception {

            }

            @Override
            public void passivateObject(Object obj) throws Exception {

            }
        });
    }

    /**
     * 单例获取
     */
    public static WebClientPool getInstance() {
        return INSTANCE;
    }

    /**
     * 从池中获取一个浏览器
     */
    public WebClient getClient() throws Exception {
        WebClient webClient = (WebClient) this.clientPool.borrowObject();
        return webClient;
    }

    /**
     * 归还到池中
     */
    public void returnClient(WebClient webClient) throws Exception {
        if (webClient != null) {
            this.clientPool.returnObject(webClient);
        }
    }

}
