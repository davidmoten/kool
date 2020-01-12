package org.davidmoten.kool.json;

import java.io.InputStream;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface InputStreamFactory extends Callable<InputStream> {

}
