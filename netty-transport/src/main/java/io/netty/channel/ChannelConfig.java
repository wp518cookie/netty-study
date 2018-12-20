package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

/**
 * @author wuping
 * @date 2018/12/18
 */

public interface ChannelConfig {
    Map<ChannelOption<?>, Object> getOptions();

    boolean setOptions(Map<ChannelOption<?>, ?> options);

    <T> T getOption(ChannelOption<T> option);

    /**
     * Sets a configuration property with the specified name and value.
     * To override this method properly, you must call the super class:
     * <pre>
     * public boolean setOption(ChannelOption&lt;T&gt; option, T value) {
     *     if (super.setOption(option, value)) {
     *         return true;
     *     }
     *
     *     if (option.equals(additionalOption)) {
     *         ....
     *         return true;
     *     }
     *
     *     return false;
     * }
     * </pre>
     *
     * @return {@code true} if and only if the property has been set
     */
    <T> boolean setOption(ChannelOption<T> option, T value);

    /**
     * Returns the connect timeout of the channel in milliseconds.  If the
     * {@link Channel} does not support connect operation, this property is not
     * used at all, and therefore will be ignored.
     *
     * @return the connect timeout in milliseconds.  {@code 0} if disabled.
     */
    int getConnectTimeoutMillis();

    /**
     * Sets the connect timeout of the channel in milliseconds.  If the
     * {@link Channel} does not support connect operation, this property is not
     * used at all, and therefore will be ignored.
     *
     * @param connectTimeoutMillis the connect timeout in milliseconds.
     *                             {@code 0} to disable.
     */
    ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis);

    /**
     * @deprecated Use {@link MaxMessagesRecvByteBufAllocator}
     * <p>
     * Returns the maximum number of messages to read per read loop.
     * a {@link ChannelInboundHandler#channelRead(ChannelHandlerContext, Object) channelRead()} event.
     * If this value is greater than 1, an event loop might attempt to read multiple times to procure multiple messages.
     */
    @Deprecated
    int getMaxMessagesPerRead();

    /**
     * @deprecated Use {@link MaxMessagesRecvByteBufAllocator}
     * <p>
     * Sets the maximum number of messages to read per read loop.
     * If this value is greater than 1, an event loop might attempt to read multiple times to procure multiple messages.
     */
    @Deprecated
    ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead);

    /**
     * Returns the maximum loop count for a write operation until
     * {@link WritableByteChannel#write(ByteBuffer)} returns a non-zero value.
     * It is similar to what a spin lock is used for in concurrency programming.
     * It improves memory utilization and write throughput depending on
     * the platform that JVM runs on.  The default value is {@code 16}.
     */
    int getWriteSpinCount();

    /**
     * Sets the maximum loop count for a write operation until
     * {@link WritableByteChannel#write(ByteBuffer)} returns a non-zero value.
     * It is similar to what a spin lock is used for in concurrency programming.
     * It improves memory utilization and write throughput depending on
     * the platform that JVM runs on.  The default value is {@code 16}.
     *
     * @throws IllegalArgumentException
     *         if the specified value is {@code 0} or less than {@code 0}
     */
    ChannelConfig setWriteSpinCount(int writeSpinCount);

    /**
     * Returns {@link ByteBufAllocator} which is used for the channel
     * to allocate buffers.
     */
    ByteBufAllocator getAllocator();

    /**
     * Set the {@link ByteBufAllocator} which is used for the channel
     * to allocate buffers.
     */
    ChannelConfig setAllocator(ByteBufAllocator allocator);

    /**
     * Returns {@link RecvByteBufAllocator} which is used for the channel to allocate receive buffers.
     */
    <T extends RecvByteBufAllocator> T getRecvByteBufAllocator();

    /**
     * Set the {@link RecvByteBufAllocator} which is used for the channel to allocate receive buffers.
     */
    ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator);

    /**
     * Returns {@code true} if and only if {@link ChannelHandlerContext#read()} will be invoked automatically so that
     * a user application doesn't need to call it at all. The default value is {@code true}.
     */
    boolean isAutoRead();

    /**
     * Sets if {@link ChannelHandlerContext#read()} will be invoked automatically so that a user application doesn't
     * need to call it at all. The default value is {@code true}.
     */
    ChannelConfig setAutoRead(boolean autoRead);

    /**
     * Returns {@code true} if and only if the {@link Channel} will be closed automatically and immediately on
     * write failure. The default is {@code true}.
     */
    boolean isAutoClose();

    /**
     * Sets whether the {@link Channel} should be closed automatically and immediately on write failure.
     * The default is {@code true}.
     */
    ChannelConfig setAutoClose(boolean autoClose);

    /**
     * Returns the high water mark of the write buffer.  If the number of bytes
     * queued in the write buffer exceeds this value, {@link Channel#isWritable()}
     * will start to return {@code false}.
     */
    int getWriteBufferHighWaterMark();

    /**
     * <p>
     * Sets the high water mark of the write buffer.  If the number of bytes
     * queued in the write buffer exceeds this value, {@link Channel#isWritable()}
     * will start to return {@code false}.
     */
    ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark);

    /**
     * Returns the low water mark of the write buffer.  Once the number of bytes
     * queued in the write buffer exceeded the
     * {@linkplain #setWriteBufferHighWaterMark(int) high water mark} and then
     * dropped down below this value, {@link Channel#isWritable()} will start to return
     * {@code true} again.
     */
    int getWriteBufferLowWaterMark();

    /**
     * <p>
     * Sets the low water mark of the write buffer.  Once the number of bytes
     * queued in the write buffer exceeded the
     * {@linkplain #setWriteBufferHighWaterMark(int) high water mark} and then
     * dropped down below this value, {@link Channel#isWritable()} will start to return
     * {@code true} again.
     */
    ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark);

    /**
     * Returns {@link MessageSizeEstimator} which is used for the channel
     * to detect the size of a message.
     */
    MessageSizeEstimator getMessageSizeEstimator();

    /**
     * Set the {@link MessageSizeEstimator} which is used for the channel
     * to detect the size of a message.
     */
    ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator);

    /**
     * Returns the {@link WriteBufferWaterMark} which is used for setting the high and low
     * water mark of the write buffer.
     */
    WriteBufferWaterMark getWriteBufferWaterMark();

    /**
     * Set the {@link WriteBufferWaterMark} which is used for setting the high and low
     * water mark of the write buffer.
     */
    ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark);
}
