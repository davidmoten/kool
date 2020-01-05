package org.davidmoten.kool.json;


import java.util.concurrent.atomic.AtomicInteger;

import org.davidmoten.kool.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;


public final class JsonArray {

    private final Stream<JsonParser> stream;

    JsonArray(Stream<JsonParser> flowable) {
        this.stream = flowable;
    }

    public <T> Stream<LazyTreeNode> nodes() {
        return nodes_().map(p -> Util.MAPPER.readTree(p));
    }

    public Stream<LazyObjectNode> objectNodes() {
        return nodes_().map(p -> new LazyObjectNode(p));
    }

    public Stream<LazyValueNode> valueNodes() {
        return nodes_().map(p -> new LazyValueNode(p));
    }

    public Stream<LazyArrayNode> arrayNodes() {
        return nodes_().map(p -> new LazyArrayNode(p));
    }

    public Stream<JsonNode> field(String name) {
        return objectNodes().map(on -> on.get().get(name));
    }

    private Stream<JsonParser> nodes_() {
        return Stream.defer(() -> {
            // TODO use single element array instead of AtomicXXX
            AtomicInteger depth = new AtomicInteger();
            return stream //
                    .doOnNext(p -> {
                        JsonToken t = p.currentToken();
                        if (t == JsonToken.START_OBJECT || t == JsonToken.START_ARRAY) {
                            depth.incrementAndGet();
                        } else if (t == JsonToken.END_OBJECT || t == JsonToken.END_ARRAY) {
                            depth.decrementAndGet();
                        }
                    }) //
                    .skipWhile(p -> p.currentToken() == JsonToken.FIELD_NAME
                            || p.currentToken() == JsonToken.START_ARRAY) //
                    .takeUntil(p -> depth.get() == 0) //
                    .filter(p -> p.currentToken() != JsonToken.END_ARRAY
                            && p.currentToken() != JsonToken.END_OBJECT); //
        });
    }

}
