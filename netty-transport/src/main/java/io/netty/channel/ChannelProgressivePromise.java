package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressivePromise;

/**
 * @author wuping
 * @date 2018/12/18
 */

public interface ChannelProgressivePromise extends ProgressivePromise<Void>, ChannelProgressiveFuture, ChannelPromise {
    @Override
    ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelProgressivePromise sync() throws InterruptedException;

    @Override
    ChannelProgressivePromise syncUninterruptibly();

    @Override
    ChannelProgressivePromise await() throws InterruptedException;

    @Override
    ChannelProgressivePromise awaitUninterruptibly();

    @Override
    ChannelProgressivePromise setSuccess(Void result);

    @Override
    ChannelProgressivePromise setSuccess();

    @Override
    ChannelProgressivePromise setFailure(Throwable cause);

    @Override
    ChannelProgressivePromise setProgress(long progress, long total);

    @Override
    ChannelProgressivePromise unvoid();
}
