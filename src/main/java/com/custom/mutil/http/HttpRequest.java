package com.custom.mutil.http;

import org.apache.commons.httpclient.NameValuePair;

import java.util.Map;

public class HttpRequest {

    /**
     * HTTP GET method
     */
    public static final String METHOD_GET = "GET";

    /**
     * HTTP POST method
     */
    public static final String METHOD_POST = "POST";

    public static final String RESUTL_TYPE_BYTES = "BYTES";

    public static final String RESUTL_TYPE_STRING = "STRING";

    /**
     * 待请求的url
     */
    private String url = null;

    /**
     * 默认的请求方式
     */
    private String method = METHOD_POST;

    private int timeout = 0;

    private int connectionTimeout = 0;

    /**
     * Post方式发送xml
     */
    private String data = null;

    /**
     * Get方式请求时对应的参数
     */
    private String queryString = null;

    /**
     * 默认的请求编码方式
     */
    private String charset = "UTF-8";

    /**
     * 请求发起方的ip地址
     */
    private String clientIp;

    /**
     * 数据格式: "application/json"
     */
    private String dataFormat = "application/json";

    private NameValuePair[] parameters = null;

    private Map<String, String> headParameters;

    public NameValuePair[] getParameters() {
        return parameters;
    }

    public void setParameters(NameValuePair[] parameters) {
        this.parameters = parameters;
    }

    /**
     * 重试开关
     */
    private boolean retryFlag = false;

    public boolean getRetryFlag() {
        return retryFlag;
    }

    public void setRetryFlag(boolean retryFlag) {
        this.retryFlag = retryFlag;
    }

    /**
     * 首次执行失败后的重试次数
     */
    private int retryCount = 2;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 请求返回的方式
     */
    private String resultType = HttpRequest.RESUTL_TYPE_STRING;

    public HttpRequest(String resultType) {
        super();
        this.resultType = resultType;
    }

    public HttpRequest(String url, String body) {
        super();
        this.url = url;
        this.data = body;
    }

    private boolean authFlag;
    private String authUserName;
    private String authPassword;

    /**
     * @return Returns the clientIp.
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp The clientIp to set.
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return Returns the charset.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset The charset to set.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Map<String, String> getHeadParameters() {
        return headParameters;
    }

    public void setHeadParameters(Map<String, String> headParameters) {
        this.headParameters = headParameters;
    }

    public boolean isAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(boolean authFlag) {
        this.authFlag = authFlag;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

}
