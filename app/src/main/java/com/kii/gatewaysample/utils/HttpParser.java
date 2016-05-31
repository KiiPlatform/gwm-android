package com.kii.gatewaysample.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HttpParser {

    /**
     * Constructor for HttpParser.
     */
    private HttpParser() { }

    /**
     * Return byte array from an (unchunked) input stream.
     * Stop reading when <tt>"\n"</tt> terminator encountered
     * If the stream ends before the line terminator is found,
     * the last part of the string will still be returned.
     * If no input data available, <code>null</code> is returned.
     *
     * @param inputStream the stream to read from
     *
     * @throws IOException if an I/O problem occurs
     * @return a byte array from the stream
     */
    public static byte[] readRawLine(InputStream inputStream) throws IOException {

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int ch;
        while ((ch = inputStream.read()) >= 0) {
            buf.write(ch);
            if (ch == '\n') { // be tolerant (RFC-2616 Section 19.3)
                break;
            }
        }
        if (buf.size() == 0) {
            return null;
        }
        return buf.toByteArray();
    }

    /**
     * Read up to <tt>"\n"</tt> from an (unchunked) input stream.
     * If the stream ends before the line terminator is found,
     * the last part of the string will still be returned.
     * If no input data available, <code>null</code> is returned.
     *
     * @param inputStream the stream to read from
     * @param charset charset of HTTP protocol elements
     *
     * @throws IOException if an I/O problem occurs
     * @return a line from the stream
     *
     */
    public static String readLine(InputStream inputStream, String charset) throws IOException {
        byte[] rawdata = readRawLine(inputStream);
        if (rawdata == null) {
            return null;
        }
        // strip CR and LF from the end
        int len = rawdata.length;
        int offset = 0;
        if (len > 0) {
            if (rawdata[len - 1] == '\n') {
                offset++;
                if (len > 1) {
                    if (rawdata[len - 2] == '\r') {
                        offset++;
                    }
                }
            }
        }
        final String result =
                new String(rawdata, 0, len - offset, charset);
        return result;
    }

    /**
     * Parses headers from the given stream.  Headers with the same name are not
     * combined.
     *
     * @param is the stream to read headers from
     * @param charset the charset to use for reading the data
     *
     * @return an array of headers in the order in which they were parsed
     *
     * @throws IOException if an IO error occurs while reading from the stream
     * @throws IOException if there is an error parsing a header value
     *
     * @since 3.0
     */
    public static HashMap<String, String> parseHeaders(InputStream is, String charset) throws IOException {

        HashMap<String, String> headers = new HashMap<String, String>();
        String name = null;
        StringBuffer value = null;
        for (; ;) {
            String line = HttpParser.readLine(is, charset);
            if ((line == null) || (line.trim().length() < 1)) {
                break;
            }

            // Parse the header name and value
            // Check for folded headers first
            // Detect LWS-char see HTTP/1.0 or HTTP/1.1 Section 2.2
            // discussion on folded headers
            if ((line.charAt(0) == ' ') || (line.charAt(0) == '\t')) {
                // we have continuation folded header
                // so append value
                if (value != null) {
                    value.append(' ');
                    value.append(line.trim());
                }
            } else {
                // make sure we save the previous name,value pair if present
                if (name != null) {
                    headers.put(name, value.toString());
                }

                // Otherwise we should have normal HTTP header line
                // Parse the header name and value
                int colon = line.indexOf(":");
                if (colon < 0) {
                    throw new IOException("Unable to parse header: " + line);
                }
                name = line.substring(0, colon).trim();
                value = new StringBuffer(line.substring(colon + 1).trim());
            }

        }

        // make sure we save the last name,value pair if present
        if (name != null) {
            headers.put(name, value.toString());
        }
        return headers;
    }
}