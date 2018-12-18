package io.netty.channel;

import java.io.Serializable;

/**
 * @author wuping
 * @date 2018/12/18
 */

public interface ChannelId extends Serializable, Comparable<ChannelId> {
    String asShortText();

    String asLongText();
}
