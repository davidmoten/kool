package org.davidmoten.kool;

import java.io.IOException;
import java.io.InputStream;

public class To {

    public static InputStream toInputStream(Stream<? extends byte[]> stream) {
        return new InputStream() {

            StreamIterator<? extends byte[]> it = stream.iteratorNullChecked();
            byte[] bytes = new byte[0];
            int index;

            @Override
            public int read() throws IOException {
                load();
                if (bytes == null) {
                    return -1;
                } else {
                    return bytes[index++] & 0xFF;
                }
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                load();
                if (bytes == null) {
                    return -1;
                } else {
                    int length = Math.min(len, bytes.length - index);
                    System.arraycopy(bytes, index, b, off, length);
                    index += length;
                    return length;
                }
            }

            private void load() {
                if (bytes != null && index == bytes.length) {
                    while (it.hasNext()) {
                        bytes = it.nextNullChecked();
                        if (bytes.length > 0) {
                            index = 0;
                            return;
                        }
                    }
                    bytes = null;
                    it.dispose();
                }
            }

        };
    }

}
