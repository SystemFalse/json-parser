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

import system_false.json.content.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class of JSON5 parser. It can parse JSON5 text and converts it to
 * {@link JsonElement} values. It provides methods that can be used for
 * parsing JSON values and structures. This parser does not require
 * text to begin with JSON elements, it will skip all whitespaces to
 * find first readable symbol.
 * <pre>
 * Methods parse..() can parse JSON text and validate it.
 * </pre>
 */
public class Json5Parser {
    /**
     * Flag that means source text has no more JSON elements.
     */
    public static final int NO_ELEMENT = 0;
    /**
     * Flag that means next JSON element may be null.
     */
    public static final int ELEMENT_NULL = 0x1;
    /**
     * Flag that means next JSON element may be boolean.
     */
    public static final int ELEMENT_BOOLEAN = 0x2;
    /**
     * Flag that means next JSON element may be number.
     */
    public static final int ELEMENT_NUMBER = 0x4;
    /**
     * Flag that means next JSON element may be string.
     */
    public static final int ELEMENT_STRING = 0x8;
    /**
     * Flag that means next JSON element may be any JSON value.
     */
    public static final int ELEMENT_VALUE = 0xf;
    /**
     * Flag that means next JSON element may be JSON array.
     */
    public static final int ELEMENT_ARRAY = 0x10;
    /**
     * Flag that means next JSON element may be JSON object.
     */
    public static final int ELEMENT_OBJECT = 0x20;
    /**
     * Flag that means next JSON element may be any JSON structure.
     */
    public static final int ELEMENT_STRUCTURE = 0x30;

    /**
     * PreParser will read symbols ignoring whitespaces and comments.
     */
    private final PreParser preParser;
    /**
     * This flag shows whether JSON element must be indexed or not;
     */
    private final boolean index;

    /**
     * Private constructor that creates parser with given Reader as text source.
     *
     * @param pp JSON text pre parser source
     * @param index indexation flag
     */
    private Json5Parser(PreParser pp, boolean index) {
        this.preParser = pp;
        this.index = index;
    }

    /**
     * Method creates new exception object with given cause.
     * @param cause exception that occurred
     *
     * @return new ParserException to throw
     */
    private ParserException parserWithCause(Exception cause) {
        return new ParserException("Exception at " + preParser.position() + ": " + cause.getMessage(), cause);
    }

    /**
     * Method creates new exception with this text: "Exception at " + preParser.position()
     * + ": illegal begin of " + type.
     * @param type type of JSON element which has incorrect begin
     *
     * @return new ParserException to throw
     */
    private ParserException parserWithBegin(String type) {
        return new ParserException("Exception at " + preParser.position() + ": illegal begin of " + type);
    }

    /**
     * Method parses JSON and returns {@code NullValue} if first JSON element is {@code null} or
     * throws an exception otherwise. All whitespaces before value will be skipped.
     *
     * @return parsed {@code null} value if it is next element
     * @throws ParserException if next element is not {@code null} or if end of source reached
     */
    public synchronized NullValue parseNull() throws ParserException {
        try {
            char b = preParser.nextReadable();
            if (b != 'n') {
                preParser.reread();
                throw parserWithBegin("null");
            }
            return parseNull0();
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    /**
     * Method parses JSON and returns {@code NullValue} if next symbols in string form a word null,
     * and throws an exception in otherwise. But unlike {@link #parseNull()} this method must be invoked
     * after reading the letter 'n'.
     *
     * @return parsed {@code null} value
     * @throws IOException if any exception occurred during parsing
     */
    private synchronized NullValue parseNull0() throws IOException {
        char[] ch = new char[1];
        if (!preParser.next(ch) || ch[0] != 'u') throw new IOException("unknown value");
        if (!preParser.next(ch) || ch[0] != 'l') throw new IOException("unknown value");
        if (!preParser.next(ch) || ch[0] != 'l') throw new IOException("unknown value");
        return new NullValue();
    }

    /**
     * Method parses JSON and returns {@code BooleanValue} if first JSON element is {@code boolean} or
     * throws an exception otherwise. All whitespaces before value will be skipped.
     *
     * @return parsed {@code boolean} value if it is next element
     * @throws ParserException if next element is not {@code boolean} or if end of source reached
     */
    private synchronized BooleanValue parseBoolean() throws ParserException {
        try {
            char b = preParser.nextReadable();
            if (b != 't' && b != 'f') {
                preParser.reread();
                throw parserWithBegin("boolean");
            }
            return parseBoolean0(b == 't');
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    /**
     * Method parses JSON and returns {@code BooleanValue} if next symbols in string form a word true
     * or false, and throws an exception in otherwise. But unlike {@link #parseBoolean()} this method
     * must be invoked after reading the letter 't' or 'f'.
     * @param value supposed value (if first letter is "t" it must be true and false otherwise)
     *
     * @return parsed {@code boolean} value
     * @throws IOException if any exception occurred during parsing
     */
    private synchronized BooleanValue parseBoolean0(boolean value) throws IOException {
        char[] ch = new char[1];
        if (value) {
            if (!preParser.next(ch) || ch[0] != 'r') throw new IOException("unknown value");
            if (!preParser.next(ch) || ch[0] != 'u') throw new IOException("unknown value");
            if (!preParser.next(ch) || ch[0] != 'e') throw new IOException("unknown value");
            return new BooleanValue(true);
        } else {
            if (!preParser.next(ch) || ch[0] != 'a') throw new IOException("unknown value");
            if (!preParser.next(ch) || ch[0] != 'l') throw new IOException("unknown value");
            if (!preParser.next(ch) || ch[0] != 's') throw new IOException("unknown value");
            if (!preParser.next(ch) || ch[0] != 'e') throw new IOException("unknown value");
            return new BooleanValue(false);
        }
    }

    /**
     * Method parses JSON and returns {@code NumberValue} if first JSON element is number or
     * throws an exception otherwise. All whitespaces before value will be skipped.
     *
     * @return parsed number value if it is next element
     * @throws ParserException if next element is not number or if end of source reached
     */
    @SuppressWarnings("ConstantValue")
    public synchronized NumberValue parseNumber() throws ParserException {
        try {
            char b = preParser.nextReadable();
            if (b != '+' && b != '-' && b < '0' && b > '9') {
                preParser.reread();
                throw parserWithBegin("number");
            }
            return parseNumber0(b);
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    private static final Pattern NUMBER = Pattern.compile("^(?<sign>[+\\-])?" +
            "(?<number>(?<special>Infinity|NaN)|(?:(?:0|[1-9]\\d*)(?<decimal>\\.\\d*)?(?:[eE]([+\\-]?\\d+))?|" +
            "(?<radix>(?<binary>0b[01]+)|(?<octal>0o[0-7]+)|(?<hex>0x\\p{XDigit}+))))$");

    private boolean isNumberChar(char ch) {
        return Character.isDigit(ch) || ch == 'a' || ch == 'b' || ch == 'c' || ch == 'd' || ch == 'e' || ch == 'E'
               || ch == 'f' ||ch == 'i' || ch == 'I' || ch == 'n' || ch == 'N' || ch == 'o' || ch == 't' || ch == 'x'
               || ch == 'y' || ch == '.' || ch == '+' || ch == '-';
    }

    /**
     * Method parses JSON and returns {@code NumberValue} if next symbols in string form a number,
     * and throws an exception in otherwise. But unlike {@link #parseNumber()} this method
     * must be invoked after reading the first letter.
     * @param first first letter of number
     *
     * @return parsed number value
     * @throws IOException if any exception occurred during parsing
     */
    private synchronized NumberValue parseNumber0(char first) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        char[] ch = new char[1];
        while (preParser.next(ch)) {
            if (isNumberChar(ch[0])) sb.append(ch[0]);
            else {
                if (!Character.isWhitespace(ch[0])) preParser.reread();
                break;
            }
        }
        Matcher m = NUMBER.matcher(sb.toString());
        if (!m.matches())
            throw new IOException("incorrect number value");
        if (m.group("number") == null)
            throw new IllegalArgumentException("empty number is not allowed");
        if (m.group("special") != null) {
            boolean sign = (m.group("sign") == null || m.group("sign").equals("+"));
            if (m.group("special").equals("Infinity")) {
                return new InfinityValue(!sign);
            } else {
                return new NaNValue();
            }
        }
        if (m.group("radix") != null && m.group("decimal") == null) {
            boolean sign = (m.group("sign") == null || m.group("sign").equals("+"));
            if (m.group("binary") != null) return new IntegerValue(sign
                    ? new BigInteger(sb.substring(2), 2)
                    : new BigInteger(sb.substring(2), 2).negate(), 2);
            else if (m.group("octal") != null) return new IntegerValue(sign
                    ? new BigInteger(sb.substring(2), 8)
                    : new BigInteger(sb.substring(2), 8).negate(), 8);
            else return new IntegerValue(sign
                    ? new BigInteger(sb.substring(2), 16)
                    : new BigInteger(sb.substring(2), 16).negate(), 16);
        } else if (m.group("decimal") == null)
            return new IntegerValue(new BigInteger(sb.toString()));
        else return new DecimalValue(new BigDecimal(sb.toString()));
    }

    /**
     * Method parses JSON and returns {@code StringValue} if first JSON element is string or
     * throws an exception otherwise. All whitespaces before value will be skipped.
     *
     * @return parsed string value if it is next element
     * @throws ParserException if next element is not string or if end of source reached
     */
    public synchronized StringValue parseString() throws ParserException {
        try {
            char b = preParser.nextReadable();
            if (b != '"' && b != '\'') {
                preParser.reread();
                throw parserWithBegin("string");
            }
            return parseString0(b);
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    /**
     * Method parses JSON and returns {@code StringValue} if next symbols in string form a JSON string,
     * and throws an exception in otherwise. But unlike {@link #parseString()} this method
     * must be invoked after reading the first letter.
     * @param first first letter of number
     *
     * @return parsed number value
     * @throws IOException if any exception occurred during parsing
     */
    private synchronized StringValue parseString0(char first) throws IOException {
        StringBuilder sb = new StringBuilder();
        StringValue.fromJSONString(preParser.asReader(), first, sb);
        return new StringValue(sb.toString());
    }

    private static final Pattern ECMA_NAME_PATTERN = Pattern.compile(
            "^\\p{javaJavaIdentifierStart}\\p{javaUnicodeIdentifierPart}*$");

    private String parseECMAKey(char first) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        if (!ECMA_NAME_PATTERN.matcher(sb).matches()) {
            throw new IOException("illegal ECMAScript identifier begin");
        }
        char[] ch = new char[1];
        while (preParser.next(ch)) {
            sb.append(ch[0]);
            if (ECMA_NAME_PATTERN.matcher(sb).matches()) continue;
            sb.deleteCharAt(sb.length() - 1);
            preParser.reread();
            break;
        }
        return sb.toString();
    }

    /**
     * Method parses JSON and returns {@code JsonArray} if first JSON element is array or
     * throws an exception otherwise. All whitespaces before value will be skipped.
     *
     * @return parsed JSON array if it is next element
     * @throws ParserException if next element is not array or if end of source reached
     */
    public synchronized JsonArray parseArray() throws ParserException {
        try {
            char b = preParser.nextReadable();
            if (b != '[') {
                preParser.reread();
                throw parserWithBegin("array");
            }
            return parseArray0();
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    /**
     * Method parses JSON and returns {@code JsonArray} if next symbols in string form a JSON array,
     * and throws an exception in otherwise. But unlike {@link #parseArray()} this method
     * must be invoked after reading the opening bracket.
     *
     * @return parsed JSON array value
     * @throws IOException if any exception occurred during parsing
     */
    private synchronized JsonArray parseArray0() throws IOException {
        JsonArrayBuilder builder = JsonArrayBuilder.create();
        char[] ch = new char[1];
        boolean requireDelimiter = false;
        char next;
        while (preParser.nextReadable(ch)) {
            next = ch[0];

            if (next == ']') return builder.build(index);

            if (requireDelimiter && next == ',') {
                requireDelimiter = false;
                continue;
            } else if (!requireDelimiter && next == ',') throw new IOException("extra delimiter between values");
            else if (requireDelimiter) throw new IOException("missing comma between values");

            if (next == '}') throw new IOException("illegal closing bracket in array");
            JsonElement value = parseAny0(next);
            builder.add(value);
            requireDelimiter = true;
        }
        throw new IOException("missing end bracket for array");
    }

    /**
     * Method parses JSON and returns {@code JsonObject} if first JSON element is object or
     * throws an exception otherwise. All whitespaces before value will be skipped.
     *
     * @return parsed JSON object if it is next element
     * @throws ParserException if next element is not object or if end of source reached
     */
    public synchronized JsonObject parseObject() throws ParserException {
        try {
            char b = preParser.nextReadable();
            if (b != '{') {
                preParser.reread();
                throw parserWithBegin("object");
            }
            return parseObject0();
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    /**
     * Method parses JSON and returns {@code JsonObject} if next symbols in string form a JSON object,
     * and throws an exception in otherwise. But unlike {@link #parseObject()} this method
     * must be invoked after reading the opening bracket.
     *
     * @return parsed JSON object value
     * @throws IOException if any exception occurred during parsing
     */
    private synchronized JsonObject parseObject0() throws IOException {
        JsonObjectBuilder builder = JsonObjectBuilder.create();
        char[] ch = new char[1];
        boolean requireDelimiter = false, isKey = true;
        String key = null;
        char next;
        while (preParser.nextReadable(ch)) {
            next = ch[0];

            if (isKey && next == '}') return builder.build(index);
            else if (!isKey && next == '}') throw new IOException("missing value in the end of object");

            if (isKey && requireDelimiter && next == ',') {
                requireDelimiter = false;
                continue;
            }
            else if (isKey && !requireDelimiter && next == ',') throw new IOException("extra comma between key-value pairs");
            else if (!isKey && next == ',') throw new IOException("unexpected comma between key and value");
            else if (isKey && requireDelimiter && next != ':') throw new IOException("missing comma between key-value pairs");

            else if (!isKey && requireDelimiter && next == ':') {
                requireDelimiter = false;
                continue;
            }
            else if (!isKey && !requireDelimiter && next == ':') throw new IOException("extra colon between key and value");
            else if (isKey && next == ':') throw new IOException("unexpected colon between key-value pairs");
            else if (!isKey && requireDelimiter) throw new IOException("missing colon between key and value");

            if (isKey) {
                if (next == '"' || next == '\'') key = parseString0(next).asString();
                else key = parseECMAKey(next);
                if (index) {
                    try {
                        JsonPath.checkName(key);
                    } catch (IllegalArgumentException e) {
                        throw new IOException("object key \"" + key + "\" can not be used in indexing");
                    }
                }
                requireDelimiter = true;
                isKey = false;
                continue;
            }

            if (next == ']') throw new IOException("illegal closing bracket in object");
            JsonElement value = parseAny0(next);
            builder.put(key, value);
            requireDelimiter = true;
            isKey = true;
        }
        throw new IOException("missing end bracket for object");
    }

    /**
     * Method parses JSON and returns first JSON element.
     *
     * @return first JSON element
     * @throws ParserException if next element is incorrect or if end of source reached
     */
    public synchronized JsonElement parseAny() throws ParserException {
        try {
            char next = preParser.nextReadable();
            return parseAny0(next);
        } catch (IOException e) {
            throw parserWithCause(e);
        }
    }

    /**
     * Method parses JSON and returns first JSON element. But unlike {@link #parseAny()} this method
     * must be invoked after reading first letter.
     *
     * @return first JSON element
     * @throws ParserException if next element is incorrect or if end of source reached
     */
    private synchronized JsonElement parseAny0(char begin) throws IOException {
        if (begin == 'n') return parseNull0();
        if (begin == 't' || begin == 'f') return parseBoolean0(begin == 't');
        if (begin == '+' || begin == '-' || begin == 'I' || begin == 'N' || (begin >= '0' && begin <= '9')) return parseNumber0(begin);
        if (begin == '"' || begin == '\'') return parseString0(begin);
        if (begin == '[') return parseArray0();
        if (begin == '{') return parseObject0();
        throw new IOException("unknown element begin: '" + begin + '\'');
    }

    /**
     * Method returns supposed type of next JSON element. It may be one of these:
     * <el>
     *     <li>{@link #NO_ELEMENT}</li>
     *     <li>{@link #ELEMENT_NULL}</li>
     *     <li>{@link #ELEMENT_BOOLEAN}</li>
     *     <li>{@link #ELEMENT_NUMBER}</li>
     *     <li>{@link #ELEMENT_STRING}</li>
     *     <li>{@link #ELEMENT_ARRAY}</li>
     *     <li>{@link #ELEMENT_OBJECT}</li>
     * </el>
     *
     * @return supposed type of next JSON element
     */
    public synchronized int nextElement() {
        char[] ch = new char[1];
        try {
            if (preParser.nextReadable(ch)) {
                preParser.reread();
            } else return NO_ELEMENT;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        char b = ch[0];
        if (b == 'n') return ELEMENT_NULL;
        if (b == 't' || b == 'f') return ELEMENT_BOOLEAN;
        if (b == '+' || b == '-' || b == 'I' || b == 'N' || (b >= '0' && b <= '9')) return ELEMENT_NUMBER;
        if (b == '"' || b == '\'') return ELEMENT_STRING;
        if (b == '[') return ELEMENT_ARRAY;
        if (b == '{') return ELEMENT_OBJECT;
        return NO_ELEMENT;
    }

    private static final Pattern BLACK_PATTERN = Pattern.compile("\\s*");

    /**
     * Method is static constructor for this class. It creates new parser with given string as JSON
     * source.
     * @param json JSON text
     *
     * @return new instance of parser
     * @throws NullPointerException if string is null
     */
    public static Json5Parser create(String json) {
        return create(json, false);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given string as JSON
     * source. If the index flag is specified, then all parsed objects will be indexed immediately.
     * @param json JSON text
     * @param index indexation flag
     *
     * @return new instance of parser
     * @see JsonPath
     */
    public static Json5Parser create(String json, boolean index) {
        if (json == null)
            throw new NullPointerException("null string");
        if (BLACK_PATTERN.matcher(json).matches())
            throw new IllegalArgumentException("blank string");
        return new Json5Parser(new PreParser(new StringReader(json)), index);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given InputStream as JSON
     * source. Default charset is UTF-8.
     * @param json stream, containing JSON text
     *
     * @return new instance of parser
     * @throws NullPointerException if stream is null
     */
    public static Json5Parser create(InputStream json) {
        return create(json, StandardCharsets.UTF_8, false);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given InputStream as JSON
     * source. Default charset is UTF-8. If the index flag is specified, then all parsed objects will be indexed
     * immediately.
     * @param json stream, containing JSON text
     * @param index indexation flag
     *
     * @return new instance of parser
     * @throws NullPointerException if stream is null
     * @see JsonPath
     */
    public static Json5Parser create(InputStream json, boolean index) {
        return create(json, StandardCharsets.UTF_8, index);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given InputStream as JSON
     * source and charset.
     * @param json stream, containing JSON text
     * @param charset charset to use
     *
     * @return new instance of parser
     * @throws NullPointerException if any argument is null
     */
    public static Json5Parser create(InputStream json, Charset charset) {
        return create(json, charset, false);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given InputStream as JSON
     * source and charset. If the index flag is specified, then all parsed objects will be indexed immediately.
     * @param json stream, containing JSON text
     * @param charset charset to use
     * @param index indexation flag
     *
     * @return new instance of parser
     * @throws NullPointerException if any argument is null
     * @see JsonPath
     */
    public static Json5Parser create(InputStream json, Charset charset, boolean index) {
        if (json == null)
            throw new NullPointerException("null stream");
        if (charset == null)
            throw new NullPointerException("null charset");
        return new Json5Parser(new PreParser(new InputStreamReader(json, charset)), index);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given Reader as JSON
     * source.
     * @param json reader, containing JSON text
     *
     * @return new instance of parser
     * @throws NullPointerException if reader is null
     */
    public static Json5Parser create(Reader json) {
        return create(json, false);
    }

    /**
     * Method is static constructor for this class. It creates new parser with given Reader as JSON
     * source. If the index flag is specified, then all parsed objects will be indexed immediately.
     * @param json reader, containing JSON text
     * @param index indexation flag
     *
     * @return new instance of parser
     * @throws NullPointerException if reader is null
     * @see JsonPath
     */
    public static Json5Parser create(Reader json, boolean index) {
        if (json == null)
            throw new NullPointerException("null string");
        return new Json5Parser(new PreParser(json), index);
    }

    /**
     * Method is static constructor for this class. It creates new parser with specified PreParser.
     * @param parser pre parser, with JSON source.
     *
     * @return new instance of parser
     * @throws NullPointerException if parser is null
     */
    public static Json5Parser create(PreParser parser) {
        return create(parser, false);
    }

    /**
     * Method is static constructor for this class. It creates new parser with specified PreParser.
     * If the index flag is specified, then all parsed objects will be indexed immediately.
     * @param parser pre parser, with JSON source.
     * @param index indexation flag
     *
     * @return new instance of parser
     * @throws NullPointerException if parser is null
     * @see JsonPath
     */
    public static Json5Parser create(PreParser parser, boolean index) {
        if (parser == null)
            throw new NullPointerException("null parser");
        return new Json5Parser(parser, index);
    }
}
