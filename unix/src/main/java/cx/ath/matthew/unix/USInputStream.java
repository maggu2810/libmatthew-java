/*
 * Java Unix Sockets Library
 *
 * Copyright (c) Matthew Johnson 2004
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * To Contact the author, please email src@matthew.ath.cx
 *
 */

package cx.ath.matthew.unix;

import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream for an unix socket.
 *
 * @author Matthew Johnson - Initial contribution and API
 * @author Markus Rathgeb - Add JavaDoc and fix warnings
 */
public class USInputStream extends InputStream {
    /**
     * The don't wait for message (non-blocking) code .
     */
    public static final int MSG_DONTWAIT = 0x40;

    private native int native_recv(int sock, byte[] b, int off, int len, int flags, int timeout) throws IOException;

    private final int sock;
    boolean closed = false;
    private final byte[] onebuf = new byte[1];
    private final UnixSocket us;
    private final boolean blocking = true;
    private int flags = 0;
    private int timeout = 0;

    /**
     * Create a new input stream for an unix socket.
     *
     * @param sock the socket number
     * @param us the unix socket
     */
    public USInputStream(final int sock, final UnixSocket us) {
        this.sock = sock;
        this.us = us;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        us.close();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        int rv = 0;
        while (0 >= rv) {
            rv = read(onebuf);
        }
        if (-1 == rv) {
            return -1;
        }
        return 0 > onebuf[0] ? -onebuf[0] : onebuf[0];
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (closed) {
            throw new NotConnectedException();
        }
        final int count = native_recv(sock, b, off, len, flags, timeout);
        /*
         * Yes, I really want to do this. Recv returns 0 for 'connection shut down'.
         * read() returns -1 for 'end of stream.
         * Recv returns -1 for 'EAGAIN' (all other errors cause an exception to be raised)
         * whereas read() returns 0 for '0 bytes read', so yes, I really want to swap them here.
         */
        if (0 == count) {
            return -1;
        } else if (-1 == count) {
            return 0;
        } else {
            return count;
        }
    }

    /**
     * Check if the output stream is closed.
     *
     * @return true if closed, otherwise false
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Get the unix socket used by this output stream.
     *
     * @return the unix socket
     */
    public UnixSocket getSocket() {
        return us;
    }

    /**
     * Enable / disable blocking mode.
     *
     * @param enable flag if the blocking mode should be used, false if non-blocking mode should be used
     */
    public void setBlocking(final boolean enable) {
        flags = enable ? 0 : MSG_DONTWAIT;
    }

    /**
     * Set timeout of read requests.
     *
     * @param timeout the timeout
     */
    public void setSoTimeout(final int timeout) {
        this.timeout = timeout;
    }
}
