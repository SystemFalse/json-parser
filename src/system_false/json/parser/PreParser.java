/*
 * Copyright (c) 2023, SystemFalse. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 */

package system_false.json.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Class for pre parser. Object of this class will analyze JSON text before parser
 * and do some changes. For example, this parser can ignore whitespaces between
 * tokens and comments (line and multiline). This object is automatically used in
 * {@link Json5Parser}.
 */
public class PreParser {
    /**
     * Source reader.
     */
    private final Reader parent;
    /**
     * Counter for read lines.
     */
    private final AtomicLong lineCount = new AtomicLong(1);
    /**
     * Counter for line character position.
     */
    private final AtomicLong linePos = new AtomicLong(1);
    /**
     * Flag showing that last read character should be returned next time.
     */
    private boolean reread;
    /**
     * Last read character.
     */
    private int lastChar = -1;

    /**
     * Buffer for text.
     */
    private final char[] buffer = new char[4096];
    /**
     * Current index in buffer.
     */
    private int index = 1;
    /**
     * Current length of read characters.
     */
    private int bufLen = 1;

    /**
     * Flag showing that all comments must be passed during reading.
     */
    private final boolean passComments;
    /**
     * Flag showing that next text is comment and should be ignored until linefeed.
     */
    private boolean isComment;
    /**
     * Flag showing that next text is comment and should be ignored until *&#47;.
     */
    private boolean isMultilineComment;

    /**
     * Constructor creates object using given reader as JSON source.
     * @param reader reader, containing JSON text
     */
    public PreParser(Reader reader) {
        this(reader, false);
    }

    /**
     * Constructor creates object using given reader as JSON source.
     * @param reader reader, containing JSON text
     * @param passComments should be comments pass into the parsing text ot not
     */
    public PreParser(Reader reader, boolean passComments) {
        if (reader == null)
            throw new NullPointerException("null reader");
        this.parent = reader;
        this.passComments = passComments;
    }

    /**
     * Method turns flag {@link #reread} up.
     */
    public void reread() {
        reread = true;
    }

    /**
     * Method loads characters from reader to buffer and sets accompanying parameters.
     * @throws IOException if any exception occurred during reading
     */
    private void load() throws IOException {
        bufLen = parent.read(buffer);
        index = 0;
    }

    /**
     * Method reads next character from source. If end of source was reached, an exception
     * will be thrown.
     *
     * @return next character from source
     * @throws IOException if any exception occurred during reading
     */
    public char next() throws IOException {
        char[] ch = new char[1];
        if (next(ch)) return ch[0];
        else throw new IOException("end of data");
    }

    /**
     * Method reads next character from source and puts it in given array. Array at least must
     * be length of 1. If any character was read, it will be put in the 0 index.
     * @param ch buffer for next character
     *
     * @return {@code true} if any character was read, {@code false} otherwise
     * @throws IOException if any exception occurred during reading
     */
    public boolean next(char[] ch) throws IOException {
        if (reread) {
            reread = false;
            ch[0] = (char) lastChar;
            return true;
        }
        if (bufLen > 0 && index == bufLen) load();
        if (index < bufLen) {
            ch[0] = buffer[index++];
            if (lastChar == '\n') {
                lineCount.incrementAndGet();
                linePos.set(1);
            } else linePos.incrementAndGet();
            lastChar = ch[0];
            return true;
        } else return false;
    }

    /**
     * Method reads characters until given condition accepting it. When last character of
     * sequence will be read, flag {@link #reread} will be turned up.
     * @param func condition for reading
     *
     * @return first string satisfy the condition
     * @throws IOException if any exception occurred during reading
     */
    public String next(Predicate<String> func) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] ch = new char[1];
        while (next(ch)) {
            sb.append(ch[0]);
            if (func.test(sb.toString())) continue;
            if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
            reread();
            break;
        }
        return sb.toString();
    }

    /**
     * Method reads characters until they're looking at pattern. When last character of
     * sequence will be read, flag {@link #reread} will be turned up.
     * @param pattern pattern that accepts string
     *
     * @return first string satisfy the pattern
     * @throws IOException if any exception occurred during reading
     */
    public String next(Pattern pattern) throws IOException {
        return next(str -> pattern.matcher(str).lookingAt());
    }

    public static final Pattern WORD_PATTERN = Pattern.compile("\\A[a-zA-Z_]++(?!.)");

    /**
     * Method reads next word from source. A word is a sequence of Latin letters without
     * spaces or other punctuation marks.
     *
     * @return first word in source
     * @throws IOException if any exception occurred during reading
     */
    public String nextWord() throws IOException {
        return next(WORD_PATTERN);
    }

    /**
     * Method reads next character from source skipping all whitespaces and comments if flag
     * {@link #passComments} if false. If end of source was reached, an exception will be thrown.
     *
     * @return first readable character
     * @throws IOException if any exception occurred during reading
     */
    public char nextReadable() throws IOException {
        char[] ch = new char[1];
        if (nextReadable(ch)) return ch[0];
        else throw new IOException("end of data");
    }

    /**
     * Method reads next character from source skipping all whitespaces and comments if flag
     * {@link #passComments} if false and puts it in given array. Array at least must
     * be length of 1. If any character was read, it will be put in the 0 index.
     * @param ch buffer for next readable character
     *
     * @return {@code true} if any character was read, {@code false} otherwise
     * @throws IOException if any exception occurred during reading
     */
    public boolean nextReadable(char[] ch) throws IOException {
        boolean result = false;
        while (next(ch)) {
            if (!passComments && ch[0] == '/' && !(isComment || isMultilineComment)) { //checking begin of comment
                if (next(ch)) {
                    if (ch[0] == '/') {
                        isComment = true;
                        continue;
                    }
                    if (ch[0] == '*') {
                        isMultilineComment = true;
                        continue;
                    }
                    reread(); //if next symbol is normal, it will be returned next time
                }
            }
            if (ch[0] == '\n' && isComment) { //checking end of line comment
                isComment = false;
                continue;
            }
            if (ch[0] == '*' && isMultilineComment) { //checking end of multiline comment
                if (next(ch)) {
                    if (ch[0] == '/') {
                        isMultilineComment = false;
                        continue;
                    }
                    reread(); //if next symbol is normal, it will be returned next time
                }
            }
            if (!passComments && (isComment || isMultilineComment)) continue;
            if (Character.isWhitespace(ch[0])) continue;
            result = true;
            lastChar = ch[0];
            break;
        }
        return result;
    }

    /**
     * Method converts functionality of this object to Reader. For methods {@link Reader#read()} and
     * {@link Reader#read(char[], int, int)} method {@link #next(char[])} wil be used. Closing returned
     * reader will not affect on this source reader.
     *
     * @return reader which is using methods of this object.
     */
    public Reader asReader() {
        return new Reader() {
            boolean closed;

            void ensureClose() throws IOException {
                if (closed) throw new IOException("reader is closed");
            }

            @Override
            public int read() throws IOException {
                ensureClose();
                char[] ch = new char[1];
                if (next(ch)) return ch[0];
                else return -1;
            }

            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                ensureClose();
                checkFromIndexSize(off, len, cbuf.length);
                char[] ch = new char[1];
                int i = 0;
                for (; i < len && next(ch); i++) cbuf[off + i] = ch[0];
                return i;
            }

            private void checkFromIndexSize(int off, int len, int length) {
                if ((off | len) < 0 || off + len > length)
                    throw new IndexOutOfBoundsException(
                            "index " + off + " of sub length " + len + " and length " + length);
            }

            @Override
            public boolean markSupported() {
                return true;
            }

            @Override
            public void mark(int readAheadLimit) throws IOException {
                if (readAheadLimit <= 0)
                    throw new IllegalArgumentException("illegal read limit");
                if (readAheadLimit > 1)
                    throw new IOException("too big mark buffer " + readAheadLimit);
                super.mark(readAheadLimit);
            }

            @Override
            public void reset() {
                reread = true;
            }

            @Override
            public void close() {
                closed = true;
            }
        };
    }

    /**
     * Methods returns current parser line.
     *
     * @return current line
     */
    public long line() {
        return lineCount.get();
    }

    /**
     * Methods returns current parser line position.
     *
     * @return current line position
     */
    public long index() {
        return linePos.get();
    }

    /**
     * Method returns string representation of parser position.
     * It looks like using {@code "line " + line() + ", index " + index()}.
     *
     * @return string representation of parser position
     */
    public String position() {
        return "line " + line() + ", index " + index();
    }
}
