package org.davidmoten.kool.internal.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

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
