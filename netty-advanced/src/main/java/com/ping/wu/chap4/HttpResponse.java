package com.ping.wu.chap4;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * @author wuping
 * @date 2019-06-05
 */

public class HttpResponse {
    private HttpHeaders header;
    private FullHttpResponse response;
    private byte[] body;

    public HttpResponse(FullHttpResponse response) {
        this.header = response.headers();
        this.response = response;
        if (response.content() != null) {
            body = new byte[response.content().readableBytes()];
            response.content().getBytes(0, body);
        }
    }

    public HttpHeaders header() {
        return header;
    }

    public byte[] body() {
        return body;
    }
}
