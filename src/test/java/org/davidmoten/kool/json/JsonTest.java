package org.davidmoten.kool.json;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

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

    private static InputStream input(int i) {
        return JsonTest.class.getResourceAsStream("/test" + i + ".json");
    }

}
