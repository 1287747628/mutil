package com.custom.mutil.http;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

public class HttpHandler {

    /**
     * 连接超时时间，由bean factory设置，缺省为5秒钟
     */
    private int defaultConnectionTimeout = 5000;

    /**
     * 回应超时时间, 由bean factory设置，缺省为30秒钟
     */
    private int defaultSoTimeout = 30000;

    /**
     * 闲置连接超时时间, 由bean factory设置，缺省为60秒钟
     */
    private int defaultIdleConnTimeout = 60000;

    /**
     * 默认每台主机允许的最大连接数
     */
    private int defaultMaxConnPerHost = 50;

    /**
     * 默认最大连接数
     */
    private int defaultMaxTotalConn = 200;

    /**
     * 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒
     */
    private long defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager connectionManager;

    private static class HttpHandlerHolder {
        public final static HttpHandler instance = new HttpHandler();
    }

    public static HttpHandler getInstance() {
        return HttpHandlerHolder.instance;
    }

    /**
     * 私有的构造方法
     */
    private HttpHandler() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);
        //
        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);
        ict.start();
    }

    public void setMaxConnPerHost(int maxConnPerHost) {
        defaultMaxConnPerHost = maxConnPerHost;
    }

    public void setMaxTotalConn(int maxTotalConn) {
        defaultMaxTotalConn = maxTotalConn;
    }

    public void setIdleConnTimeout(int idleConnTimeout) {
        defaultIdleConnTimeout = idleConnTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        defaultConnectionTimeout = connectionTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        defaultSoTimeout = soTimeout;
    }

    public HttpConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * 执行请求
     *
     * @param request 请求参数
     */
    public static HttpResponse execute(HttpRequest request) {

    }

}
