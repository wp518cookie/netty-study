package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static io.netty.util.internal.ObjectUtil.checkPositive;

/**
 * @author wuping
 * @date 2018/12/19
 */

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> refCntUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");

    /**
     * 引用计数
     */
    private volatile int refCnt;

    protected AbstractReferenceCountedByteBuf(int maxCapacity) {
        // 设置最大容量
        super(maxCapacity);
        // 初始 refCnt 为 1
        refCntUpdater.set(this, 1);
    }

    @Override
    public int refCnt() {
        return refCnt;
    }

    /**
     * 直接修改 refCnt
     *
     * An unsafe operation intended for use by a subclass that sets the reference count of the buffer directly
     */
    protected final void setRefCnt(int refCnt) {
        refCntUpdater.set(this, refCnt);
    }

    @Override
    public ByteBuf retain() {
        return retain0(1);
    }

    @Override
    public ByteBuf retain(int increment) {
        return retain0(checkPositive(increment, "increment"));
    }

    private ByteBuf retain0(final int increment) {
        // 增加
        int oldRef = refCntUpdater.getAndAdd(this, increment);
        // 原有 refCnt 就是 <= 0 ；或者，increment 为负数
        if (oldRef <= 0 || oldRef + increment < oldRef) {
            // Ensure we don't resurrect (which means the refCnt was 0) and also that we encountered an overflow.
            // 加回去，负负得正。
            refCntUpdater.getAndAdd(this, -increment);
            // 抛出 IllegalReferenceCountException 异常
            throw new IllegalReferenceCountException(oldRef, increment);
        }
        return this;
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return release0(1);
    }

    @Override
    public boolean release(int decrement) {
        return release0(checkPositive(decrement, "decrement"));
    }

    @SuppressWarnings("Duplicates")
    private boolean release0(int decrement) {
        // 减少
        int oldRef = refCntUpdater.getAndAdd(this, -decrement);
        // 原有 oldRef 等于减少的值
        if (oldRef == decrement) {
            // 释放
            deallocate();
            return true;
            // 减少的值得大于 原有 oldRef ，说明“越界”；或者，increment 为负数
        } else if (oldRef < decrement || oldRef - decrement > oldRef) {
            // Ensure we don't over-release, and avoid underflow.
            // 加回去，负负得正。
            refCntUpdater.getAndAdd(this, decrement);
            // 抛出 IllegalReferenceCountException 异常
            throw new IllegalReferenceCountException(oldRef, -decrement);
        }
        return false;
    }

    /**
     * Called once {@link #refCnt()} is equals 0.
     */
    protected abstract void deallocate();
}
