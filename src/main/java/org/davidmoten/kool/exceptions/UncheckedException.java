package org.davidmoten.kool.exceptions;

public final class UncheckedException extends RuntimeException {

    private static final long serialVersionUID = 764599058749051955L;

    public UncheckedException(Throwable e) {
        super (e);
    }
    
}
