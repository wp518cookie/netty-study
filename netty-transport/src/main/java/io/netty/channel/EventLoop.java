package io.netty.channel;

import io.netty.util.concurrent.OrderedEventExecutor;

/**
 * @author wuping
 * @date 2018/12/18
 */

public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}
