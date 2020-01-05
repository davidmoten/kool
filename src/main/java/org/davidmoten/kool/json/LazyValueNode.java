package org.davidmoten.kool.json;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ValueNode;

public final class LazyValueNode implements Supplier<ValueNode> {

    private final JsonParser parser;

    LazyValueNode(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public ValueNode get() {
        try {
            return (ValueNode) Util.MAPPER.readTree(parser);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
