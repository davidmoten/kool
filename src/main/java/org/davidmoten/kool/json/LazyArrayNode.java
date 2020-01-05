package org.davidmoten.kool.json;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import org.davidmoten.kool.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
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

    public Stream<JsonNode> values() {
        return Stream.defer(() -> {
            // skip array start
            return Stream.generate(emitter -> {
                JsonToken token = parser.nextToken();
                if (token.equals(JsonToken.END_ARRAY)) {
                    emitter.onComplete();
                } else {
                    TreeNode v = Util.MAPPER.readTree(parser);
                    emitter.onNext((JsonNode) v);
                }
            });
        });
    }
    
    public <T> Stream<T> values(Class<T> cls) {
        return Stream.defer(() -> {
            // skip array start
            return Stream.generate(emitter -> {
                JsonToken token = parser.nextToken();
                if (token.equals(JsonToken.END_ARRAY)) {
                    emitter.onComplete();
                } else {
                    T v = Util.MAPPER.readValue(parser,cls);
                    emitter.onNext(v);
                }
            });
        });
    }

}
