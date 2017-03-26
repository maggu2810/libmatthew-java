/*
 * Java Exec Pipe Library
 *
 * Copyright (c) Matthew Johnson 2005
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

package cx.ath.matthew.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class to pipe an InputStream through a command using stdin/stdout.
 * E.g.
 *
 * <pre>
 * Reader r = new InputStreamReader(new ExecInputStream(new FileInputStream("file"), "command"));
 * </pre>
 */
public class ExecInputStream extends FilterInputStream {
    private final Process proc;
    private final InputStream stdout;
    private final OutputStream stdin;
    private final InOutCopier copy;

    /**
     * Create a new ExecInputStream on the given InputStream
     * using the process to filter the stream.
     *
     * @param is Reads from this InputStream
     * @param p Filters data through stdin/out on this Process
     */
    public ExecInputStream(final InputStream is, final Process p) throws IOException {
        super(is);
        proc = p;
        stdin = p.getOutputStream();
        stdout = p.getInputStream();
        copy = new InOutCopier(in, stdin);
        copy.start();
    }

    /**
     * Create a new ExecInputStream on the given InputStream
     * using the process to filter the stream.
     *
     * @param is Reads from this InputStream
     * @param cmd Creates a Process from this string to filter data through stdin/out
     */
    public ExecInputStream(final InputStream is, final String cmd) throws IOException {
        this(is, Runtime.getRuntime().exec(cmd));
    }

    /**
     * Create a new ExecInputStream on the given InputStream
     * using the process to filter the stream.
     *
     * @param is Reads from this InputStream
     * @param cmd Creates a Process from this string array (command, arg, ...) to filter data through stdin/out
     */
    public ExecInputStream(final InputStream is, final String[] cmd) throws IOException {
        this(is, Runtime.getRuntime().exec(cmd));
    }

    /**
     * Create a new ExecInputStream on the given InputStream
     * using the process to filter the stream.
     *
     * @param is Reads from this InputStream
     * @param cmd Creates a Process from this string to filter data through stdin/out
     * @param env Setup the environment for the command
     */
    public ExecInputStream(final InputStream is, final String cmd, final String[] env) throws IOException {
        this(is, Runtime.getRuntime().exec(cmd, env));
    }

    /**
     * Create a new ExecInputStream on the given InputStream
     * using the process to filter the stream.
     *
     * @param is Reads from this InputStream
     * @param cmd Creates a Process from this string array (command, arg, ...) to filter data through stdin/out
     * @param env Setup the environment for the command
     */
    public ExecInputStream(final InputStream is, final String[] cmd, final String[] env) throws IOException {
        this(is, Runtime.getRuntime().exec(cmd, env));
    }

    @Override
    public void close() throws IOException {
        try {
            proc.waitFor();
        } catch (final InterruptedException Ie) {
        }
        // copy.close();
        try {
            copy.join();
        } catch (final InterruptedException Ie) {
        }
        stdin.close();
        in.close();
        stdout.close();
    }

    public void flush() throws IOException {
        copy.flush();
    }

    @Override
    public int available() throws IOException {
        return stdout.available();
    }

    @Override
    public int read() throws IOException {
        return stdout.read();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return stdout.read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return stdout.read(b, off, len);
    }

    @Override
    public long skip(final long n) throws IOException {
        return stdout.skip(n);
    }

    @Override
    public void mark(final int readlimit) {
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void reset() {
    }

    @Override
    public void finalize() {
        try {
            close();
        } catch (final Exception e) {
        }
    }
}
