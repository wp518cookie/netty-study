package io.netty.buffer;

/**
 * @author wuping
 * @date 2018/12/19
 */

public interface ByteBufAllocatorMetricProvider {
    ByteBufAllocatorMetric metric();
}
