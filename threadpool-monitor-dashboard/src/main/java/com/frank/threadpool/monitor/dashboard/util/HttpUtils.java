package com.frank.threadpool.monitor.dashboard.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author francis
 * @version 2021-10-20
 */
public class HttpUtils {
    private static CloseableHttpClient httpClient;

    static {
        httpClient = HttpClientBuilder.create().build();
    }

    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String post(String url, String jsonStr) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(jsonStr, "utf-8");
        stringEntity.setContentType(ContentType.APPLICATION_JSON.toString());
        httpPost.setEntity(stringEntity);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
