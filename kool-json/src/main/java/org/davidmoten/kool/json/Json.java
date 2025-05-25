package org.davidmoten.kool.json;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davidmoten.guavamini.Lists;

public final class Json {

    private static final JsonFactory FACTORY_AUTO_CLOSE_OFF = new JsonFactory().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    private static final JsonFactory FACTORY_AUTO_CLOSE_ON = new JsonFactory().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

    private final Stream<JsonParser> stream;
    private ObjectMapper mapper = new ObjectMapper();
    
    private static final List<JsonToken> VALUE_TOKENS = Lists.newArrayList(JsonToken.VALUE_STRING,
            JsonToken.VALUE_EMBEDDED_OBJECT, JsonToken.VALUE_FALSE, JsonToken.VALUE_NULL, JsonToken.VALUE_NUMBER_FLOAT,
            JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_TRUE);

    // Note that it's a bad idea to provide a stream(Callable<InputStream>) method
    // because the responsibility for closing the InputStream would rest
    // with this library. The fact that a stateful JsonParser is emitted by methods
    // on this stream means that the InputStream could be closed before the parser
    // has read stuff (depending on what operators are applied to the stream). For
    // this reason InputStream closure is best handled by the client than by this
    // library.

    public static Json stream(InputStreamFactory inputStreamFactory) {
        return new Json(streamFrom(FACTORY_AUTO_CLOSE_ON, factory -> factory.createParser(inputStreamFactory.call())));
    }
    
    public static Json stream(ReaderFactory readerFactory) {
        return new Json(streamFrom(FACTORY_AUTO_CLOSE_ON, factory -> factory.createParser(readerFactory.call())));
    }
    
    public static Json stream(InputStream in) {
        return new Json(streamFrom(FACTORY_AUTO_CLOSE_OFF, factory -> factory.createParser(in)));
    }

    public static Json stream(Reader reader) {
        return new Json(streamFrom(FACTORY_AUTO_CLOSE_OFF,factory -> factory.createParser(reader)));
    }

    public static Json stream(String text) {
        return new Json(streamFrom(FACTORY_AUTO_CLOSE_OFF,factory -> factory.createParser(text)));
    }

    public static Json stream(Function<? super JsonFactory, ? extends JsonParser> creator) {
        return new Json(streamFrom(FACTORY_AUTO_CLOSE_OFF, creator));
    }

    public Json withMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    private static Stream<JsonParser> streamFrom(JsonFactory factory, Function<? super JsonFactory, ? extends JsonParser> creator) {
        return Stream.generate(() -> creator.apply(factory), //
                (p, emitter) -> {
                    if (p.nextToken() != null) {
                        emitter.onNext(p);
                    } else {
                        emitter.onComplete();
                    }
                });
    }

    private Json(Stream<JsonParser> stream) {
        this.stream = stream;
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

    public Stream<JsonParser> get() {
        return stream;
    }

    public JsonArray fieldArray(String name) {
        return new JsonArray(field(name).stream, mapper);
    }

    public Maybe<LazyObjectNode> objectNode() {
        return node_(t -> t == JsonToken.START_OBJECT) //
                .map(LazyObjectNode::new);
    }

    public Maybe<LazyValueNode> valueNode() {
        return node_(VALUE_TOKENS::contains) //
                .map(LazyValueNode::new);
    }

    public Maybe<LazyTreeNode> node() {
        return node_(t -> true) //
                .map(LazyTreeNode::new);
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
