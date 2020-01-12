package org.davidmoten.kool.json;

import java.io.Reader;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface ReaderFactory extends Callable<Reader> {

}
