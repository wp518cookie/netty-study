package com.ping.wu.bytebuf.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wuping
 * @date 2019-04-24
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端连接成功开始发送消息!");
        for (int i = 0; i < 50; i++) {
            byte[] bytes = "你好，我是麻子嘎嘎".getBytes();
            byte[] newBytes = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, newBytes, 1, bytes.length);
            newBytes[0] = (byte) (bytes.length);
            ByteBuf byteBuf = ctx.alloc().buffer(newBytes.length);
            byteBuf.writeBytes(newBytes);
            ctx.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes() - 1];
        byteBuf.readBytes(bytes, 1, bytes.length);
        System.out.println("客户端收到消息:" + new String(bytes));
    }
}
