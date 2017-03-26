/*
 * Java Hexdump Library
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

package cx.ath.matthew.utils;

import java.io.PrintStream;

/**
 * This class formats byte-arrays in hex and ascii for display.
 *
 * @author Matthew Johnson - Initial contribution and API
 * @author Markus Rathgeb - add JavaDoc, use capacity hint for string buffers, fix printable character begin
 */
public class Hexdump {
    private static final char[] HEXCHARS_LC = "0123456789abcdef".toCharArray();

    /**
     * Convert an array of bytes to a string representation.
     *
     * <p>
     * A byte is converted to two hex characters followed by a space (there is also a space at the end of the
     * string).
     * <p>
     * The returned length of the string is equal to 3*{@code buf.length}
     *
     * @param buf the buffer
     * @return the data in the format: ab cd ...
     */
    public static String toHex(final byte[] buf) {
        return toHex(buf, 0, buf.length);
    }

    /**
     * Convert an array of bytes to a string representation.
     *
     * <p>
     * A byte is converted to two hex characters followed by a space (there is also a whitespace at the end of the
     * string).
     * <p>
     * If the buffer does not contain enough data (with respect to {@code ofs} and {@code len}, spaces are used for
     * the two characters.
     * <p>
     * The returned length of the string is equal to 3*{@code len}.
     *
     * @param buf the buffer
     * @param ofs the offset to start in the buffer
     * @param len the number of bytes that should be used of the buffer starting from {@code ofs}
     * @return the data in the format: ab cd ...
     */
    public static String toHex(final byte[] buf, final int ofs, final int len) {
        final StringBuffer sb = new StringBuffer(3 * len);
        for (int i = ofs; i < ofs + len; ++i) {
            if (i < buf.length) {
                sb.append(HEXCHARS_LC[(buf[i] & 0xF0) >> 4]);
                sb.append(HEXCHARS_LC[buf[i] & 0x0F]);
                sb.append(' ');
            } else {
                sb.append("   ");
            }
        }
        return sb.toString();
    }

    /**
     * Convert an array of bytes to a string representation.
     *
     * <p>
     * Every byte is interpret as an ASCII character (so no Unicode support).
     * For all readable ASCII bytes the respective character is used.
     * For all non-readable ASCII bytes '.' character is used.
     *
     * @param buf the byte buffer
     * @return a string representation
     */
    public static String toAscii(final byte[] buf) {
        return toAscii(buf, 0, buf.length);
    }

    /**
     * Convert an array of bytes to a string representation.
     *
     * <p>
     * Every byte is interpret as an ASCII character (so no Unicode support).
     * For all readable ASCII bytes the respective character is used.
     * For all non-readable ASCII bytes '.' character is used.
     * <p>
     * If the buffer does not contain enough data (with respect to {@code ofs} and {@code len}, spaces are inserted for
     * the remaining bytes.
     * <p>
     * The returned length of the string is equal to {@code len}.
     *
     * @param buf the byte buffer
     * @param ofs the offset to start in the buffer
     * @param len the number of bytes that should be used of the buffer starting from {@code ofs}
     * @return a string representation
     */
    public static String toAscii(final byte[] buf, final int ofs, final int len) {
        final StringBuffer sb = new StringBuffer(len);
        final int j = ofs + len;
        for (int i = ofs; i < j; i++) {
            if (i < buf.length) {
                if (32 <= buf[i] && 126 >= buf[i]) {
                    sb.append((char) buf[i]);
                } else {
                    sb.append('.');
                }
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    /**
     * Format a multi-line string for a byte buffer.
     *
     * <p>
     * This method calls {@link #format(byte[], int)} using a width of 80.
     * For further description, see linked function.
     *
     * @param buf the buffer
     * @return the formatted representation
     */
    public static String format(final byte[] buf) {
        return format(buf, 80);
    }

    /**
     * Format a multi-line string for a byte buffer.
     *
     * <p>
     * The number of bytes that could be represent per line depends on the given {@code width}.
     * The lines start with the byte sequenze position followed by a tabulator.
     * After that the bytes represented in the line are shown as hex strings ({@link #toHex(byte[], int, int)}).
     * After that a space is added followed by the representation as ascii string ({@link #toAscii(byte[], int, int)}).
     *
     * @param buf the buffer
     * @param width the line width
     * @return the formatted representation
     */
    public static String format(final byte[] buf, final int width) {
        /*
         * Calculate the number of "bytes" that are inspected per line.
         * - 8:
         * * 6 characters at the beginning for the position
         * * 1x '\t' between position and hex representation
         * * 1x ' ' between hex and ascii representation
         * / 4: 4 characters per byte (hex: 2 for byte, 1 for space; ascii: 1 for byte)
         */
        //
        final int bs = (width - 8) / 4;
        if (bs < 1) {
            throw new IllegalArgumentException("The given width is too small.");
        }

        final int lines = (buf.length + bs - 1) / bs;
        // See above: (6 + 1 + 1) + (4x bs) + (1 [new line])
        final int maxCharsPerLine = 8 + 4 * bs + 1;

        final StringBuffer sb = new StringBuffer(lines * maxCharsPerLine);
        for (int i = 0; i < buf.length; i += bs) {
            // sb.append(String.format("%06x", i));
            for (int j = 0; j < 6; j++) {
                sb.append(HEXCHARS_LC[(i << j * 4 & 0xF00000) >> 20]);
            }
            sb.append('\t');
            sb.append(toHex(buf, i, bs));
            sb.append(' ');
            sb.append(toAscii(buf, i, bs));
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Write a formatted representation to the standard error stream.
     *
     * <p>
     * See {@link #format(byte[])}
     *
     * @param buf the buffer
     */
    public static void print(final byte[] buf) {
        print(buf, System.err);
    }

    /**
     * Write a formatted representation to the standard error stream.
     *
     * <p>
     * See {@link #format(byte[], int)}
     *
     * @param buf the buffer
     * @param width the line width
     */
    public static void print(final byte[] buf, final int width) {
        print(buf, width, System.err);
    }

    /**
     * Write a formatted representation to a print stream.
     *
     * <p>
     * See {@link #format(byte[])}
     *
     * @param buf the buffer
     * @param out the print stream
     */
    public static void print(final byte[] buf, final PrintStream out) {
        out.print(format(buf));
    }

    /**
     * Write a formatted representation to a print stream.
     *
     * <p>
     * See {@link #format(byte[], int)}
     *
     * @param buf the buffer
     * @param width the line width
     * @param out the print stream
     */
    public static void print(final byte[] buf, final int width, final PrintStream out) {
        out.print(format(buf, width));
    }

    /**
     * Convert a array of bytes to a string representation.
     *
     * @param buf the buffer
     * @return the data in the format 0xab, 0xcd, ...
     */
    public static String toByteArray(final byte[] buf) {
        return toByteArray(buf, 0, buf.length);
    }

    /**
     * Convert a array of bytes to a string representation.
     *
     * @param buf the buffer
     * @param ofs the offset to start using the buffer
     * @param endPositionExcl the end position (exclusive) in the buffer
     * @return the data in the format 0xab,0xcd,...
     */
    public static String toByteArray(final byte[] buf, final int ofs, final int endPositionExcl) {
        final StringBuffer sb = new StringBuffer(5 * (endPositionExcl - ofs));
        for (int i = ofs; i < endPositionExcl && i < buf.length; i++) {
            sb.append('0');
            sb.append('x');
            sb.append(HEXCHARS_LC[(buf[i] & 0xF0) >> 4]);
            sb.append(HEXCHARS_LC[buf[i] & 0x0F]);
            if (i + 1 < endPositionExcl && i + 1 < buf.length) {
                sb.append(',');
            }
        }
        return sb.toString();
    }
}
