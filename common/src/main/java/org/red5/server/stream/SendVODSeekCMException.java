package org.red5.server.stream;

public class SendVODSeekCMException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SendVODSeekCMException(String message) {
        super(message);
    }

    public SendVODSeekCMException(String message, Throwable cause) {
        super(message, cause);
    }
}
