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
import java.io.OutputStream;

/**
 * An output stream for an unix socket.
 *
 * @author Matthew Johnson - Initial contribution and API
 * @author Markus Rathgeb - Add JavaDoc and fix warnings
 */
public class USOutputStream extends OutputStream {
    private native int native_send(int sock, byte[] b, int off, int len) throws IOException;

    private native int native_send(int sock, byte[][] b) throws IOException;

    private final int sock;
    boolean closed = false;
    private final byte[] onebuf = new byte[1];
    private final UnixSocket us;

    /**
     * Create a new output stream for an unit socket.
     *
     * @param sock the socket number
     * @param us the unix socket to use
     */
    public USOutputStream(final int sock, final UnixSocket us) {
        this.sock = sock;
        this.us = us;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        us.close();
    }

    @Override
    public void flush() {
    } // no-op, we do not buffer

    /**
     * Write the given array of byte arrays.
     *
     * @param b the data
     * @throws IOException if an I/O error occurs
     * @throws NotConnectedException if the output stream has been closed
     */
    public void write(final byte[][] b) throws IOException {
        if (closed) {
            throw new NotConnectedException();
        }
        native_send(sock, b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (closed) {
            throw new NotConnectedException();
        }
        native_send(sock, b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        onebuf[0] = (byte) (b % 0x7F);
        if (1 == b % 0x80) {
            onebuf[0] = (byte) -onebuf[0];
        }
        write(onebuf);
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
}
