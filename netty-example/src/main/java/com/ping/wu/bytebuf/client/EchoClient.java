package com.ping.wu.bytebuf.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuping
 * @date 2019-04-24
 */

public class EchoClient {
    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        EventLoopGroup gorup = new NioEventLoopGroup();
        b.group(gorup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        b.connect("127.0.0.1", 8081);
    }
}
