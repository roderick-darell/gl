package org.red5.server.stream;

import org.red5.server.api.stream.IPlayItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.red5.server.stream.PlayEngine;

public class PlaybackDecisionResolver {
    static final int PLAY_DECISION_LIVE = 0;

    static final int PLAY_DECISION_VOD = 1;

    static final int PLAY_DECISION_WAIT = 2;

    static final int PLAY_DECISION_NOT_FOUND = 3;

    private static final Logger log = LoggerFactory.getLogger(PlayEngine.class);

    int toPlayType(IPlayItem item) {
        int type = (int) (item.getStart() / 1000);
        log.debug("Type {}", type);
        return type;
    }

    PlayEngine.PlayDecision determinePlayDecision(int type, IProviderService.INPUT_TYPE sourceType) {
        return switch (type) {
            case -2 -> determinePlayDecisionForLiveOrRecorded(sourceType);
            case -1 -> determinePlayDecisionForLiveOnly(sourceType);
            case 0 -> determinePlayDecisionForZeroStart(sourceType);
            default -> determinePlayDecisionForRecordedOnly(sourceType);
        };
    }

    PlayEngine.PlayDecision determinePlayDecisionForLiveOrRecorded(IProviderService.INPUT_TYPE sourceType) {
        if (sourceType == IProviderService.INPUT_TYPE.LIVE) {
            return PlayEngine.PlayDecision.LIVE;
        }
        if (sourceType == IProviderService.INPUT_TYPE.VOD) {
            return PlayEngine.PlayDecision.VOD;
        }
        if (sourceType == IProviderService.INPUT_TYPE.LIVE_WAIT) {
            return PlayEngine.PlayDecision.WAIT;
        }
        return PlayEngine.PlayDecision.NOT_FOUND;
    }

    PlayEngine.PlayDecision determinePlayDecisionForLiveOnly(IProviderService.INPUT_TYPE sourceType) {
        if (sourceType == IProviderService.INPUT_TYPE.LIVE) {
            return PlayEngine.PlayDecision.LIVE;
        }
        if (sourceType == IProviderService.INPUT_TYPE.LIVE_WAIT) {
            return PlayEngine.PlayDecision.WAIT;
        }
        return PlayEngine.PlayDecision.NOT_FOUND;
    }

    PlayEngine.PlayDecision determinePlayDecisionForZeroStart(IProviderService.INPUT_TYPE sourceType) {
        if (sourceType == IProviderService.INPUT_TYPE.LIVE) {
            return PlayEngine.PlayDecision.LIVE;
        }
        if (sourceType == IProviderService.INPUT_TYPE.VOD) {
            return PlayEngine.PlayDecision.VOD;
        }
        return PlayEngine.PlayDecision.NOT_FOUND;
    }

    PlayEngine.PlayDecision determinePlayDecisionForRecordedOnly(IProviderService.INPUT_TYPE sourceType) {
        return sourceType == IProviderService.INPUT_TYPE.VOD ? PlayEngine.PlayDecision.VOD : PlayEngine.PlayDecision.NOT_FOUND;
    }
}
