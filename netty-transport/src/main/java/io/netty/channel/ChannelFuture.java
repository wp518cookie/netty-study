package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author wuping
 * @date 2018/12/18
 *  *                                      +---------------------------+
 *  *                                      | Completed successfully    |
 *  *                                      +---------------------------+
 *  *                                 +---->      isDone() = true      |
 *  * +--------------------------+    |    |   isSuccess() = true      |
 *  * |        Uncompleted       |    |    +===========================+
 *  * +--------------------------+    |    | Completed with failure    |
 *  * |      isDone() = false    |    |    +---------------------------+
 *  * |   isSuccess() = false    |----+---->      isDone() = true      |
 *  * | isCancelled() = false    |    |    |       cause() = non-null  |
 *  * |       cause() = null     |    |    +===========================+
 *  * +--------------------------+    |    | Completed by cancellation |
 *  *                                 |    +---------------------------+
 *  *                                 +---->      isDone() = true      |
 *  *                                      | isCancelled() = true      |
 *  *                                      +---------------------------+
 */

/**
 * GenericFutureListener避免手工检查异步调用的结果
 */
public interface ChannelFuture extends Future<Void> {
    Channel channel();

    @Override
    ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelFuture sync() throws InterruptedException;

    @Override
    ChannelFuture syncUninterruptibly();

    @Override
    ChannelFuture await() throws InterruptedException;

    @Override
    ChannelFuture awaitUninterruptibly();

    boolean isVoid();
}
