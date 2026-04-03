package org.red5.server.stream;

import org.red5.server.api.stream.StreamState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaybackJobController {
    private static final Logger log = LoggerFactory.getLogger(PlayEngine.class);

    private final PlayEngine playEngine;

    PlaybackJobController(PlayEngine playEngine) {
        this.playEngine = playEngine;
    }

    /**
     * Make sure the pull and push processing is running.
     */
    void ensurePullAndPushRunning() {
        log.trace("State should be PLAYING to running this task: {}", playEngine.getSubscriberStreamInternal().getState());
        if (playEngine.isPullModeInternal() && playEngine.getPullAndPushInternal() == null && playEngine.getSubscriberStreamInternal().getState() == StreamState.PLAYING) {
            playEngine.setPullAndPushInternal(playEngine.schedulePullAndPushJobInternal());
        }
    }

    /**
     * Clear all scheduled waiting jobs
     */
    void clearWaitJobs() {
        log.debug("Clear wait jobs");
        if (playEngine.getPullAndPushInternal() != null) {
            playEngine.getSubscriberStreamInternal().cancelJob(playEngine.getPullAndPushInternal());
            playEngine.releasePendingMessageInternal();
            playEngine.setPullAndPushInternal(null);
        }
        if (playEngine.getWaitLiveJobInternal() != null) {
            playEngine.getSchedulingServiceInternal().removeScheduledJob(playEngine.getWaitLiveJobInternal());
            playEngine.setWaitLiveJobInternal(null);
        }
    }

    /**
     * Schedule a stop to be run from a separate thread to allow the background thread to stop cleanly.
     */
    void runDeferredStop() {
        clearWaitJobs();
        log.trace("Ran deferred stop");
        if (playEngine.getDeferredStopInternal() == null) {
            playEngine.setDeferredStopInternal(playEngine.scheduleDeferredStopJobInternal());
        }
    }

    void cancelDeferredStop() {
        log.debug("Cancel deferred stop");
        if (playEngine.getDeferredStopInternal() != null) {
            playEngine.getSubscriberStreamInternal().cancelJob(playEngine.getDeferredStopInternal());
            playEngine.setDeferredStopInternal(null);
        }
    }

}
