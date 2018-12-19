package io.netty.buffer;

/**
 * @author wuping
 * @date 2018/12/19
 */

public interface ByteBufAllocatorMetric {
    /**
     * Returns the number of bytes of heap memory used by a {@link ByteBufAllocator} or {@code -1} if unknown.
     *
     * 已使用 Heap 占用内存大小
     */
    long usedHeapMemory();

    /**
     * Returns the number of bytes of direct memory used by a {@link ByteBufAllocator} or {@code -1} if unknown.
     *
     * 已使用 Direct 占用内存大小
     */
    long usedDirectMemory();
}
