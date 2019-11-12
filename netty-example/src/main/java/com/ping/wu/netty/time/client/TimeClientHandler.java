package com.ping.wu.netty.time.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * @author wuping
 * @date 2019-06-03
 */

public class TimeClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        System.out.println("client 收到内容:" + new String(bytes));
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] response = "请问现在的时间是".getBytes();
        ByteBuf byteBuf = ctx.alloc().buffer(response.length);
        byteBuf.writeBytes(response);
        ctx.channel().writeAndFlush(byteBuf);
        ctx.writeAndFlush(byteBuf);
    }
}
