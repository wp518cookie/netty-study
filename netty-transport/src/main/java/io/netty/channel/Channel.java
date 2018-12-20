package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AttributeMap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author wuping
 * @date 2018/12/18
 */

public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {
    ChannelId id();

    /**
     * Return the {@link EventLoop} this {@link Channel} was registered to.
     * <p>
     * Channel 注册到的 EventLoop
     */
    EventLoop eventLoop();

    /**
     * Returns the parent of this channel.
     * <p>
     * 父 Channel 对象
     *
     * @return the parent channel.
     * {@code null} if this channel does not have a parent channel.
     */
    Channel parent();

    /**
     * Returns the configuration of this channel.
     * <p>
     * Channel 配置参数
     */
    ChannelConfig config();

    /**
     * Returns {@code true} if the {@link Channel} is open and may get active later
     * <p>
     * Channel 是否打开。
     * <p>
     * true 表示 Channel 可用
     * false 表示 Channel 已关闭，不可用
     */
    boolean isOpen();

    /**
     * Returns {@code true} if the {@link Channel} is registered with an {@link EventLoop}.
     * <p>
     * Channel 是否注册
     * <p>
     * true 表示 Channel 已注册到 EventLoop 上
     * false 表示 Channel 未注册到 EventLoop 上
     */
    boolean isRegistered();

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     * <p>
     * Channel 是否激活
     * <p>
     * 对于服务端 ServerSocketChannel ，true 表示 Channel 已经绑定到端口上，可提供服务
     * 对于客户端 SocketChannel ，true 表示 Channel 连接到远程服务器
     */
    boolean isActive();

    /**
     * Return the {@link ChannelMetadata} of the {@link Channel} which describe the nature of the {@link Channel}.
     * <p>
     * Channel 元数据
     */
    ChannelMetadata metadata();

    /**
     * Returns the local address where this channel is bound to.  The returned
     * {@link SocketAddress} is supposed to be down-cast into more concrete
     * type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     * <p>
     * 本地地址
     *
     * @return the local address of this channel.
     * {@code null} if this channel is not bound.
     */
    SocketAddress localAddress();

    /**
     * Returns the remote address where this channel is connected to.  The
     * returned {@link SocketAddress} is supposed to be down-cast into more
     * concrete type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     * <p>
     * 远端地址
     *
     * @return the remote address of this channel.
     * {@code null} if this channel is not connected.
     * If this channel is not connected but it can receive messages
     * from arbitrary remote addresses (e.g. {@link DatagramChannel},
     * use {@link DatagramPacket#recipient()} to determine
     * the origination of the received message as this method will
     * return {@code null}.
     */
    SocketAddress remoteAddress();

    /**
     * Returns the {@link ChannelFuture} which will be notified when this
     * channel is closed.  This method always returns the same future instance.
     * <p>
     * Channel 关闭的 Future 对象
     */
    ChannelFuture closeFuture();

    /**
     * Returns {@code true} if and only if the I/O thread will perform the
     * requested write operation immediately.  Any write requests made when
     * this method returns {@code false} are queued until the I/O thread is
     * ready to process the queued write requests.
     * <p>
     * Channel 是否可写
     * <p>
     * 当 Channel 的写缓存区 outbound 非 null 且可写时，返回 true
     */
    boolean isWritable();

    /**
     * 获得距离不可写还有多少字节数
     * <p>
     * Get how many bytes can be written until {@link #isWritable()} returns {@code false}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code false} then 0.
     */
    long bytesBeforeUnwritable();

    /**
     * 获得距离可写还要多少字节数
     * <p>
     * Get how many bytes must be drained from underlying buffers until {@link #isWritable()} returns {@code true}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code true} then 0.
     */
    long bytesBeforeWritable();

    /**
     * Returns an <em>internal-use-only</em> object that provides unsafe operations.
     * <p>
     * Unsafe 对象
     */
    Unsafe unsafe();

    /**
     * Return the assigned {@link ChannelPipeline}.
     * <p>
     * ChannelPipeline 对象，用于处理 Inbound 和 Outbound 事件的处理
     */
    ChannelPipeline pipeline();

    /**
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     * <p>
     * ByteBuf 分配器
     */
    ByteBufAllocator alloc();

    @Override
    Channel read();

    @Override
    Channel flush();

    /**
     * <em>Unsafe</em> operations that should <em>never</em> be called from user-code. These methods
     * are only provided to implement the actual transport, and must be invoked from an I/O thread except for the
     * following methods:
     * <ul>
     *   <li>{@link #localAddress()}</li>
     *   <li>{@link #remoteAddress()}</li>
     *   <li>{@link #closeForcibly()}</li>
     *   <li>{@link #register(EventLoop, ChannelPromise)}</li>
     *   <li>{@link #deregister(ChannelPromise)}</li>
     *   <li>{@link #voidPromise()}</li>
     * </ul>
     */
    interface Unsafe {
        /**
         * Return the assigned {@link RecvByteBufAllocator.Handle} which will be used to allocate {@link ByteBuf}'s when
         * receiving data.
         *
         * ByteBuf 分配器的处理器
         */
        RecvByteBufAllocator.Handle recvBufAllocHandle();

        /**
         * Return the {@link SocketAddress} to which is bound local or
         * {@code null} if none.
         *
         * 本地地址
         */
        SocketAddress localAddress();

        /**
         * Return the {@link SocketAddress} to which is bound remote or
         * {@code null} if none is bound yet.
         *
         * 远端地址
         */
        SocketAddress remoteAddress();

        /**
         * Register the {@link Channel} of the {@link ChannelPromise} and notify
         * the {@link ChannelFuture} once the registration was complete.
         */
        void register(EventLoop eventLoop, ChannelPromise promise);

        /**
         * Bind the {@link SocketAddress} to the {@link Channel} of the {@link ChannelPromise} and notify
         * it once its done.
         */
        void bind(SocketAddress localAddress, ChannelPromise promise);

        /**
         * Connect the {@link Channel} of the given {@link ChannelFuture} with the given remote {@link SocketAddress}.
         * If a specific local {@link SocketAddress} should be used it need to be given as argument. Otherwise just
         * pass {@code null} to it.
         *
         * The {@link ChannelPromise} will get notified once the connect operation was complete.
         */
        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        /**
         * Disconnect the {@link Channel} of the {@link ChannelFuture} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        void disconnect(ChannelPromise promise);

        /**
         * Close the {@link Channel} of the {@link ChannelPromise} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        void close(ChannelPromise promise);

        /**
         * Closes the {@link Channel} immediately without firing any events.  Probably only useful
         * when registration attempt failed.
         */
        void closeForcibly();

        /**
         * Deregister the {@link Channel} of the {@link ChannelPromise} from {@link EventLoop} and notify the
         * {@link ChannelPromise} once the operation was complete.
         */
        void deregister(ChannelPromise promise);

        /**
         * Schedules a read operation that fills the inbound buffer of the first {@link ChannelInboundHandler} in the
         * {@link ChannelPipeline}.  If there's already a pending read operation, this method does nothing.
         */
        void beginRead();

        /**
         * Schedules a write operation.
         */
        void write(Object msg, ChannelPromise promise);

        /**
         * Flush out all write operations scheduled via {@link #write(Object, ChannelPromise)}.
         */
        void flush();

        /**
         * Return a special ChannelPromise which can be reused and passed to the operations in {@link Unsafe}.
         * It will never be notified of a success or error and so is only a placeholder for operations
         * that take a {@link ChannelPromise} as argument but for which you not want to get notified.
         */
        ChannelPromise voidPromise();

        /**
         * Returns the {@link ChannelOutboundBuffer} of the {@link Channel} where the pending write requests are stored.
         */
        ChannelOutboundBuffer outboundBuffer();
    }
}
