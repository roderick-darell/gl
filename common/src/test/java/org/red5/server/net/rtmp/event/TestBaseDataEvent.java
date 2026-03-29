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
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBaseDataEvent {
    @Test
    public void shouldApplyByteArrayWhenCopyIsTrue() {
        IoBuffer buffer = IoBuffer.wrap(new byte[] { 1, 2, 3, 4 });

        TestDataEvent event = new TestDataEvent();
        event.initialize(buffer, true);

        assertNotNull(event.byteArrayData);
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, event.byteArrayData);
        assertNull(event.bufferData);
        assertEquals(0, buffer.position());
    }

    @Test
    public void shouldApplyIoBufferWhenCopyIsFalse() {
        IoBuffer buffer = IoBuffer.wrap(new byte[] { 5, 6, 7, 8 });

        TestDataEvent event = new TestDataEvent();
        event.initialize(buffer, false);

        assertNull(event.byteArrayData);
        assertSame(buffer, event.bufferData);
        assertEquals(0, buffer.position());
    }

    private static class TestDataEvent extends BaseDataEvent {

        private byte[] byteArrayData;

        private IoBuffer bufferData;

        TestDataEvent() {
            super(Type.STREAM_DATA);
        }

        void initialize(IoBuffer data, boolean copy) {
            initializeData(data, copy);
        }

        @Override
        protected void applyData(byte[] data) {
            this.byteArrayData = data;
        }

        @Override
        protected void applyData(IoBuffer data) {
            this.bufferData = data;
        }

        @Override
        public byte getDataType() {
            return TYPE_NOTIFY; // valeur simple pour le test
        }

        @Override
        protected void releaseInternal() {
            // rien à faire pour le test
        }
    }

}
