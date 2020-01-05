package org.davidmoten.kool.json;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ArrayNode;

public final class LazyArrayNode implements Supplier<ArrayNode> {

    private final JsonParser parser;

    LazyArrayNode(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public ArrayNode get() {
        try {
            return (ArrayNode) Util.MAPPER.readTree(parser);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
