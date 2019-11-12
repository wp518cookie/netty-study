package com.ping.wu.chap6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuping
 * @date 2019-06-05
 */

public class ApiGatewayServerHandler extends ChannelInboundHandlerAdapter {
    ExecutorService executorService = Executors.newFixedThreadPool(8);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg);
        char[] req = new char[((ByteBuf)msg).readableBytes()];
        executorService.execute(() -> {
            char[] dispatchReq = req;
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

