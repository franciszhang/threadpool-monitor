package com.frank.threadpool.monitor.agent.beat.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author francis
 * @version 2021-07-20
 */
public class SimpleHttpRequest {
    private InetSocketAddress socketAddress;
    private String requestPath;
    private int soTimeout = 3000;
    private Map<String, String> params;
    private Charset charset = StandardCharsets.UTF_8;

    public SimpleHttpRequest(InetSocketAddress socketAddress, String requestPath) {
        this.socketAddress = socketAddress;
        this.requestPath = requestPath;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public SimpleHttpRequest setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public SimpleHttpRequest setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public SimpleHttpRequest setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public SimpleHttpRequest setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public Charset getCharset() {
        return charset;
    }

    public SimpleHttpRequest setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public SimpleHttpRequest addParam(String key, String value) {
        if (key == null || "".equals(key)) {
            throw new IllegalArgumentException("Parameter key cannot be empty");
        }
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }
}
