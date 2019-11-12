package com.ping.wu.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuping
 * @date 2019-06-18
 */

public class TestServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector selector = Selector.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select(1000);
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                try {
                    handle(key);
                } catch (Exception e) {
                    key.cancel();
                    if (key.channel() != null) {
                        key.channel().close();
                    }
                }
            }
        }
    }

    public static void handle(SelectionKey key) {
        try {
            if (!key.isValid()) {
                return;
            }
            if (key.isAcceptable()) {
                SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
                sc.configureBlocking(false);
                sc.register(key.selector(), SelectionKey.OP_READ);
            } else if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                sc.read(byteBuffer);
                byteBuffer.flip();
                byte[] content = new byte[byteBuffer.remaining()];
                byteBuffer.get(content);
                System.out.println("read from client:" + new String(content));
                sc.register(key.selector(), SelectionKey.OP_WRITE);
            } else if (key.isWritable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                byte[] response = "hello client, i am server!".getBytes();
                ByteBuffer byteBuffer = ByteBuffer.wrap(response);
                sc.write(byteBuffer);
                sc.register(key.selector(), SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
