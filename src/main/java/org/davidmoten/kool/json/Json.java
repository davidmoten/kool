package org.davidmoten.kool.json;

import java.io.InputStream;
import java.io.Reader;
import java.util.concurrent.Callable;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Json {

    private static final JsonFactory FACTORY = new JsonFactory().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

    private final Stream<JsonParser> stream;
    private ObjectMapper mapper = new ObjectMapper();

    public static Json stream(Callable<InputStream> in) {
        return new Json(Stream.using(in, //
                is -> streamFrom(factory -> factory.createParser(is)), //
                is -> {}));
    }
    
    public static Json stream(InputStream in) {
        return new Json(streamFrom(factory -> factory.createParser(in)));
    }
    
    public static Json stream(Reader reader) {
        return new Json(streamFrom(factory -> factory.createParser(reader)));
    }
    
    public static Json stream(String text) {
        return new Json(streamFrom(factory -> factory.createParser(text)));
    }
    
    public static Json stream(Function<? super JsonFactory, ? extends JsonParser> creator) {
        return new Json(streamFrom(creator));
    }
    
    public Json withMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }
    
    private static Stream<JsonParser> streamFrom(
            Function<? super JsonFactory, ? extends JsonParser> creator) {
        return Stream.generate(() -> {
            return creator.apply(FACTORY);
        }, (p, emitter) -> {
            if (p.nextToken() != null) {
                emitter.onNext(p);
            } else {
                emitter.onComplete();
            }
        });
    }

    private Json(Stream<JsonParser> flowable) {
        this.stream = flowable;
    }

    public Json field(String name) {
        return new Json(Stream.defer(() -> {
            int[] depth = new int[1];
            return stream //
                    .doOnNext(p -> {
                        JsonToken t = p.currentToken();
                        if (t == JsonToken.START_OBJECT || t == JsonToken.START_ARRAY) {
                            depth[0]++;
                        } else if (t == JsonToken.END_OBJECT || t == JsonToken.END_ARRAY) {
                            depth[0]--;
                        }
                    }) //
                    .skipWhile(p -> !(p.currentToken() == JsonToken.FIELD_NAME //
                            && p.currentName().equals(name) //
                            && depth[0] == 1)) //
                    .takeUntil(p -> depth[0] == 0);
        }));
    }

    public static String indent(int n) {
        StringBuilder s = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            s.append("  ");
        }
        return s.toString();
    }

    public Stream<JsonParser> get() {
        return stream;
    }

    public JsonArray fieldArray(String name) {
        return new JsonArray(field(name).stream, mapper);
    }

    public Maybe<LazyObjectNode> objectNode() {
        return node_(t -> t == JsonToken.START_OBJECT) //
                .map(p -> new LazyObjectNode(p));
    }

    public Maybe<LazyValueNode> valueNode() {
        // TODO make test more efficient?
        return node_(t -> t.name().startsWith("VALUE")) //
                .map(p -> new LazyValueNode(p));
    }

    public Maybe<LazyTreeNode> node() {
        return node_(t -> true) //
                .map(p -> new LazyTreeNode(p));
    }

    public Maybe<LazyArrayNode> arrayNode() {
        return node_(t -> t == JsonToken.START_ARRAY) //
                .map(p -> new LazyArrayNode(p, mapper));
    }
    
    private Maybe<JsonParser> node_(Predicate<JsonToken> predicate) {
        return stream //
                .skipWhile(p -> p.currentToken() == JsonToken.FIELD_NAME) //
                .filter(p -> predicate.test(p.currentToken())) //
                .first();
    }

}
