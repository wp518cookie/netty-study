package com.ping.wu.bytebuf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author wuping
 * @date 2019-04-24
 */

public class EchoServer {
    public static void main(String[] args) throws Exception {
        ServerBootstrap strap = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        strap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
        strap.bind(8081);
    }
}
