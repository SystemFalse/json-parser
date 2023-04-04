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

package system_false.json;

import system_false.json.content.JsonArray;
import system_false.json.content.JsonObject;
import system_false.json.content.JsonValue;
import system_false.json.parser.Json5Parser;
import system_false.json.parser.ParserException;
import system_false.json.parser.PreParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * Utility class providing useful methods for working with JSON text.
 */
public class Json {
    /**
     * Private constructor, not used.
     */
    private Json() {}

    /**
     * Method parses {@code String} and create returns {@code JsonValue} container for
     * parsed text.
     * @param json JSON text
     *
     * @return parsed {@code JsonValue}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonValue parseJsonValue(String json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_VALUE) != 0)
            return (JsonValue) parser.parseAny();
        else throw new IllegalArgumentException("first element is string is not JsonValue");
    }

    /**
     * Method parses {@code Reader} text and create returns {@code JsonValue} container for
     * parsed text.
     * @param json JSON text reader
     *
     * @return parsed {@code JsonValue}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonValue parseJsonValue(Reader json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_VALUE) != 0)
            return (JsonValue) parser.parseAny();
        else throw new IllegalArgumentException("first element is string is not JsonValue");
    }

    /**
     * Method parses {@code InputStream} containing JSON text and create returns {@code JsonValue}
     * container for parsed text.
     * @param json {@code InputStream} for JSON text
     *
     * @return parsed {@code JsonValue}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonValue parseJsonValue(InputStream json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_VALUE) != 0)
            return (JsonValue) parser.parseAny();
        else throw new IllegalArgumentException("first element is string is not JsonValue");
    }

    /**
     * Method parses {@code InputStream} containing JSON text and create returns {@code JsonValue}
     * container for parsed text.
     * @param json {@code InputStream} for JSON text
     * @param charset charset to use in parsing
     *
     * @return parsed {@code JsonValue}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonValue parseJsonValue(InputStream json, Charset charset) throws ParserException {
        Json5Parser parser = Json5Parser.create(json, charset);
        if ((parser.nextElement() & Json5Parser.ELEMENT_VALUE) != 0)
            return (JsonValue) parser.parseAny();
        else throw new IllegalArgumentException("first element is string is not JsonValue");
    }

    /**
     * Method parses {@code String} and create returns {@code JsonObject} for parsed text.
     * @param json JSON text
     *
     * @return parsed {@code JsonObject}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonObject parseJsonObject(String json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_OBJECT) != 0)
            return parser.parseObject();
        else throw new IllegalArgumentException("first element is string is not JsonObject");
    }

    /**
     * Method parses {@code Reader} text and create returns {@code JsonObject} for parsed text.
     * @param json JSON text reader
     *
     * @return parsed {@code JsonObject}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonObject parseJsonObject(Reader json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_OBJECT) != 0)
            return parser.parseObject();
        else throw new IllegalArgumentException("first element is string is not JsonObject");
    }

    /**
     * Method parses {@code InputStream} containing JSON text and create returns {@code JsonObject}
     * for parsed text.
     * @param json {@code InputStream} for JSON text
     *
     * @return parsed {@code JsonObject}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonObject parseJsonObject(InputStream json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_OBJECT) != 0)
            return parser.parseObject();
        else throw new IllegalArgumentException("first element is string is not JsonObject");
    }

    /**
     * Method parses {@code InputStream} containing JSON text and create returns {@code JsonObject}
     * for parsed text.
     * @param json {@code InputStream} for JSON text
     * @param charset charset to use in parsing
     *
     * @return parsed {@code JsonObject}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonObject parseJsonObject(InputStream json, Charset charset) throws ParserException {
        Json5Parser parser = Json5Parser.create(json, charset);
        if ((parser.nextElement() & Json5Parser.ELEMENT_OBJECT) != 0)
            return parser.parseObject();
        else throw new IllegalArgumentException("first element is string is not JsonObject");
    }

    /**
     * Method parses {@code String} and create returns {@code JsonArray} for parsed text.
     * @param json JSON text
     *
     * @return parsed {@code JsonArray}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonArray parseJsonArray(String json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_ARRAY) != 0)
            return parser.parseArray();
        else throw new IllegalArgumentException("first element is string is not JsonArray");
    }

    /**
     * Method parses {@code Reader} text and create returns {@code JsonArray} for parsed text.
     * @param json JSON text reader
     *
     * @return parsed {@code JsonArray}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonArray parseJsonArray(Reader json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_ARRAY) != 0)
            return parser.parseArray();
        else throw new IllegalArgumentException("first element is string is not JsonArray");
    }

    /**
     * Method parses {@code InputStream} containing JSON text and create returns {@code JsonArray}
     * for parsed text.
     * @param json {@code InputStream} for JSON text
     *
     * @return parsed {@code JsonArray}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonArray parseJsonArray(InputStream json) throws ParserException {
        Json5Parser parser = Json5Parser.create(json);
        if ((parser.nextElement() & Json5Parser.ELEMENT_ARRAY) != 0)
            return parser.parseArray();
        else throw new IllegalArgumentException("first element is string is not JsonArray");
    }

    /**
     * Method parses {@code InputStream} containing JSON text and create returns {@code JsonArray}
     * for parsed text.
     * @param json {@code InputStream} for JSON text
     * @param charset charset to use in parsing
     *
     * @return parsed {@code JsonArray}
     * @throws ParserException if an exception occurred during parsing
     */
    public static JsonArray parseJsonArray(InputStream json, Charset charset) throws ParserException {
        Json5Parser parser = Json5Parser.create(json, charset);
        if ((parser.nextElement() & Json5Parser.ELEMENT_ARRAY) != 0)
            return parser.parseArray();
        else throw new IllegalArgumentException("first element is string is not JsonArray");
    }

    /**
     * Method validates JSON and returns first exception occurred during parsing. If no
     * exception occurred, {@code null} will be returned.
     * @param json JSON text to be validated
     *
     * @return first exception occurred during parsing or {@code null}
     */
    public static ParserException validate(String json) {
        try {
            Json5Parser parser = Json5Parser.create(json);
            while (parser.nextElement() != Json5Parser.NO_ELEMENT)
                parser.parseAny();
        } catch (ParserException e) {
            return e;
        }
        return null;
    }

    /**
     * Method validates JSON and returns first exception occurred during parsing. If no
     * exception occurred, {@code null} will be returned.
     * @param json JSON text reader to be validated
     *
     * @return first exception occurred during parsing or {@code null}
     */
    public static ParserException validate(Reader json) {
        try {
            Json5Parser parser = Json5Parser.create(json);
            while (parser.nextElement() != Json5Parser.NO_ELEMENT)
                parser.parseAny();
        } catch (ParserException e) {
            return e;
        }
        return null;
    }

    /**
     * Method validates JSON and returns first exception occurred during parsing. If no
     * exception occurred, {@code null} will be returned.
     * @param json {@code InputStream} for JSON text to be validated
     *
     * @return first exception occurred during parsing or {@code null}
     */
    public static ParserException validate(InputStream json) {
        try {
            Json5Parser parser = Json5Parser.create(json);
            while (parser.nextElement() != Json5Parser.NO_ELEMENT)
                parser.parseAny();
        } catch (ParserException e) {
            return e;
        }
        return null;
    }

    /**
     * Method validates JSON and returns first exception occurred during parsing. If no
     * exception occurred, {@code null} will be returned.
     * @param json {@code InputStream} for JSON text to be validated
     * @param charset charset to use in parsing
     *
     * @return first exception occurred during parsing or {@code null}
     */
    public static ParserException validate(InputStream json, Charset charset) {
        try {
            Json5Parser parser = Json5Parser.create(json, charset);
            while (parser.nextElement() != Json5Parser.NO_ELEMENT)
                parser.parseAny();
        } catch (ParserException e) {
            return e;
        }
        return null;
    }

    /**
     * Method modify JSON text to be readable by adding line breakers after opening brackets,
     * commas and closing brackets. <strong>Warning!</strong> this method does not validate text,
     * it only adds some line breakers and symbols of tabulation for making text more structured.
     * @param json JSON text to be beautified
     *
     * @return beautified JSON text
     */
    public static String beautify(String json) {
        PreParser pp = new PreParser(new StringReader(json));
        StringBuilder sb = new StringBuilder();
        char[] next = new char[1];
        try {
            while (pp.nextReadable(next)) {
                if (next[0] == '"' || next[0] == '\'') {
                    pp.reread();
                    beautifyString(pp, sb);
                } else if (next[0] == '{') {
                    pp.reread();
                    beautifyObject(pp, 0, sb);
                } else if (next[0] == '[') {
                    pp.reread();
                    beautifyArray(pp, 0, sb);
                } else sb.append(next[0]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred at " +
                    pp.position() + ": " + e.getMessage());
        }
        return sb.toString();
    }

    private static void beautifyString(PreParser json, StringBuilder sb) throws IOException {
        char bracket = json.next();
        sb.append(bracket);
        boolean isAlt = false;
        char[] next = new char[1];
        while (json.next(next)) {
            if (next[0] == bracket && !isAlt) {
                sb.append(bracket);
                return;
            }
            if (next[0] == '\\') isAlt = !isAlt;
            else isAlt = false;
            sb.append(next[0]);
        }
    }

    private static void beautifyObject(PreParser json, int level, StringBuilder sb) throws IOException {
        sb.append(json.next());
        char[] next = new char[1];
        level++;
        while (json.nextReadable(next)) {
            if (next[0] == '}') {
                sb.append(" ".repeat(4 * --level)).append('}');
                return;
            }
            if (sb.charAt(sb.length() - 1) == '\n')
                sb.append(" ".repeat(4 * level));
            if (next[0] == '"' || next[0] == '\'') {
                json.reread();
                beautifyString(json, sb);
            } else if (next[0] == ':') {
                sb.append(": ");
            } else if (next[0] == '{') {
                json.reread();
                beautifyObject(json, level, sb);
            } else if (next[0] == '[') {
                json.reread();
                beautifyArray(json, level, sb);
            } else if (next[0] == ',') {
                sb.append(",\n");
            } else sb.append(next[0]);
        }
    }

    private static void beautifyArray(PreParser json, int level, StringBuilder sb) throws IOException {
        sb.append(json.next());
        char[] next = new char[1];
        level++;
        while (json.nextReadable(next)) {
            if (next[0] == ']') {
                sb.append(" ".repeat(4 * --level)).append(']');
                return;
            }
            if (sb.charAt(sb.length() - 1) == '\n')
                sb.append(" ".repeat(4 * level));
            if (next[0] == '"' || next[0] == '\'') {
                json.reread();
                beautifyString(json, sb);
            } else if (next[0] == '{') {
                json.reread();
                beautifyObject(json, level, sb);
            } else if (next[0] == '[') {
                json.reread();
                beautifyArray(json, level, sb);
            } else if (next[0] == ',') {
                sb.append(",\n");
            } else sb.append(next[0]);
        }
    }
}
