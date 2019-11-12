package com.ping.wu.chap4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.DefaultPromise;

/**
 * @author wuping
 * @date 2019-06-05
 */

public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    DefaultPromise<HttpResponse> respPromise;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse msg) throws Exception {
        if (msg.decoderResult().isFailure()) {
            throw new Exception("Decode HttpResponse error : " + msg.decoderResult().cause());
        }
        HttpResponse response = new HttpResponse(msg);
        respPromise.setSuccess(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public DefaultPromise<HttpResponse> getRespPromise() {
        return respPromise;
    }

    public void setRespPromise(DefaultPromise<HttpResponse> respPromise) {
        this.respPromise = respPromise;
    }
}
