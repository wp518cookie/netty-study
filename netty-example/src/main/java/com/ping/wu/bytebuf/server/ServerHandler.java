package com.ping.wu.bytebuf.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wuping
 * @date 2019-04-24
 */

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] content = new byte[byteBuf.readByte()];
        byteBuf.readBytes(content);
        System.out.println("服务端收到消息:" + new String(content));
        byte[] response = "服务端的回复【你好啊客户端】".getBytes();
        ByteBuf responseBuf = ctx.alloc().buffer();
        responseBuf.writeByte((byte) (response.length));
        responseBuf.writeBytes(response);
        ctx.writeAndFlush(responseBuf);
    }
}
