package com.prayerlaputa.rpcfx.client.transport.httpclient;

import com.alibaba.fastjson.JSON;
import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.client.transport.AbstractTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
@Slf4j
public class AsyncHttpClientTransport extends AbstractTransport {

    private CloseableHttpAsyncClient httpClient;

    public AsyncHttpClientTransport(CloseableHttpAsyncClient httpClient) {
        this.httpClient = httpClient;
    }

    private String respJson = null;

    @Override
    public RpcfxResponse postCall(RpcfxRequest req, String url) throws Exception {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: " + reqJson);

        final HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpPost.setHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        //给httpPost设置JSON格式的参数
        StringEntity requestEntity = new StringEntity(reqJson, StandardCharsets.UTF_8);
        requestEntity.setContentEncoding(StandardCharsets.UTF_8.name());
        httpPost.setEntity(requestEntity);

        /*
        TODO 如何优雅的将httpclient异步请求的结果返回给上层的代理
        有待进一步查一下，今天先用CountDownLatch跑个结果出来吧。
         */
        CountDownLatch countDownLatch = new CountDownLatch(1);

        httpClient.execute(httpPost, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse response) {
                respJson = handleResponse(response);
                countDownLatch.countDown();
            }

            @Override
            public void failed(Exception ex) {
                httpPost.abort();
                countDownLatch.countDown();
                log.error("[httpclient post] error:", ex);
            }

            @Override
            public void cancelled() {
                httpPost.abort();
                countDownLatch.countDown();
                log.warn("[httpclient post]  cancelled!");
            }
        });

        countDownLatch.await();

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client
        System.out.println("resp json: " + respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }

    private String handleResponse(final HttpResponse endpointResponse) {
        String content = "";

        try {
            HttpEntity entity = endpointResponse.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent()) {
                    final StringBuilder sb = new StringBuilder();
                    final char[] tmp = new char[1024];
                    final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    int l;
                    while ((l = reader.read(tmp)) != -1) {
                        sb.append(tmp, 0, l);
                    }
                    content = sb.toString();
                }
            }
        } catch (ParseException | IOException e) {
            log.error("[handleResponse] error:", e);
        }

        return content;
    }
}
