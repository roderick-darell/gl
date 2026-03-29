package org.red5.server.net.rtmp.event;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.event.IEventListener;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.net.rtmp.message.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDataEvent extends BaseEvent {

    protected BaseDataEvent(Type type) {
        super(type);
    }

    protected BaseDataEvent() {
        super();
    }

    protected void initializeData(IoBuffer data, boolean copy) {
        if (copy) {
            byte[] array = new byte[data.remaining()];
            data.mark();
            data.get(array);
            data.reset();
            applyData(array);
        } else {
            applyData(data);
        }
    }

    protected abstract void applyData(byte[] data);

    protected abstract void applyData(IoBuffer data);

}
