package org.red5.server.stream;

import org.red5.server.api.stream.IPlayItem;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientBufferMonitor {
    private static final Logger log = LoggerFactory.getLogger(ClientBufferMonitor.class);

    private final PlayEngine playEngine;

    ClientBufferMonitor(PlayEngine playEngine) {
        this.playEngine = playEngine;
    }

    /**
     * Check if it's okay to send the client more data. This takes the configured bandwidth as well as the requested client buffer into
     * account.
     *
     * @param message
     * @return true if it is ok to send more, false otherwise
     */
    boolean okayToSendMessage(IRTMPEvent message) {
        if (message instanceof IStreamData) {
            final long now = System.currentTimeMillis();
            // check client buffer size
            if (isClientBufferFull(now)) {
                return false;
            }
            // get pending message count
            long pending = pendingMessages();
            if (playEngine.getBufferCheckIntervalInternal() > 0 && now >= playEngine.getNextCheckBufferUnderrunInternal()) {
                if (pending > playEngine.getUnderrunTriggerInternal()) {
                    IPlayItem currentItem = playEngine.getCurrentItemInternal();
                    if (currentItem != null) {
                        playEngine.getPlaybackNotifierInternal().sendInsufficientBandwidthStatus(currentItem);
                    }
                }
                playEngine.setNextCheckBufferUnderrunInternal(now + playEngine.getBufferCheckIntervalInternal());
            }

            return pending <= playEngine.getUnderrunTriggerInternal();
            // check for under run
            // too many messages already queued on the connection

        } else {
            String itemName = "Undefined";
            // if current item exists get the name to help debug this issue
            if (playEngine.getCurrentItemInternal() != null) {
                itemName = playEngine.getCurrentItemInternal().getName();
            }
            Object[] errorItems = new Object[] { message.getClass(), message.getDataType(), itemName };
            throw new RuntimeException(String.format("Expected IStreamData but got %s (type %s) for %s", errorItems));
        }
    }

    /**
     * Get number of pending messages to be sent
     *
     * @return Number of pending messages
     */
    private long pendingMessages() {
        return playEngine.getSubscriberStreamInternal().getConnection().getPendingMessages();
    }

    /**
     * Estimate client buffer fill.
     *
     * @param now
     *            The current timestamp being used.
     * @return True if it appears that the client buffer is full, otherwise false.
     */
    boolean isClientBufferFull(final long now) {
        // check client buffer length when we've already sent some messages
        if (playEngine.getLastMessageTsInternal() > 0) {
            // duration the stream is playing / playback duration
            final long delta = now - playEngine.getPlaybackStartInternal();
            // buffer size as requested by the client
            final long buffer = playEngine.getSubscriberStreamInternal().getClientBufferDuration();
            // expected amount of data present in client buffer
            final long buffered = playEngine.getLastMessageTsInternal() - delta;
            log.trace("isClientBufferFull: timestamp {} delta {} buffered {} buffer duration {}", new Object[] { playEngine.getLastMessageTsInternal(), delta, buffered, buffer });
            // fix for SN-122, this sends double the size of the client buffer
            if (buffer > 0 && buffered > (buffer * 2)) {
                // client is likely to have enough data in the buffer
                return true;
            }
        }
        return false;
    }

    boolean isClientBufferEmpty() {
        // check client buffer length when we've already sent some messages
        if (playEngine.getLastMessageTsInternal() >= 0) {
            // duration the stream is playing / playback duration
            final long delta = System.currentTimeMillis() - playEngine.getPlaybackStartInternal();
            // expected amount of data present in client buffer
            final long buffered = playEngine.getLastMessageTsInternal() - delta;
            log.trace("isClientBufferEmpty: timestamp {} delta {} buffered {}", new Object[] { playEngine.getLastMessageTsInternal(), delta, buffered });
            if (buffered < 0) {
                return true;
            }
        }
        return false;
    }
}
