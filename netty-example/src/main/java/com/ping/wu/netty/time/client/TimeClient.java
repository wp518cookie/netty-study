package com.ping.wu.netty.time.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author wuping
 * @date 2019-06-03
 */

public class TimeClient {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new TimeClientHandler());
                    }
                });
        ChannelFuture ch = b.connect().sync();
        ch.channel().closeFuture().sync();
    }
}
