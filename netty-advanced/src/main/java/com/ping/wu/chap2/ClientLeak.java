package com.ping.wu.chap2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuping
 * @date 2019-06-04
 */

public class ClientLeak {
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "18081"));

    public static void main(String[] args) {

    }

    static void initClientPool(int poolSize) throws Exception {
        for(int i = 0; i < poolSize; i++)
        {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                        }
                    });
            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.channel().closeFuture().addListener((r)->
            {
                group.shutdownGracefully();
            });
        }
    }
}
