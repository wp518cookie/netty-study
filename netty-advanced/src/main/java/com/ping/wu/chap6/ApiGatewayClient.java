package com.ping.wu.chap6;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuping
 * @date 2019-06-05
 */

public class ApiGatewayClient {
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "18086"));
    static final int MSG_SIZE = 256;

    public void run() throws Exception  {
        connect();
    }

    public void connect() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new ApiGatewayClientHandler());
                    }
                });
        ChannelFuture f = b.connect(HOST, PORT).sync();
        f.channel().closeFuture().sync();
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        new ApiGatewayClient().run();
    }
}
