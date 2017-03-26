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

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a listening UNIX Socket.
 *
 * @author Matthew Johnson - Initial contribution and API
 * @author Markus Rathgeb - Add / fix JavaDoc and fix warnings
 * @author Markus Rathgeb - Use Closeable interface
 */
public class UnixServerSocket implements Closeable {
    static {
        System.loadLibrary("unix-java");
    }

    private native int native_bind(String address, boolean abs) throws IOException;

    private native void native_close(int sock) throws IOException;

    private native int native_accept(int sock) throws IOException;

    private UnixSocketAddress address = null;
    private boolean bound = false;
    private boolean closed = false;
    private int sock;

    /**
     * Create an un-bound server socket.
     */
    public UnixServerSocket() {
    }

    /**
     * Create a server socket bound to the given address.
     *
     * @param address Path to the socket.
     * @throws IOException on bind error
     */
    public UnixServerSocket(final UnixSocketAddress address) throws IOException {
        bind(address);
    }

    /**
     * Create a server socket bound to the given address.
     *
     * @param address Path to the socket.
     * @throws IOException on bind error
     */
    public UnixServerSocket(final String address) throws IOException {
        this(new UnixSocketAddress(address));
    }

    /**
     * Accepts a connection on the ServerSocket.
     *
     * @return A UnixSocket connected to the accepted connection.
     * @throws IOException on error
     */
    public UnixSocket accept() throws IOException {
        final int client_sock = native_accept(sock);
        return new UnixSocket(client_sock, address);
    }

    @Override
    public synchronized void close() throws IOException {
        native_close(sock);
        sock = 0;
        closed = true;
        bound = false;
    }

    /**
     * Binds a server socket to the given address.
     *
     * @param address Path to the socket.
     * @throws IOException on error
     */
    public void bind(final UnixSocketAddress address) throws IOException {
        if (bound) {
            close();
        }
        sock = native_bind(address.path, address.abs);
        bound = true;
        closed = false;
        this.address = address;
    }

    /**
     * Binds a server socket to the given address.
     *
     * @param address Path to the socket.
     * @throws IOException on error
     */
    public void bind(final String address) throws IOException {
        bind(new UnixSocketAddress(address));
    }

    /**
     * Return the address this socket is bound to.
     *
     * @return The UnixSocketAddress if bound or null if unbound.
     */
    public UnixSocketAddress getAddress() {
        return address;
    }

    /**
     * Check the status of the socket.
     *
     * @return True if closed.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Check the status of the socket.
     *
     * @return True if bound.
     */
    public boolean isBound() {
        return bound;
    }
}
