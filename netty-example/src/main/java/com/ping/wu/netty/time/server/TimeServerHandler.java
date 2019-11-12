package com.ping.wu.netty.time.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * @author wuping
 * @date 2019-06-03
 */

public class TimeServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        System.out.println("server 收到内容:" + new String(bytes));
        byte[] response = new Date().toString().getBytes();
        ByteBuf byteBuf = ctx.alloc().buffer(response.length);
        byteBuf.writeBytes(response);
        ctx.channel().writeAndFlush(byteBuf);
    }
}
