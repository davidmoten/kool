package org.davidmoten.kool.json;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import org.davidmoten.kool.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public final class LazyArrayNode implements Supplier<ArrayNode> {

    private final JsonParser parser;
    private final ObjectMapper mapper;

    LazyArrayNode(JsonParser parser, ObjectMapper mapper) {
        this.parser = parser;
        this.mapper = mapper;
    }

    @Override
    public ArrayNode get() {
        try {
            return (ArrayNode) mapper.readTree(parser);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Stream<JsonNode> values() {
        return Stream.<JsonNode>defer(() -> //
        Stream.generate(emitter -> {
            JsonToken token = parser.nextToken();
            if (token.equals(JsonToken.END_ARRAY)) {
                emitter.onComplete();
            } else {
                TreeNode v = mapper.readTree(parser);
                emitter.onNext((JsonNode) v);
            }
        })).doOnDispose(() -> parser.close());
    }

    public <T> Stream<T> values(Class<T> cls) {
        return Stream.<T>defer(() -> {
            // skip array start
            parser.nextToken();
            return Stream.generate(emitter -> {
                if (parser.isClosed()) {
                    emitter.onComplete();
                } else {
                    T v = mapper.readValue(parser, cls);
                    if (v == null) {
                        emitter.onComplete();
                    } else {
                        emitter.onNext(v);
                    }
                }
            });
        }).doOnDispose(() -> parser.close());
    }

}
