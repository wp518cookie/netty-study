package com.ping.wu.nio.demo;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuping
 * @date 2019-06-18
 */

public class TestClient {
    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        Selector selector = Selector.open();
        sc.configureBlocking(false);
        if (sc.connect(new InetSocketAddress("127.0.0.1", 8080))) {
            sc.register(selector, SelectionKey.OP_READ);
        } else {
            sc.register(selector, SelectionKey.OP_CONNECT);
        }
        while (true) {
            selector.select(1000);
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey meta = it.next();
                handle(meta);
                it.remove();
            }
        }
    }

    private static void handle(SelectionKey key) {
        try {
            if (!key.isValid()) {
                return;
            }
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(key.selector(), SelectionKey.OP_READ);
                    byte[] content = "hello server, i am client".getBytes();
                    ByteBuffer byteBuffer = ByteBuffer.wrap(content);
                    sc.write(byteBuffer);
                } else {
                    System.exit(1);
                }
            } else if (key.isReadable()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                sc.read(byteBuffer);
                byteBuffer.flip();
                byte[] content = new byte[byteBuffer.limit()];
                byteBuffer.get(content);
                System.out.println("client receive response from server:" + new String(content));
                sc.register(key.selector(), SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
