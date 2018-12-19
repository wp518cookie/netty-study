package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author wuping
 * @date 2018/12/19
 */

public abstract class ByteBuf implements ReferenceCounted, Comparable<ByteBuf> {
    public abstract int capacity();

    public abstract ByteBuf capacity(int newCapacity);

    public abstract int maxCapacity();

    public abstract ByteBufAllocator alloc();

    /**
     * Returns the <a href="http://en.wikipedia.org/wiki/Endianness">endianness</a>
     * of this buffer.
     *
     * @deprecated use the Little Endian accessors, e.g. {@code getShortLE}, {@code getIntLE}
     * instead of creating a buffer with swapped {@code endianness}.
     */
    @Deprecated
    public abstract ByteOrder order(); // 字节序，即大小端。推荐阅读 http://www.ruanyifeng.com/blog/2016/11/byte-order.html

    /**
     * Returns a buffer with the specified {@code endianness} which shares the whole region,
     * indexes, and marks of this buffer.  Modifying the content, the indexes, or the marks of the
     * returned buffer or this buffer affects each other's content, indexes, and marks.  If the
     * specified {@code endianness} is identical to this buffer's byte order, this method can
     * return {@code this}.  This method does not modify {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @deprecated use the Little Endian accessors, e.g. {@code getShortLE}, {@code getIntLE}
     * instead of creating a buffer with swapped {@code endianness}.
     */
    @Deprecated
    public abstract ByteBuf order(ByteOrder endianness);

    /**
     * Return the underlying buffer instance if this buffer is a wrapper of another buffer.
     *
     * @return {@code null} if this buffer is not a wrapper
     */
    public abstract ByteBuf unwrap(); // 获得被包装( wrap )的 ByteBuf 对象。

    /**
     * Returns {@code true} if and only if this buffer is backed by an
     * NIO direct buffer.
     */
    public abstract boolean isDirect(); // 是否 NIO Direct Buffer

    /**
     * Returns {@code true} if and only if this buffer is read-only.
     */
    public abstract boolean isReadOnly(); // 是否为只读 Buffer

    /**
     * Returns a read-only version of this buffer.
     */
    public abstract ByteBuf asReadOnly();

    /**
     * Returns the {@code readerIndex} of this buffer.
     */
    public abstract int readerIndex(); // 读取位置

    /**
     * Sets the {@code readerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code readerIndex} is
     *            less than {@code 0} or
     *            greater than {@code this.writerIndex}
     */
    public abstract ByteBuf readerIndex(int readerIndex);

    /**
     * Returns the {@code writerIndex} of this buffer.
     */
    public abstract int writerIndex(); // 写入位置

    /**
     * Sets the {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code writerIndex} is
     *            less than {@code this.readerIndex} or
     *            greater than {@code this.capacity}
     */
    public abstract ByteBuf writerIndex(int writerIndex);

    /**
     * Sets the {@code readerIndex} and {@code writerIndex} of this buffer
     * in one shot.  This method is useful when you have to worry about the
     * invocation order of {@link #readerIndex(int)} and {@link #writerIndex(int)}
     * methods.  For example, the following code will fail:
     *
     * <pre>
     * // Create a buffer whose readerIndex, writerIndex and capacity are
     * // 0, 0 and 8 respectively.
     * {@link ByteBuf} buf = {@link Unpooled}.buffer(8);
     *
     * // IndexOutOfBoundsException is thrown because the specified
     * // readerIndex (2) cannot be greater than the current writerIndex (0).
     * buf.readerIndex(2);
     * buf.writerIndex(4);
     * </pre>
     *
     * The following code will also fail:
     *
     * <pre>
     * // Create a buffer whose readerIndex, writerIndex and capacity are
     * // 0, 8 and 8 respectively.
     * {@link ByteBuf} buf = {@link Unpooled}.wrappedBuffer(new byte[8]);
     *
     * // readerIndex becomes 8.
     * buf.readLong();
     *
     * // IndexOutOfBoundsException is thrown because the specified
     * // writerIndex (4) cannot be less than the current readerIndex (8).
     * buf.writerIndex(4);
     * buf.readerIndex(2);
     * </pre>
     *
     * By contrast, this method guarantees that it never
     * throws an {@link IndexOutOfBoundsException} as long as the specified
     * indexes meet basic constraints, regardless what the current index
     * values of the buffer are:
     *
     * <pre>
     * // No matter what the current state of the buffer is, the following
     * // call always succeeds as long as the capacity of the buffer is not
     * // less than 4.
     * buf.setIndex(2, 4);
     * </pre>
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code readerIndex} is less than 0,
     *         if the specified {@code writerIndex} is less than the specified
     *         {@code readerIndex} or if the specified {@code writerIndex} is
     *         greater than {@code this.capacity}
     */
    public abstract ByteBuf setIndex(int readerIndex, int writerIndex); // 设置读取和写入位置

    /**
     * Returns the number of readable bytes which is equal to
     * {@code (this.writerIndex - this.readerIndex)}.
     */
    public abstract int readableBytes(); // 剩余可读字节数

    /**
     * Returns the number of writable bytes which is equal to
     * {@code (this.capacity - this.writerIndex)}.
     */
    public abstract int writableBytes(); // 剩余可写字节数

    /**
     * Returns the maximum possible number of writable bytes, which is equal to
     * {@code (this.maxCapacity - this.writerIndex)}.
     */
    public abstract int maxWritableBytes();

    /**
     * Returns {@code true}
     * if and only if {@code (this.writerIndex - this.readerIndex)} is greater
     * than {@code 0}.
     */
    public abstract boolean isReadable();

    /**
     * Returns {@code true} if and only if this buffer contains equal to or more than the specified number of elements.
     */
    public abstract boolean isReadable(int size);

    /**
     * Returns {@code true}
     * if and only if {@code (this.capacity - this.writerIndex)} is greater
     * than {@code 0}.
     */
    public abstract boolean isWritable();

    /**
     * Returns {@code true} if and only if this buffer has enough room to allow writing the specified number of
     * elements.
     */
    public abstract boolean isWritable(int size);

    /**
     * Sets the {@code readerIndex} and {@code writerIndex} of this buffer to
     * {@code 0}.
     * This method is identical to {@link #setIndex(int, int) setIndex(0, 0)}.
     * <p>
     * Please note that the behavior of this method is different
     * from that of NIO buffer, which sets the {@code limit} to
     * the {@code capacity} of the buffer.
     */
    public abstract ByteBuf clear(); // 清空字节空间。实际是修改 readerIndex=writerIndex=0，标记清空。

    /**
     * Marks the current {@code readerIndex} in this buffer.  You can
     * reposition the current {@code readerIndex} to the marked
     * {@code readerIndex} by calling {@link #resetReaderIndex()}.
     * The initial value of the marked {@code readerIndex} is {@code 0}.
     */
    public abstract ByteBuf markReaderIndex(); // 标记读取位置

    /**
     * Repositions the current {@code readerIndex} to the marked
     * {@code readerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the current {@code writerIndex} is less than the marked
     *         {@code readerIndex}
     */
    public abstract ByteBuf resetReaderIndex();

    /**
     * Marks the current {@code writerIndex} in this buffer.  You can
     * reposition the current {@code writerIndex} to the marked
     * {@code writerIndex} by calling {@link #resetWriterIndex()}.
     * The initial value of the marked {@code writerIndex} is {@code 0}.
     */
    public abstract ByteBuf markWriterIndex(); // 标记写入位置

    /**
     * Repositions the current {@code writerIndex} to the marked
     * {@code writerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the current {@code readerIndex} is greater than the marked
     *         {@code writerIndex}
     */
    public abstract ByteBuf resetWriterIndex();

    /**
     * Discards the bytes between the 0th index and {@code readerIndex}.
     * It moves the bytes between {@code readerIndex} and {@code writerIndex}
     * to the 0th index, and sets {@code readerIndex} and {@code writerIndex}
     * to {@code 0} and {@code oldWriterIndex - oldReaderIndex} respectively.
     * <p>
     * Please refer to the class documentation for more detailed explanation.
     */
    public abstract ByteBuf discardReadBytes(); // 释放已读的字节空间

    /**
     * Similar to {@link ByteBuf#discardReadBytes()} except that this method might discard
     * some, all, or none of read bytes depending on its internal implementation to reduce
     * overall memory bandwidth consumption at the cost of potentially additional memory
     * consumption.
     */
    public abstract ByteBuf discardSomeReadBytes(); // 释放部分已读的字节空间

    /**
     * Makes sure the number of {@linkplain #writableBytes() the writable bytes}
     * is equal to or greater than the specified value.  If there is enough
     * writable bytes in this buffer, this method returns with no side effect.
     * Otherwise, it raises an {@link IllegalArgumentException}.
     *
     * @param minWritableBytes
     *        the expected minimum number of writable bytes
     * @throws IndexOutOfBoundsException
     *         if {@link #writerIndex()} + {@code minWritableBytes} &gt; {@link #maxCapacity()}
     */
    public abstract ByteBuf ensureWritable(int minWritableBytes);

    /**
     * Tries to make sure the number of {@linkplain #writableBytes() the writable bytes}
     * is equal to or greater than the specified value.  Unlike {@link #ensureWritable(int)},
     * this method does not raise an exception but returns a code.
     *
     * @param minWritableBytes
     *        the expected minimum number of writable bytes
     * @param force
     *        When {@link #writerIndex()} + {@code minWritableBytes} &gt; {@link #maxCapacity()}:
     *        <ul>
     *        <li>{@code true} - the capacity of the buffer is expanded to {@link #maxCapacity()}</li>
     *        <li>{@code false} - the capacity of the buffer is unchanged</li>
     *        </ul>
     * @return {@code 0} if the buffer has enough writable bytes, and its capacity is unchanged.
     *         {@code 1} if the buffer does not have enough bytes, and its capacity is unchanged.
     *         {@code 2} if the buffer has enough writable bytes, and its capacity has been increased.
     *         {@code 3} if the buffer does not have enough bytes, but its capacity has been
     *                   increased to its maximum.
     */
    public abstract int ensureWritable(int minWritableBytes, boolean force);

    /**
     * Gets a boolean at the specified absolute (@code index) in this buffer.
     * This method does not modify the {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    public abstract boolean getBoolean(int index); // Boolean

    public abstract byte  getByte(int index);

    /**
     * Gets an unsigned byte at the specified absolute {@code index} in this
     * buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    public abstract short getUnsignedByte(int index);

    /**
     * Gets a 16-bit short integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract short getShort(int index);

    /**
     * Gets a 16-bit short integer at the specified absolute {@code index} in
     * this buffer in Little Endian Byte Order. This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract short getShortLE(int index);

    /**
     * Gets an unsigned 16-bit short integer at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract int getUnsignedShort(int index);

    /**
     * Gets an unsigned 16-bit short integer at the specified absolute
     * {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract int getUnsignedShortLE(int index);

    /**
     * Gets a 24-bit medium integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    public abstract int   getMedium(int index);

    /**
     * Gets a 24-bit medium integer at the specified absolute {@code index} in
     * this buffer in the Little Endian Byte Order. This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    public abstract int getMediumLE(int index);

    /**
     * Gets an unsigned 24-bit medium integer at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    public abstract int   getUnsignedMedium(int index);

    /**
     * Gets an unsigned 24-bit medium integer at the specified absolute
     * {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    public abstract int   getUnsignedMediumLE(int index);

    /**
     * Gets a 32-bit integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract int   getInt(int index);

    /**
     * Gets a 32-bit integer at the specified absolute {@code index} in
     * this buffer with Little Endian Byte Order. This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract int   getIntLE(int index);

    /**
     * Gets an unsigned 32-bit integer at the specified absolute {@code index}
     * in this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract long  getUnsignedInt(int index);

    /**
     * Gets an unsigned 32-bit integer at the specified absolute {@code index}
     * in this buffer in Little Endian Byte Order. This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract long  getUnsignedIntLE(int index);

    /**
     * Gets a 64-bit long integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public abstract long  getLong(int index);

    /**
     * Gets a 64-bit long integer at the specified absolute {@code index} in
     * this buffer in Little Endian Byte Order. This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public abstract long  getLongLE(int index);

    /**
     * Gets a 2-byte UTF-16 character at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract char  getChar(int index);

    /**
     * Gets a 32-bit floating point number at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract float getFloat(int index);

    /**
     * Gets a 32-bit floating point number at the specified absolute
     * {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public float getFloatLE(int index) {
        return Float.intBitsToFloat(getIntLE(index));
    }

    /**
     * Gets a 64-bit floating point number at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public abstract double getDouble(int index);

    /**
     * Gets a 64-bit floating point number at the specified absolute
     * {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public double getDoubleLE(int index) {
        return Double.longBitsToDouble(getLongLE(index));
    }

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index} until the destination becomes
     * non-writable.  This method is basically same with
     * {@link #getBytes(int, ByteBuf, int, int)}, except that this
     * method increases the {@code writerIndex} of the destination by the
     * number of the transferred bytes while
     * {@link #getBytes(int, ByteBuf, int, int)} does not.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * the source buffer (i.e. {@code this}).
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + dst.writableBytes} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf getBytes(int index, ByteBuf dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.  This method is basically same
     * with {@link #getBytes(int, ByteBuf, int, int)}, except that this
     * method increases the {@code writerIndex} of the destination by the
     * number of the transferred bytes while
     * {@link #getBytes(int, ByteBuf, int, int)} does not.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * the source buffer (i.e. {@code this}).
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code length} is greater than {@code dst.writableBytes}
     */
    public abstract ByteBuf getBytes(int index, ByteBuf dst, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex}
     * of both the source (i.e. {@code this}) and the destination.
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code dstIndex + length} is greater than
     *            {@code dst.capacity}
     */
    public abstract ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + dst.length} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf getBytes(int index, byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code dstIndex + length} is greater than
     *            {@code dst.length}
     */
    public abstract ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index} until the destination's position
     * reaches its limit.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer while the destination's {@code position} will be increased.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + dst.remaining()} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf getBytes(int index, ByteBuffer dst);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than
     *            {@code this.capacity}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract ByteBuf getBytes(int index, OutputStream out, int length) throws IOException;

    /**
     * Transfers this buffer's data to the specified channel starting at the
     * specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes written out to the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than
     *            {@code this.capacity}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int getBytes(int index, GatheringByteChannel out, int length) throws IOException;

    /**
     * Transfers this buffer's data starting at the specified absolute {@code index}
     * to the specified channel starting at the given file position.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer. This method does not modify the channel's position.
     *
     * @param position the file position at which the transfer is to begin
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes written out to the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than
     *            {@code this.capacity}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int getBytes(int index, FileChannel out, long position, int length) throws IOException;

    /**
     * Gets a {@link CharSequence} with the given length at the given index.
     *
     * @param length the length to read
     * @param charset that should be used
     * @return the sequence
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract CharSequence getCharSequence(int index, int length, Charset charset);

    /**
     * Sets the specified boolean at the specified absolute {@code index} in this
     * buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setBoolean(int index, boolean value);

    /**
     * Sets the specified byte at the specified absolute {@code index} in this
     * buffer.  The 24 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setByte(int index, int value);

    /**
     * Sets the specified 16-bit short integer at the specified absolute
     * {@code index} in this buffer.  The 16 high-order bits of the specified
     * value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setShort(int index, int value);

    /**
     * Sets the specified 16-bit short integer at the specified absolute
     * {@code index} in this buffer with the Little Endian Byte Order.
     * The 16 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setShortLE(int index, int value);

    /**
     * Sets the specified 24-bit medium integer at the specified absolute
     * {@code index} in this buffer.  Please note that the most significant
     * byte is ignored in the specified value.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setMedium(int index, int value);

    /**
     * Sets the specified 24-bit medium integer at the specified absolute
     * {@code index} in this buffer in the Little Endian Byte Order.
     * Please note that the most significant byte is ignored in the
     * specified value.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setMediumLE(int index, int value);

    /**
     * Sets the specified 32-bit integer at the specified absolute
     * {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setInt(int index, int value);

    /**
     * Sets the specified 32-bit integer at the specified absolute
     * {@code index} in this buffer with Little Endian byte order
     * .
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setIntLE(int index, int value);

    /**
     * Sets the specified 64-bit long integer at the specified absolute
     * {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setLong(int index, long value);

    /**
     * Sets the specified 64-bit long integer at the specified absolute
     * {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setLongLE(int index, long value);

    /**
     * Sets the specified 2-byte UTF-16 character at the specified absolute
     * {@code index} in this buffer.
     * The 16 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setChar(int index, int value);

    /**
     * Sets the specified 32-bit floating-point number at the specified
     * absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setFloat(int index, float value);

    /**
     * Sets the specified 32-bit floating-point number at the specified
     * absolute {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    public ByteBuf setFloatLE(int index, float value) {
        return setIntLE(index, Float.floatToRawIntBits(value));
    }

    /**
     * Sets the specified 64-bit floating-point number at the specified
     * absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setDouble(int index, double value);

    /**
     * Sets the specified 64-bit floating-point number at the specified
     * absolute {@code index} in this buffer in Little Endian Byte Order.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    public ByteBuf setDoubleLE(int index, double value) {
        return setLongLE(index, Double.doubleToRawLongBits(value));
    }

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index} until the source buffer becomes
     * unreadable.  This method is basically same with
     * {@link #setBytes(int, ByteBuf, int, int)}, except that this
     * method increases the {@code readerIndex} of the source buffer by
     * the number of the transferred bytes while
     * {@link #setBytes(int, ByteBuf, int, int)} does not.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * the source buffer (i.e. {@code this}).
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.readableBytes} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf setBytes(int index, ByteBuf src);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index}.  This method is basically same
     * with {@link #setBytes(int, ByteBuf, int, int)}, except that this
     * method increases the {@code readerIndex} of the source buffer by
     * the number of the transferred bytes while
     * {@link #setBytes(int, ByteBuf, int, int)} does not.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * the source buffer (i.e. {@code this}).
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code length} is greater than {@code src.readableBytes}
     */
    public abstract ByteBuf setBytes(int index, ByteBuf src, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex}
     * of both the source (i.e. {@code this}) and the destination.
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code srcIndex + length} is greater than
     *            {@code src.capacity}
     */
    public abstract ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.length} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf setBytes(int index, byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code srcIndex + length} is greater than {@code src.length}
     */
    public abstract ByteBuf setBytes(int index, byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index} until the source buffer's position
     * reaches its limit.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.remaining()} is greater than
     *            {@code this.capacity}
     */
    public abstract ByteBuf setBytes(int index, ByteBuffer src);

    /**
     * Transfers the content of the specified source stream to this buffer
     * starting at the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified channel is closed.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract int setBytes(int index, InputStream in, int length) throws IOException;

    /**
     * Transfers the content of the specified source channel to this buffer
     * starting at the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified channel is closed.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int setBytes(int index, ScatteringByteChannel in, int length) throws IOException;

    /**
     * Transfers the content of the specified source channel starting at the given file position
     * to this buffer starting at the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer. This method does not modify the channel's position.
     *
     * @param position the file position at which the transfer is to begin
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified channel is closed.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int setBytes(int index, FileChannel in, long position, int length) throws IOException;

    /**
     * Fills this buffer with <tt>NUL (0x00)</tt> starting at the specified
     * absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of <tt>NUL</tt>s to write to the buffer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     */
    public abstract ByteBuf setZero(int index, int length);

    /**
     * Writes the specified {@link CharSequence} at the current {@code writerIndex} and increases
     * the {@code writerIndex} by the written bytes.
     *
     * @param index on which the sequence should be written
     * @param sequence to write
     * @param charset that should be used.
     * @return the written number of bytes.
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is not large enough to write the whole sequence
     */
    public abstract int setCharSequence(int index, CharSequence sequence, Charset charset);

    /**
     * Gets a boolean at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 1}
     */
    public abstract boolean readBoolean();

    /**
     * Gets a byte at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 1}
     */
    public abstract byte  readByte();

    /**
     * Gets an unsigned byte at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 1}
     */
    public abstract short readUnsignedByte();

    /**
     * Gets a 16-bit short integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    public abstract short readShort();

    /**
     * Gets a 16-bit short integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    public abstract short readShortLE();

    /**
     * Gets an unsigned 16-bit short integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    public abstract int   readUnsignedShort();

    /**
     * Gets an unsigned 16-bit short integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    public abstract int   readUnsignedShortLE();

    /**
     * Gets a 24-bit medium integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 3}
     */
    public abstract int   readMedium();

    /**
     * Gets a 24-bit medium integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the
     * {@code readerIndex} by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 3}
     */
    public abstract int   readMediumLE();

    /**
     * Gets an unsigned 24-bit medium integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 3}
     */
    public abstract int   readUnsignedMedium();

    /**
     * Gets an unsigned 24-bit medium integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 3}
     */
    public abstract int   readUnsignedMediumLE();

    /**
     * Gets a 32-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    public abstract int   readInt();

    /**
     * Gets a 32-bit integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    public abstract int   readIntLE();

    /**
     * Gets an unsigned 32-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    public abstract long  readUnsignedInt();

    /**
     * Gets an unsigned 32-bit integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    public abstract long  readUnsignedIntLE();

    /**
     * Gets a 64-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 8}
     */
    public abstract long  readLong();

    /**
     * Gets a 64-bit integer at the current {@code readerIndex}
     * in the Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 8}
     */
    public abstract long  readLongLE();

    /**
     * Gets a 2-byte UTF-16 character at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    public abstract char  readChar();

    /**
     * Gets a 32-bit floating point number at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    public abstract float readFloat();

    /**
     * Gets a 32-bit floating point number at the current {@code readerIndex}
     * in Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    public float readFloatLE() {
        return Float.intBitsToFloat(readIntLE());
    }

    /**
     * Gets a 64-bit floating point number at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 8}
     */
    public abstract double readDouble();

    /**
     * Gets a 64-bit floating point number at the current {@code readerIndex}
     * in Little Endian Byte Order and increases the {@code readerIndex}
     * by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 8}
     */
    public double readDoubleLE() {
        return Double.longBitsToDouble(readLongLE());
    }

    /**
     * Transfers this buffer's data to a newly created buffer starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code length}).
     * The returned buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0} and {@code length} respectively.
     *
     * @param length the number of bytes to transfer
     *
     * @return the newly created buffer which contains the transferred bytes
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract ByteBuf readBytes(int length);

    /**
     * Returns a new slice of this buffer's sub-region starting at the current
     * {@code readerIndex} and increases the {@code readerIndex} by the size
     * of the new slice (= {@code length}).
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     *
     * @param length the size of the new slice
     *
     * @return the newly created slice
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract ByteBuf readSlice(int length);

    /**
     * Returns a new retained slice of this buffer's sub-region starting at the current
     * {@code readerIndex} and increases the {@code readerIndex} by the size
     * of the new slice (= {@code length}).
     * <p>
     * Note that this method returns a {@linkplain #retain() retained} buffer unlike {@link #readSlice(int)}.
     * This method behaves similarly to {@code readSlice(...).retain()} except that this method may return
     * a buffer implementation that produces less garbage.
     *
     * @param length the size of the new slice
     *
     * @return the newly created slice
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract ByteBuf readRetainedSlice(int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} until the destination becomes
     * non-writable, and increases the {@code readerIndex} by the number of the
     * transferred bytes.  This method is basically same with
     * {@link #readBytes(ByteBuf, int, int)}, except that this method
     * increases the {@code writerIndex} of the destination by the number of
     * the transferred bytes while {@link #readBytes(ByteBuf, int, int)}
     * does not.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code dst.writableBytes} is greater than
     *            {@code this.readableBytes}
     */
    public abstract ByteBuf readBytes(ByteBuf dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code length}).  This method
     * is basically same with {@link #readBytes(ByteBuf, int, int)},
     * except that this method increases the {@code writerIndex} of the
     * destination by the number of the transferred bytes (= {@code length})
     * while {@link #readBytes(ByteBuf, int, int)} does not.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes} or
     *         if {@code length} is greater than {@code dst.writableBytes}
     */
    public abstract ByteBuf readBytes(ByteBuf dst, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code length}).
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code length} is greater than {@code this.readableBytes}, or
     *         if {@code dstIndex + length} is greater than
     *            {@code dst.capacity}
     */
    public abstract ByteBuf readBytes(ByteBuf dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code dst.length}).
     *
     * @throws IndexOutOfBoundsException
     *         if {@code dst.length} is greater than {@code this.readableBytes}
     */
    public abstract ByteBuf readBytes(byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code length}).
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code length} is greater than {@code this.readableBytes}, or
     *         if {@code dstIndex + length} is greater than {@code dst.length}
     */
    public abstract ByteBuf readBytes(byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} until the destination's position
     * reaches its limit, and increases the {@code readerIndex} by the
     * number of the transferred bytes.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code dst.remaining()} is greater than
     *            {@code this.readableBytes}
     */
    public abstract ByteBuf readBytes(ByteBuffer dst);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * current {@code readerIndex}.
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract ByteBuf readBytes(OutputStream out, int length) throws IOException;

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * current {@code readerIndex}.
     *
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes written out to the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int readBytes(GatheringByteChannel out, int length) throws IOException;

    /**
     * Gets a {@link CharSequence} with the given length at the current {@code readerIndex}
     * and increases the {@code readerIndex} by the given length.
     *
     * @param length the length to read
     * @param charset that should be used
     * @return the sequence
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract CharSequence readCharSequence(int length, Charset charset);

    /**
     * Transfers this buffer's data starting at the current {@code readerIndex}
     * to the specified channel starting at the given file position.
     * This method does not modify the channel's position.
     *
     * @param position the file position at which the transfer is to begin
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes written out to the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int readBytes(FileChannel out, long position, int length) throws IOException;

    /**
     * Increases the current {@code readerIndex} by the specified
     * {@code length} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract ByteBuf skipBytes(int length);

    /**
     * Sets the specified boolean at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 1}
     */
    public abstract ByteBuf writeBoolean(boolean value);

    /**
     * Sets the specified byte at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 1} in this buffer.
     * The 24 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 1}
     */
    public abstract ByteBuf writeByte(int value);

    /**
     * Sets the specified 16-bit short integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
     * in this buffer.  The 16 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 2}
     */
    public abstract ByteBuf writeShort(int value);

    /**
     * Sets the specified 16-bit short integer in the Little Endian Byte
     * Order at the current {@code writerIndex} and increases the
     * {@code writerIndex} by {@code 2} in this buffer.
     * The 16 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 2}
     */
    public abstract ByteBuf writeShortLE(int value);

    /**
     * Sets the specified 24-bit medium integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 3}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 3}
     */
    public abstract ByteBuf writeMedium(int value);

    /**
     * Sets the specified 24-bit medium integer at the current
     * {@code writerIndex} in the Little Endian Byte Order and
     * increases the {@code writerIndex} by {@code 3} in this
     * buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 3}
     */
    public abstract ByteBuf writeMediumLE(int value);

    /**
     * Sets the specified 32-bit integer at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 4}
     */
    public abstract ByteBuf writeInt(int value);

    /**
     * Sets the specified 32-bit integer at the current {@code writerIndex}
     * in the Little Endian Byte Order and increases the {@code writerIndex}
     * by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 4}
     */
    public abstract ByteBuf writeIntLE(int value);

    /**
     * Sets the specified 64-bit long integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 8}
     */
    public abstract ByteBuf writeLong(long value);

    /**
     * Sets the specified 64-bit long integer at the current
     * {@code writerIndex} in the Little Endian Byte Order and
     * increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 8}
     */
    public abstract ByteBuf writeLongLE(long value);

    /**
     * Sets the specified 2-byte UTF-16 character at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
     * in this buffer.  The 16 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 2}
     */
    public abstract ByteBuf writeChar(int value);

    /**
     * Sets the specified 32-bit floating point number at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 4}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 4}
     */
    public abstract ByteBuf writeFloat(float value);

    /**
     * Sets the specified 32-bit floating point number at the current
     * {@code writerIndex} in Little Endian Byte Order and increases
     * the {@code writerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 4}
     */
    public ByteBuf writeFloatLE(float value) {
        return writeIntLE(Float.floatToRawIntBits(value));
    }

    /**
     * Sets the specified 64-bit floating point number at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 8}
     */
    public abstract ByteBuf writeDouble(double value);

    /**
     * Sets the specified 64-bit floating point number at the current
     * {@code writerIndex} in Little Endian Byte Order and increases
     * the {@code writerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is less than {@code 8}
     */
    public ByteBuf writeDoubleLE(double value) {
        return writeLongLE(Double.doubleToRawLongBits(value));
    }

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} until the source buffer becomes
     * unreadable, and increases the {@code writerIndex} by the number of
     * the transferred bytes.  This method is basically same with
     * {@link #writeBytes(ByteBuf, int, int)}, except that this method
     * increases the {@code readerIndex} of the source buffer by the number of
     * the transferred bytes while {@link #writeBytes(ByteBuf, int, int)}
     * does not.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code src.readableBytes} is greater than
     *            {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(ByteBuf src);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code length}).  This method
     * is basically same with {@link #writeBytes(ByteBuf, int, int)},
     * except that this method increases the {@code readerIndex} of the source
     * buffer by the number of the transferred bytes (= {@code length}) while
     * {@link #writeBytes(ByteBuf, int, int)} does not.
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.writableBytes} or
     *         if {@code length} is greater then {@code src.readableBytes}
     */
    public abstract ByteBuf writeBytes(ByteBuf src, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code length}).
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code srcIndex + length} is greater than
     *            {@code src.capacity}, or
     *         if {@code length} is greater than {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(ByteBuf src, int srcIndex, int length);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code src.length}).
     *
     * @throws IndexOutOfBoundsException
     *         if {@code src.length} is greater than {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code length}).
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code srcIndex + length} is greater than
     *            {@code src.length}, or
     *         if {@code length} is greater than {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} until the source buffer's position
     * reaches its limit, and increases the {@code writerIndex} by the
     * number of the transferred bytes.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code src.remaining()} is greater than
     *            {@code this.writableBytes}
     */
    public abstract ByteBuf writeBytes(ByteBuffer src);

    /**
     * Transfers the content of the specified stream to this buffer
     * starting at the current {@code writerIndex} and increases the
     * {@code writerIndex} by the number of the transferred bytes.
     *
     * @param length the number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified stream
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.writableBytes}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    public abstract int  writeBytes(InputStream in, int length) throws IOException;

    /**
     * Transfers the content of the specified channel to this buffer
     * starting at the current {@code writerIndex} and increases the
     * {@code writerIndex} by the number of the transferred bytes.
     *
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.writableBytes}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int writeBytes(ScatteringByteChannel in, int length) throws IOException;

    /**
     * Transfers the content of the specified channel starting at the given file position
     * to this buffer starting at the current {@code writerIndex} and increases the
     * {@code writerIndex} by the number of the transferred bytes.
     * This method does not modify the channel's position.
     *
     * @param position the file position at which the transfer is to begin
     * @param length the maximum number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.writableBytes}
     * @throws IOException
     *         if the specified channel threw an exception during I/O
     */
    public abstract int writeBytes(FileChannel in, long position, int length) throws IOException;

    /**
     * Fills this buffer with <tt>NUL (0x00)</tt> starting at the current
     * {@code writerIndex} and increases the {@code writerIndex} by the
     * specified {@code length}.
     *
     * @param length the number of <tt>NUL</tt>s to write to the buffer
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.writableBytes}
     */
    public abstract ByteBuf writeZero(int length);

    /**
     * Writes the specified {@link CharSequence} at the current {@code writerIndex} and increases
     * the {@code writerIndex} by the written bytes.
     * in this buffer.
     *
     * @param sequence to write
     * @param charset that should be used
     * @return the written number of bytes
     * @throws IndexOutOfBoundsException
     *         if {@code this.writableBytes} is not large enough to write the whole sequence
     */
    public abstract int writeCharSequence(CharSequence sequence, Charset charset);

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search takes place from the specified {@code fromIndex}
     * (inclusive)  to the specified {@code toIndex} (exclusive).
     * <p>
     * If {@code fromIndex} is greater than {@code toIndex}, the search is
     * performed in a reversed order.
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the absolute index of the first occurrence if found.
     *         {@code -1} otherwise.
     */
    public abstract int indexOf(int fromIndex, int toIndex, byte value); // 指定值( value ) 在 ByteBuf 中的位置

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search takes place from the current {@code readerIndex}
     * (inclusive) to the current {@code writerIndex} (exclusive).
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the number of bytes between the current {@code readerIndex}
     *         and the first occurrence if found. {@code -1} otherwise.
     */
    public abstract int bytesBefore(byte value);

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search starts from the current {@code readerIndex}
     * (inclusive) and lasts for the specified {@code length}.
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the number of bytes between the current {@code readerIndex}
     *         and the first occurrence if found. {@code -1} otherwise.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    public abstract int bytesBefore(int length, byte value);

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search starts from the specified {@code index} (inclusive)
     * and lasts for the specified {@code length}.
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the number of bytes between the specified {@code index}
     *         and the first occurrence if found. {@code -1} otherwise.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code index + length} is greater than {@code this.capacity}
     */
    public abstract int bytesBefore(int index, int length, byte value);

    /**
     * Iterates over the readable bytes of this buffer with the specified {@code processor} in ascending order.
     *
     * @return {@code -1} if the processor iterated to or beyond the end of the readable bytes.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public abstract int forEachByte(ByteProcessor processor); // 遍历 ByteBuf ，进行自定义处理

    /**
     * Iterates over the specified area of this buffer with the specified {@code processor} in ascending order.
     * (i.e. {@code index}, {@code (index + 1)},  .. {@code (index + length - 1)})
     *
     * @return {@code -1} if the processor iterated to or beyond the end of the specified area.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public abstract int forEachByte(int index, int length, ByteProcessor processor);

    /**
     * Iterates over the readable bytes of this buffer with the specified {@code processor} in descending order.
     *
     * @return {@code -1} if the processor iterated to or beyond the beginning of the readable bytes.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public abstract int forEachByteDesc(ByteProcessor processor);

    /**
     * Iterates over the specified area of this buffer with the specified {@code processor} in descending order.
     * (i.e. {@code (index + length - 1)}, {@code (index + length - 2)}, ... {@code index})
     *
     *
     * @return {@code -1} if the processor iterated to or beyond the beginning of the specified area.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public abstract int forEachByteDesc(int index, int length, ByteProcessor processor);

    /**
     * Returns a copy of this buffer's readable bytes.  Modifying the content
     * of the returned buffer or this buffer does not affect each other at all.
     * This method is identical to {@code buf.copy(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    public abstract ByteBuf copy(); // 拷贝可读部分的字节数组。独立，互相不影响。

    /**
     * Returns a copy of this buffer's sub-region.  Modifying the content of
     * the returned buffer or this buffer does not affect each other at all.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    public abstract ByteBuf copy(int index, int length);

    /**
     * Returns a slice of this buffer's readable bytes. Modifying the content
     * of the returned buffer or this buffer affects each other's content
     * while they maintain separate indexes and marks.  This method is
     * identical to {@code buf.slice(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     */
    public abstract ByteBuf slice(); // 拷贝可读部分的字节数组。共享，相互影响。

    /**
     * Returns a retained slice of this buffer's readable bytes. Modifying the content
     * of the returned buffer or this buffer affects each other's content
     * while they maintain separate indexes and marks.  This method is
     * identical to {@code buf.slice(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Note that this method returns a {@linkplain #retain() retained} buffer unlike {@link #slice()}.
     * This method behaves similarly to {@code slice().retain()} except that this method may return
     * a buffer implementation that produces less garbage.
     */
    public abstract ByteBuf retainedSlice();

    /**
     * Returns a slice of this buffer's sub-region. Modifying the content of
     * the returned buffer or this buffer affects each other's content while
     * they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     */
    public abstract ByteBuf slice(int index, int length);

    /**
     * Returns a retained slice of this buffer's sub-region. Modifying the content of
     * the returned buffer or this buffer affects each other's content while
     * they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Note that this method returns a {@linkplain #retain() retained} buffer unlike {@link #slice(int, int)}.
     * This method behaves similarly to {@code slice(...).retain()} except that this method may return
     * a buffer implementation that produces less garbage.
     */
    public abstract ByteBuf retainedSlice(int index, int length);

    /**
     * Returns a buffer which shares the whole region of this buffer.
     * Modifying the content of the returned buffer or this buffer affects
     * each other's content while they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * The reader and writer marks will not be duplicated. Also be aware that this method will
     * NOT call {@link #retain()} and so the reference count will NOT be increased.
     * @return A buffer whose readable content is equivalent to the buffer returned by {@link #slice()}.
     * However this buffer will share the capacity of the underlying buffer, and therefore allows access to all of the
     * underlying content if necessary.
     */
    public abstract ByteBuf duplicate(); // 拷贝整个的字节数组。共享，相互影响

    /**
     * Returns a retained buffer which shares the whole region of this buffer.
     * Modifying the content of the returned buffer or this buffer affects
     * each other's content while they maintain separate indexes and marks.
     * This method is identical to {@code buf.slice(0, buf.capacity())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Note that this method returns a {@linkplain #retain() retained} buffer unlike {@link #slice(int, int)}.
     * This method behaves similarly to {@code duplicate().retain()} except that this method may return
     * a buffer implementation that produces less garbage.
     */
    public abstract ByteBuf retainedDuplicate();

    /**
     * Returns the maximum number of NIO {@link ByteBuffer}s that consist this buffer.  Note that {@link #nioBuffers()}
     * or {@link #nioBuffers(int, int)} might return a less number of {@link ByteBuffer}s.
     *
     * @return {@code -1} if this buffer has no underlying {@link ByteBuffer}.
     *         the number of the underlying {@link ByteBuffer}s if this buffer has at least one underlying
     *         {@link ByteBuffer}.  Note that this method does not return {@code 0} to avoid confusion.
     *
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    public abstract int nioBufferCount();

    /**
     * Exposes this buffer's readable bytes as an NIO {@link ByteBuffer}. The returned buffer
     * either share or contains the copied content of this buffer, while changing the position
     * and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method is identical to {@code buf.nioBuffer(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     * Please note that the returned NIO buffer will not see the changes of this buffer if this buffer
     * is a dynamic buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    // ByteBuf 包含 ByteBuffer 数量。
    // 如果返回 = 1 ，则调用 `#nioBuffer()` 方法，获得 ByteBuf 包含的 ByteBuffer 对象。
    // 如果返回 > 1 ，则调用 `#nioBuffers()` 方法，获得 ByteBuf 包含的 ByteBuffer 数组。
    public abstract ByteBuffer nioBuffer();

    /**
     * Exposes this buffer's sub-region as an NIO {@link ByteBuffer}. The returned buffer
     * either share or contains the copied content of this buffer, while changing the position
     * and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     * Please note that the returned NIO buffer will not see the changes of this buffer if this buffer
     * is a dynamic buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    public abstract ByteBuffer nioBuffer(int index, int length);

    /**
     * Internal use only: Exposes the internal NIO buffer.
     */
    public abstract ByteBuffer internalNioBuffer(int index, int length);

    /**
     * Exposes this buffer's readable bytes as an NIO {@link ByteBuffer}'s. The returned buffer
     * either share or contains the copied content of this buffer, while changing the position
     * and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     * Please note that the returned NIO buffer will not see the changes of this buffer if this buffer
     * is a dynamic buffer and it adjusted its capacity.
     *
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     */
    public abstract ByteBuffer[] nioBuffers();

    /**
     * Exposes this buffer's bytes as an NIO {@link ByteBuffer}'s for the specified index and length
     * The returned buffer either share or contains the copied content of this buffer, while changing
     * the position and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer. Please note that the
     * returned NIO buffer will not see the changes of this buffer if this buffer is a dynamic
     * buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException
     *         if this buffer cannot create a {@link ByteBuffer} that shares the content with itself
     *
     * @see #nioBufferCount()
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     */
    public abstract ByteBuffer[] nioBuffers(int index, int length);

    /**
     * Returns {@code true} if and only if this buffer has a backing byte array.
     * If this method returns true, you can safely call {@link #array()} and
     * {@link #arrayOffset()}.
     */
    public abstract boolean hasArray();

    /**
     * Returns the backing byte array of this buffer.
     *
     * @throws UnsupportedOperationException
     *         if there no accessible backing byte array
     */
    public abstract byte[] array();

    /**
     * Returns the offset of the first byte within the backing byte array of
     * this buffer.
     *
     * @throws UnsupportedOperationException
     *         if there no accessible backing byte array
     */
    public abstract int arrayOffset();

    /**
     * Returns {@code true} if and only if this buffer has a reference to the low-level memory address that points
     * to the backing data.
     */
    public abstract boolean hasMemoryAddress();

    /**
     * Returns the low-level memory address that point to the first byte of ths backing data.
     *
     * @throws UnsupportedOperationException
     *         if this buffer does not support accessing the low-level memory address
     */
    public abstract long memoryAddress();

    /**
     * Decodes this buffer's readable bytes into a string with the specified
     * character set name.  This method is identical to
     * {@code buf.toString(buf.readerIndex(), buf.readableBytes(), charsetName)}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws UnsupportedCharsetException
     *         if the specified character set name is not supported by the
     *         current VM
     */
    public abstract String toString(Charset charset);

    /**
     * Decodes this buffer's sub-region into a string with the specified
     * character set.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     */
    public abstract String toString(int index, int length, Charset charset);

    /**
     * Returns a hash code which was calculated from the content of this
     * buffer.  If there's a byte array which is
     * {@linkplain #equals(Object) equal to} this array, both arrays should
     * return the same value.
     */
    @Override
    public abstract int hashCode();

    /**
     * Determines if the content of the specified buffer is identical to the
     * content of this array.  'Identical' here means:
     * <ul>
     * <li>the size of the contents of the two buffers are same and</li>
     * <li>every single byte of the content of the two buffers are same.</li>
     * </ul>
     * Please note that it does not compare {@link #readerIndex()} nor
     * {@link #writerIndex()}.  This method also returns {@code false} for
     * {@code null} and an object which is not an instance of
     * {@link ByteBuf} type.
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * Compares the content of the specified buffer to the content of this
     * buffer. Comparison is performed in the same manner with the string
     * comparison functions of various languages such as {@code strcmp},
     * {@code memcmp} and {@link String#compareTo(String)}.
     */
    @Override
    public abstract int compareTo(ByteBuf buffer);

    /**
     * Returns the string representation of this buffer.  This method does not
     * necessarily return the whole content of the buffer but returns
     * the values of the key properties such as {@link #readerIndex()},
     * {@link #writerIndex()} and {@link #capacity()}.
     */
    @Override
    public abstract String toString();

    @Override
    public abstract ByteBuf retain(int increment);

    @Override
    public abstract ByteBuf retain();

    @Override
    public abstract ByteBuf touch();

    @Override
    public abstract ByteBuf touch(Object hint);
}
