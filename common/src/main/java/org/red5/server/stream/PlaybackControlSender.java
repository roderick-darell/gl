package org.red5.server.stream;

import org.red5.server.api.stream.IPlayItem;
import org.red5.server.messaging.IPassive;
import org.red5.server.messaging.OOBControlMessage;

import java.util.HashMap;
import java.util.Map;

public class PlaybackControlSender {
    private final PlayEngine playEngine;

    PlaybackControlSender(PlayEngine playEngine) {
        this.playEngine = playEngine;
    }

    /**
     * Send VOD init control message
     *
     * @param item
     *            Playlist item
     */
    void sendVODInitCM(IPlayItem item) {
        OOBControlMessage oobCtrlMsg = new OOBControlMessage();
        oobCtrlMsg.setTarget(IPassive.KEY);
        oobCtrlMsg.setServiceName("init");
        Map<String, Object> paramMap = new HashMap<String, Object>(1);
        paramMap.put("startTS", (int) item.getStart());
        oobCtrlMsg.setServiceParamMap(paramMap);
        playEngine.getMessageInputInternal().sendOOBControlMessage(playEngine, oobCtrlMsg);
    }

    /**
     * Send VOD seek control message
     *
     * @param msgIn
     *            Message input
     * @param position
     *            Playlist item
     * @return Out-of-band control message call result or -1 on failure
     */
    int sendVODSeekCM(int position) {
        OOBControlMessage oobCtrlMsg = new OOBControlMessage();
        oobCtrlMsg.setTarget(ISeekableProvider.KEY);
        oobCtrlMsg.setServiceName("seek");
        Map<String, Object> paramMap = new HashMap<String, Object>(1);
        paramMap.put("position", position);
        oobCtrlMsg.setServiceParamMap(paramMap);
        playEngine.getMessageInputInternal().sendOOBControlMessage(playEngine, oobCtrlMsg);
        if (oobCtrlMsg.getResult() instanceof Integer) {
            return (Integer) oobCtrlMsg.getResult();
        } else {
            throw new SendVODSeekCMException("Expected Integer seek result but got: " + oobCtrlMsg.getResult());

        }
    }

    /**
     * Send VOD check video control message
     *
     * @return result of oob control message
     */
    boolean sendCheckVideoCM() {
        OOBControlMessage oobCtrlMsg = new OOBControlMessage();
        oobCtrlMsg.setTarget(IStreamTypeAwareProvider.KEY);
        oobCtrlMsg.setServiceName("hasVideo");
        playEngine.getMessageInputInternal().sendOOBControlMessage(playEngine, oobCtrlMsg);
        if (oobCtrlMsg.getResult() instanceof Boolean) {
            return (Boolean) oobCtrlMsg.getResult();
        } else {
            return false;
        }
    }

}
