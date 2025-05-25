package org.davidmoten.kool.json;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class LazyObjectNode implements Supplier<ObjectNode> {

    private final JsonParser p;

    LazyObjectNode(JsonParser p) {
        this.p = p;
    }

    @Override
    public ObjectNode get() {
        try {
            return Util.MAPPER.readTree(p);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
