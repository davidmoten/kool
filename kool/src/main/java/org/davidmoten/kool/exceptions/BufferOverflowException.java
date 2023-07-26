package org.davidmoten.kool.exceptions;

public final class BufferOverflowException extends RuntimeException {

    private static final long serialVersionUID = -2916346877529691082L;

    public BufferOverflowException(String message) {
        super(message);
    }

}
