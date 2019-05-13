package com.custom.mutil.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);

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
        private final static HttpHandler instance = new HttpHandler();
    }

    private static HttpHandler getInstance() {
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

    public void setDefaultHttpConnectionManagerTimeout(long defaultHttpConnectionManagerTimeout) {
        this.defaultHttpConnectionManagerTimeout = defaultHttpConnectionManagerTimeout;
    }

    /**
     * 执行请求
     *
     * @param request 请求参数
     */
    public static HttpResponse execute(HttpRequest request) {
        return HttpHandler.getInstance().executeRequest(request);
    }

    private HttpResponse executeRequest(HttpRequest request) {
        try {
            HttpClient httpclient = new HttpClient(connectionManager);
            // 设置连接超时
            int connectionTimeout = defaultConnectionTimeout;
            if (request.getConnectionTimeout() > 0) {
                connectionTimeout = request.getConnectionTimeout();
            }
            httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
            // 设置回应超时
            int soTimeout = defaultSoTimeout;
            if (request.getTimeout() > 0) {
                soTimeout = request.getTimeout();
            }
            httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

            // 设置等待ConnectionManager释放connection的时间
            httpclient.getParams().setConnectionManagerTimeout(defaultHttpConnectionManagerTimeout);

            String charset = request.getCharset();
            charset = charset == null ? "UTF-8" : charset;
            request.setCharset(charset);
            //
            if (request.getMethod().equals(HttpRequest.METHOD_GET)) {
                return this.doGet(httpclient, request);
            } else {
                return this.doPost(httpclient, request);
            }
        } catch (Exception e) {
            logger.error("", e);
            return new HttpResponse(HttpResponse.STATUS_500);
        }
    }

    private HttpResponse doPost(HttpClient httpclient, HttpRequest request) throws Exception {
        PostMethod method = null;
        HttpResponse response = new HttpResponse();
        try {
            method = new PostMethod(request.getUrl());
            // 设置Http Header中的User-Agent属性
            method.addRequestHeader("User-Agent", "Mozilla/4.0");
            if (request.getParameters() != null) {
                method.addParameters(request.getParameters());
            } else {
                method.setRequestEntity(new StringRequestEntity(request.getData(), request.getDataFormat(), request.getCharset()));
            }
            //
            httpclient.executeMethod(method);
            response.setStatusCode(method.getStatusLine().getStatusCode());
            if (HttpRequest.RESUTL_TYPE_STRING.equals(request.getResultType())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                String ts = stringBuffer.toString();
                response.setStringResult(ts);
            } else if (HttpRequest.RESUTL_TYPE_BYTES.equals(request.getResultType())) {
                response.setByteResult(method.getResponseBody());
            }
            response.setResponseHeaders(method.getResponseHeaders());
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
        return response;
    }

    private HttpResponse doGet(HttpClient httpclient, HttpRequest request) throws Exception {
        GetMethod method = null;
        HttpResponse response = new HttpResponse();
        try {
            method = new GetMethod(request.getUrl());
            // 设置Http Header中的User-Agent属性
            method.addRequestHeader("User-Agent", "Mozilla/4.0");
            method.getParams().setCredentialCharset(request.getCharset());
            method.setQueryString(request.getQueryString());
            //
            httpclient.executeMethod(method);
            response.setStatusCode(method.getStatusLine().getStatusCode());
            if (HttpRequest.RESUTL_TYPE_STRING.equals(request.getResultType())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "utf-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                String ts = stringBuffer.toString();
                response.setStringResult(ts);
            } else if (HttpRequest.RESUTL_TYPE_BYTES.equals(request.getResultType())) {
                response.setByteResult(method.getResponseBody());
            }
            response.setResponseHeaders(method.getResponseHeaders());
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
        return response;
    }

}
