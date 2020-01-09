package org.davidmoten.kool;

public interface Emitter<T> {
    
    void onNext(T t);
    
    void onComplete();

}
