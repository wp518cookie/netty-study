package com.ping.wu.chap3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * @author wuping
 * @date 2019-06-05
 */

public class PoolByteBufPerformanceTest {
    public static void main(String[] args) {
//        unPoolTest();
        poolTest();
    }

    static void unPoolTest() {
        //非内存池模式
        long beginTime = System.currentTimeMillis();
        ByteBuf buf = null;
        int maxTime = 100000000;
        for (int i = 0; i < maxTime; i++) {
            buf = Unpooled.buffer(10 * 1024);
            buf.release();
        }
        System.out.println("Execute " + maxTime + " time cost time: " + (System.currentTimeMillis() - beginTime));
    }

    static void poolTest() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
        long beginTime = System.currentTimeMillis();
        ByteBuf buf = null;
        int maxTimes = 100000000;
        for (int i = 0; i < maxTimes; i++) {
            buf = allocator.directBuffer(10 * 1024);
            buf.release();
        }
        System.out.println("Execute " + maxTimes + " time cost time: " + (System.currentTimeMillis() - beginTime));
    }
}
