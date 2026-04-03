package org.red5.server.stream;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.io.amf.Output;
import org.red5.io.utils.ObjectMap;
import org.red5.server.api.stream.IPlayItem;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.status.Status;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PlaybackNotifier {

    private final PlayEngine playEngine;

    private static final Logger log = LoggerFactory.getLogger(PlayEngine.class);

    private static boolean isDebug = log.isDebugEnabled();

    PlaybackNotifier(PlayEngine playEngine) {
        this.playEngine = playEngine;
    }

    /**
     * Send reset status for item
     *
     * @param item
     *            Playlist item
     */
    void sendResetStatus(IPlayItem item) {
        Status reset = new Status(StatusCodes.NS_PLAY_RESET);
        reset.setClientid(playEngine.getStreamIdInternal());
        reset.setDetails(item.getName());
        reset.setDesciption(String.format("Playing and resetting %s.", item.getName()));

        playEngine.pushStatusInternal(reset);
    }

    /**
     * Send pause status notification
     *
     * @param item
     *            Playlist item
     */
    void sendPauseStatus(IPlayItem item) {
        Status pause = new Status(StatusCodes.NS_PAUSE_NOTIFY);
        pause.setClientid(playEngine.getStreamIdInternal());
        pause.setDetails(item.getName());

        playEngine.pushStatusInternal(pause);
    }

    /**
     * Send resume status notification
     *
     * @param item
     *            Playlist item
     */
    void sendResumeStatus(IPlayItem item) {
        Status resume = new Status(StatusCodes.NS_UNPAUSE_NOTIFY);
        resume.setClientid(playEngine.getStreamIdInternal());
        resume.setDetails(item.getName());

        playEngine.pushStatusInternal(resume);
    }

    /**
     * Send published status notification
     *
     * @param item
     *            Playlist item
     */
    void sendPublishedStatus(IPlayItem item) {
        Status published = new Status(StatusCodes.NS_PLAY_PUBLISHNOTIFY);
        published.setClientid(playEngine.getStreamIdInternal());
        published.setDetails(item.getName());

        playEngine.pushStatusInternal(published);
    }

    /**
     * Send unpublished status notification
     *
     * @param item
     *            Playlist item
     */
    void sendUnpublishedStatus(IPlayItem item) {
        Status unpublished = new Status(StatusCodes.NS_PLAY_UNPUBLISHNOTIFY);
        unpublished.setClientid(playEngine.getStreamIdInternal());
        unpublished.setDetails(item.getName());

        playEngine.pushStatusInternal(unpublished);
    }

    /**
     * Stream not found status notification
     *
     * @param item
     *            Playlist item
     */
    void sendStreamNotFoundStatus(IPlayItem item) {
        Status notFound = new Status(StatusCodes.NS_PLAY_STREAMNOTFOUND);
        notFound.setClientid(playEngine.getStreamIdInternal());
        notFound.setLevel(Status.ERROR);
        notFound.setDetails(item.getName());

        playEngine.pushStatusInternal(notFound);
    }

    /**
     * Insufficient bandwidth notification
     *
     * @param item
     *            Playlist item
     */
    void sendInsufficientBandwidthStatus(IPlayItem item) {
        Status insufficientBW = new Status(StatusCodes.NS_PLAY_INSUFFICIENT_BW);
        insufficientBW.setClientid(playEngine.getStreamIdInternal());
        insufficientBW.setLevel(Status.WARNING);
        insufficientBW.setDetails(item.getName());
        insufficientBW.setDesciption("Data is playing behind the normal speed");

        playEngine.pushStatusInternal(insufficientBW);
    }

    /**
     * Sends an onPlayStatus message.
     *
     * http://help.adobe.com/en_US/FlashPlatform/reference/actionscript/3/flash/events/NetDataEvent.html
     *
     * @param code
     * @param duration
     * @param bytes
     */
    void sendOnPlayStatus(String code, int duration, long bytes) {

        if (isDebug) {
            log.debug("Sending onPlayStatus - code: {} duration: {} bytes: {}", code, duration, bytes);
        }
        // create the buffer
        int INITIAL_BUFFER_SIZE = 102;
        IoBuffer buf = IoBuffer.allocate(INITIAL_BUFFER_SIZE);
        buf.setAutoExpand(true);
        Output out = new Output(buf);
        out.writeString("onPlayStatus");
        ObjectMap<Object, Object> args = new ObjectMap<>();
        args.put("code", code);
        args.put("level", Status.STATUS);
        args.put("duration", duration);
        args.put("bytes", bytes);
        IPlayItem currentItem = playEngine.getCurrentItemInternal();
        String name = currentItem != null ? currentItem.getName() : null;
        if (StatusCodes.NS_PLAY_TRANSITION_COMPLETE.equals(code)) {
            args.put("clientId", playEngine.getStreamIdInternal());
            args.put("details", name);
            args.put("description", String.format("Transitioned to %s", name));
            args.put("isFastPlay", false);
        }
        out.writeObject(args);
        buf.flip();
        Notify event = new Notify(buf, "onPlayStatus");
        if (playEngine.getLastMessageTsInternal() > 0) {
            event.setTimestamp(playEngine.getLastMessageTsInternal());
        } else {
            event.setTimestamp(0);
        }
        RTMPMessage msg = RTMPMessage.build(event);

        playEngine.pushMessageInternal(msg);
    }

    /**
     * Send playlist switch status notification
     */
    void sendSwitchStatus() {
        // TODO: find correct duration to send
        long bytesSent = playEngine.getBytesSentInternal();
        sendOnPlayStatus(StatusCodes.NS_PLAY_SWITCH, 1, bytesSent);
    }

    /**
     * Send transition status notification
     */
    void sendTransitionStatus() {
        long bytesSent = playEngine.getBytesSentInternal();
        sendOnPlayStatus(StatusCodes.NS_PLAY_TRANSITION_COMPLETE, 0, bytesSent);
    }

    /**
     * Send playlist complete status notification
     *
     */
    void sendCompleteStatus() {
        long bytesSent = playEngine.getBytesSentInternal();
        // may be the correct duration
        int streamStartTs = playEngine.getStreamStartTsInternal();
        int duration = (playEngine.getLastMessageTsInternal() > 0) ? Math.max(0, playEngine.getLastMessageTsInternal() - playEngine.getStreamStartTsInternal()) : 0;
        if (isDebug) {
            log.debug("sendCompleteStatus - duration: {} bytes sent: {}", duration, bytesSent);
        }

        sendOnPlayStatus(StatusCodes.NS_PLAY_COMPLETE, duration, bytesSent);
    }

}
