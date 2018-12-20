package io.netty.channel;

/**
 * @author wuping
 * @date 2018/12/20
 */

public final class ChannelMetadata {
    private final boolean hasDisconnect;
    private final int defaultMaxMessagesPerRead;

    public ChannelMetadata(boolean hasDisconnect) {
        this(hasDisconnect, 1);
    }

    public ChannelMetadata(boolean hasDisconnect, int defaultMaxMessagesPerRead) {
        if (defaultMaxMessagesPerRead <= 0) {
            throw new IllegalArgumentException("defaultMaxMessagesPerRead: " + defaultMaxMessagesPerRead +
                    " (expected > 0)");
        }
        this.hasDisconnect = hasDisconnect;
        this.defaultMaxMessagesPerRead = defaultMaxMessagesPerRead;
    }

    public boolean hasDisconnect() {
        return hasDisconnect;
    }

    public int defaultMaxMessagesPerRead() {
        return defaultMaxMessagesPerRead;
    }
}
