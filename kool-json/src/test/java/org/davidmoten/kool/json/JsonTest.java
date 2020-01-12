package org.davidmoten.kool.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonTest {

    @Test
    public void testParseJson() throws JsonParseException, IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser p = factory.createParser(new BufferedInputStream(input(1), 4));
        while (p.nextToken() != null) {
            System.out.println(p.currentToken() + ": " + p.getCurrentName() + "=" + p.getText());
        }
    }

    @Test
    public void testStream() {
        Json.stream(input(1)) //
                .field("menu") //
                .field("popup") //
                .fieldArray("menuItem") //
                .field("value") //
                .map(JsonNode::asText) // s
                .test() //
                .assertValues("New", "Open", "Close");
    }

    @Test
    public void testStreamObjectNode() {
        Json.stream(input(1)) //
                .field("menu") //
                .objectNode() //
                .map(on -> on.get().fieldNames().next()) //
                .test() //
                .assertValue("id");
    }

    @Test
    public void testStreamValueNode() {
        Json.stream(input(1)) //
                .field("menu") //
                .field("id") //
                .valueNode() //
                .map(n -> n.get().asText()) //
                .test() //
                .assertValue("file");
    }

    @Test
    public void testFlowableNestedArrays() {
        Json.stream(input(1)) //
                .fieldArray("menu") //
                .field("id") //
                .map(n -> n.asText()) //
                .test() //
                .assertValues("file");
    }

    @Test
    public void testBookStream() {
        Json.stream(input(3)) //
                .fieldArray("books") //
                .field("author") //
                .map(node -> node.asText()) //
                .distinct() //
                .count() //
                .test() //
                .assertValue(8L);
    }

    @Test
    public void testArrayStream() {
        Json.stream(input(4)) //
                .arrayNode() //
                .flatMap(node -> node.values()) //
                .map(node -> node.get("aqi_pm2_5").asInt()) //
                .test() //
                .assertValues(3721, 3664, 3566, 3463);
    }

    @Test
    public void testArrayStreamOnEmptyArray() {
        Json.stream("[]") //
                .arrayNode() //
                .flatMap(node -> node.values()) //
                .test().assertNoValuesOnly();
    }

    @Test
    public void testArrayStreamMappedToClass() {
        Json.stream(input(4)) //
                .arrayNode() //
                .flatMap(node -> node.values(Record.class)) //
                .map(x -> x.pm25) //
                .test().assertValues(3721, 3664, 3566, 3463);
    }

    @Test
    public void testArrayStreamMappedToClassOnEmptyArray() {
        Json.stream("[]") //
                .arrayNode() //
                .flatMap(node -> node.values(Record.class)) //
                .map(x -> x.pm25) //
                .test().assertNoValuesOnly();
    }

    @Test
    public void testInputBiggerThanParserBuffer() throws JsonParseException, IOException {
        InputStream in = new InputStream() {
            byte[] bytes = "[{\"datetime\":\"2020-01-01T14:00:00.000\",\"aqi_pm2_5\":\"3463\"}"
                    .getBytes(StandardCharsets.UTF_8);
            int index = 0;
            long count = 0;

            @Override
            public int read() throws IOException {
                if (bytes == null) {
                    return -1;
                }
                if (index == bytes.length) {
                    if (count > 100000000) {
                        bytes = null;
                        return (int) ']';
                    } else {
                        index = 1;
                        return (int) ',';
                    }
                } else {
                    int v = (int) bytes[index];
                    index++;
                    count++;
                    return v;
                }
            }

            @Override
            public void close() throws IOException {
                index = 0;
            }
        };

        Json.stream(in) //
                .arrayNode() //
                .flatMap(node -> node.values()) //
                .map(x -> x.get("datetime").asText()) //
                .last() //
                .test() //
                .assertPresent();
    }

    @Test
    public void testDoesNotCloseInputStream() {
        WrappedInputStream w = new WrappedInputStream(input(4));
        Json.stream(w).arrayNode() //
                .flatMap(node -> node.values()) //
                .count() //
                .test() //
                .assertValueOnly(4L);
        assertFalse(w.closed);
    }

    @Test
    public void testUsingFactoryMethodDoesCloseInputStream() throws IOException {
        try (WrappedInputStream w = new WrappedInputStream(input(4))) {
            Json.stream(() -> w) //
                    .arrayNode() //
                    .flatMap(node -> node.values()) //
                    .count() //
                    .test() //
                    .assertValueOnly(4L);
            assertTrue(w.closed);
        }
    }

    @Test
    public void testDisposalNotCalledIfInputStreamPassedDirectly() throws IOException {
        try (WrappedInputStream w = new WrappedInputStream(input(4))) {
            Json.stream(w) //
                    .arrayNode() //
                    .flatMap(node -> node.values()) //
                    .count() //
                    .test() //
                    .assertValueOnly(4L);
            assertFalse(w.closed);
        }
    }

    @Test
    public void testUsingReaderFactoryMethodDoesCloseInputStream() throws IOException {
        try (WrappedInputStream w = new WrappedInputStream(input(4));
                Reader r = new InputStreamReader(w, StandardCharsets.UTF_8)) {
            Json.stream(() -> r) //
                    .arrayNode() //
                    .flatMap(node -> node.values()) //
                    .count() //
                    .test() //
                    .assertValueOnly(4L);
            assertTrue(w.closed);
        }
    }

    @Test
    public void testDisposalNotCalledIfReaderPassedDirectly() throws IOException {
        try (WrappedInputStream w = new WrappedInputStream(input(4));
                Reader r = new InputStreamReader(w, StandardCharsets.UTF_8)) {
            Json.stream(r) //
                    .arrayNode() //
                    .flatMap(node -> node.values()) //
                    .count() //
                    .test() //
                    .assertValueOnly(4L);
            assertFalse(w.closed);
        }
    }

    private static final class WrappedInputStream extends InputStream {

        private final InputStream in;
        boolean closed;

        WrappedInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public void close() throws IOException {
            in.close();
            closed = true;
        }
    }

    static final class Record {
        @JsonProperty("datetime")
        String datetime;

        @JsonProperty("aqi_pm2_5")
        Integer pm25;
    }

    private static InputStream input(int i) {
        return JsonTest.class.getResourceAsStream("/test" + i + ".json");
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

}
