package com.ping.wu.example.concurrent;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.junit.Test;

/**
 * @author wuping
 * @date 2019-06-20
 */

public class DefaultPromiseTest {
    @Test
    public void test1() {
        DefaultPromise<String> defaultPromise = new DefaultPromise(ImmediateEventExecutor.INSTANCE);
        defaultPromise.addListener(new FutureListener<String>() {
            @Override
            public void operationComplete(Future<String> future) throws Exception {
                String content = future.get();
                System.out.println("****** operationComplete:" + content);
            }
        });
        defaultPromise.setSuccess("hello world");
    }
}
