/*
 * Java Unix Sockets Library
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

package cx.ath.matthew.unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Test client for {@link UnixSocket}.
 *
 * @author Matthew Johnson - Initial contribution and API
 * @author Markus Rathgeb - Add JavaDoc and fix warnings
 * @author Markus Rathgeb - Use try-with-resources
 */
public class testclient {

    /**
     * Main function.
     *
     * @param args arguments
     * @throws IOException on error
     */
    public static void main(final String args[]) throws IOException {
        try (final UnixSocket s = new UnixSocket(new UnixSocketAddress("testsock", true))) {
            final OutputStream os = s.getOutputStream();
            // final PrintWriter o = new PrintWriter(os);
            final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            String l;
            while (null != (l = r.readLine())) {
                final byte[] buf = (l + "\n").getBytes();
                os.write(buf, 0, buf.length);
            }
        }
    }
}
