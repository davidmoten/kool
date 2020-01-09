package org.davidmoten.kool.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.davidmoten.kool.Stream;
import org.junit.Test;

public final class StreamUtilsTest {

    @Test
    public void testToInputStreamEmptyRead() throws IOException {
        InputStream in = StreamUtils.toInputStream(Stream.empty());
        assertEquals(-1, in.read());
        assertEquals(-1, in.read());
    }

    @Test
    public void testToInputStreamEmptyReadByteArray() throws IOException {
        InputStream in = StreamUtils.toInputStream(Stream.empty());
        byte[] bytes = new byte[3];
        assertEquals(-1, in.read(bytes));
        assertEquals(-1, in.read(bytes));
    }

    @Test
    public void testStreamInputStream() throws IOException {
        InputStream is = Stream.inputStream(
                Stream.bytes(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8))));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            assertEquals("hello there", br.readLine());
            assertNull(br.readLine());
        }
    }

    @Test
    public void testToInputStreamEmptyByteArrayInputs() throws IOException {
        InputStream in = StreamUtils.toInputStream(Stream.of(new byte[] {}, new byte[] {}));
        assertEquals(-1, in.read());
    }

    @Test
    public void testToInputStreamEmptyByteArrayInputs2() throws IOException {
        InputStream in = StreamUtils.toInputStream(Stream.of(new byte[] {}, new byte[] {}));
        byte[] bytes = new byte[3];
        assertEquals(-1, in.read(bytes));
        assertEquals(-1, in.read(bytes));
    }

    @Test
    public void testToInputStreamReadOneElementArraysByByte() throws IOException {
        InputStream in = StreamUtils.toInputStream(Stream.of(new byte[] { 1 }, new byte[] { 2 }));
        assertEquals(1, in.read());
        assertEquals(2, in.read());
        assertEquals(-1, in.read());
    }

    @Test
    public void testToInputStreamReadOneElementArraysByByteArray() throws IOException {
        InputStream in = StreamUtils.toInputStream(Stream.of(new byte[] { 3 }, new byte[] { 4 }));
        byte[] bytes = new byte[3];
        assertEquals(1, in.read(bytes));
        assertEquals(3, bytes[0]);
        assertEquals(1, in.read(bytes));
        assertEquals(4, bytes[0]);
    }

}
