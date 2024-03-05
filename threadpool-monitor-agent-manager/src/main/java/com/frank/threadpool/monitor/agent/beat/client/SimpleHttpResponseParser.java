package com.frank.threadpool.monitor.agent.beat.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author francis
 * @version 2021-07-20
 */
public class SimpleHttpResponseParser {
    private static final int MAX_BODY_SIZE = 1024 * 1024 * 4;
    private byte[] buf;

    public SimpleHttpResponseParser(int maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize must > 0");
        }
        this.buf = new byte[maxSize];
    }

    public SimpleHttpResponseParser() {
        this(1024 * 4);
    }

    /**
     * Parse bytes from an input stream to a {@link SimpleHttpResponse}.
     *
     * @param in input stream
     * @return parsed HTTP response entity
     * @throws IOException when an IO error occurs
     */
    public SimpleHttpResponse parse(InputStream in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        Charset charset = StandardCharsets.UTF_8;
        int contentLength = -1;
        SimpleHttpResponse response = null;
        byte[] bytes = new byte[1024];
        int len;
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        while ((len = in.read(bytes)) != -1){
            os.write(bytes,0,len);
            os.flush();
        }
        String s = new String(os.toByteArray());
        System.out.println(s);

        return response;
    }

    /**
     * Get the index of CRLF separator.
     *
     * @param bg begin offset
     * @param ed end offset
     * @return the index, or {@code -1} if no CRLF is found
     */
    private int indexOfCRLF(int bg, int ed) {
        if (ed - bg < 2) {
            return -1;
        }
        for (int i = bg; i < ed - 1; i++) {
            if (buf[i] == '\r' && buf[i + 1] == '\n') {
                return i;
            }
        }
        return -1;
    }

}
