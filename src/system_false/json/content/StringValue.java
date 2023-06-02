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

package system_false.json.content;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for string JSON value. It can be any character sequence.
 * <el>
 *     <li>boolean: "true" -> true, "false" -> false, other -> exception</li>
 *     <li>number: parsed number, exception</li>
 * </el>
 */
public final class StringValue implements JsonValue, CharSequence {
    /**
     * Path to this element.
     */
    JsonPath.BuildablePath path = new JsonPath.BuildablePath();

    /**
     * String value.
     */
    private final String value;

    /**
     * Constructor creates object with default empty value.
     */
    public StringValue() {
        this("");
    }

    /**
     * Constructor creates object with given value.
     *
     * @param value {@code String} value
     * @throws NullPointerException if value is null
     */
    public StringValue(String value) {
        if (value == null)
            throw new NullPointerException("null value");
        this.value = value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean asBoolean() {
        if (value.equalsIgnoreCase("true"))
            return true;
        else if (value.equalsIgnoreCase("false"))
            return false;
        throw new IllegalArgumentException("string value can not be converted to boolean");
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    private static final Pattern NUMBER = Pattern.compile("^(?<sign>[+\\-])?" +
            "(?<number>(?<special>Infinity|NaN)|(?:(?:0|[1-9]\\d*)(?<decimal>\\.?)(?:\\d+)?(?:[eE]([+\\-]?\\d+))?|" +
            "(?<radix>(?<binary>0b[01]+)|(?<octal>0o[0-7]+)|(?<hex>0x\\p{XDigit}+))))$");

    @Override
    public NumberValue asNumber() {
        Matcher m = NUMBER.matcher(value);
        if (!m.matches())
            throw new IllegalArgumentException("string is not number");
        if (m.group("number") == null)
            throw new IllegalArgumentException("string is does no contain number");
        if (m.group("special") != null) {
            boolean sign = (m.group("sign") == null || m.group("sign").equals("+"));
            if (m.group("special").equals("Infinity")) return new InfinityValue(!sign);
            else return new NaNValue();
        }
        if (m.group("radix") != null && m.group("decimal") == null) {
            boolean sign = (m.group("sign") == null || m.group("sign").equals("+"));
            if (m.group("binary") != null) return new IntegerValue(sign
                    ? new BigInteger(value.substring(2), 2)
                    : new BigInteger(value.substring(2), 2).negate(), 2);
            else if (m.group("octal") != null) return new IntegerValue(sign
                    ? new BigInteger(value.substring(2), 8)
                    : new BigInteger(value.substring(2), 8).negate(), 8);
            else return new IntegerValue(sign
                    ? new BigInteger(value.substring(2), 16)
                    : new BigInteger(value.substring(2), 16).negate(), 16);
        } else if (m.group("decimal") == null)
            return new IntegerValue(new BigInteger(value));
        else return new DecimalValue(new BigDecimal(value));
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String toJsonString() {
        return toJSONString(value);
    }

    @Override
    public String toJson5String() {
        return toJSONString(value, '\'');
    }

    @Override
    public String toJson2String(String path) {
        String[] lines = value.split("\\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(path).append("=\"").append(lines[i]);
            if (i < lines.length - 1) sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public StringValue copy() {
        return clone();
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public StringValue subSequence(int start, int end) {
        return new StringValue(value.substring(start, end));
    }

    @Override
    public JsonPath getPath() {
        return path;
    }

    @Override
    public StringValue clone() {
        StringValue clone;
        try {
            clone = (StringValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.path = path.clone();
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringValue that = (StringValue) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    /**
     * Method converts given string to JSON format including "" brackets. All special
     * characters will be replaced with JSON escape sequences.
     * @param value {@code String} value to convert
     *
     * @return JSON view of string with brackets
     */
    public static String toJSONString(String value) {
        return toJSONString(value, '"');
    }

    /**
     * Method converts given string to JSON format including given brackets. All special
     * characters will be replaced with JSON escape sequences.
     * @param value {@code String} value to convert
     * @param bracket type of brackets, can be ' or "
     *
     * @return JSON view of string with brackets
     * @throws IllegalArgumentException bracket is not possible
     * @throws NullPointerException is value is null
     */
    public static String toJSONString(String value, char bracket) {
        if (bracket != '"' && bracket != '\'')
            throw new IllegalArgumentException("illegal bracket symbol: " + bracket);
        StringBuilder sb = new StringBuilder();
        sb.append(bracket);
        for (char ch : value.toCharArray()) {
            if (ch == '\\') sb.append("\\\\");
            else if (ch == '\n') sb.append("\\n");
            else if (ch == '\r') sb.append("\\r");
            else if (ch == '\t') sb.append("\\t");
            else if (ch == '\f') sb.append("\\f");
            else if (ch == '\b') sb.append("\\b");
            else if (ch == 0) sb.append("\\0");
            else if (ch == 0x0B) sb.append("\\v");
            else if (ch == bracket) sb.append('\\').append(bracket);
            else if (Character.isISOControl(ch) || ch > 0xff && (!Character.isAlphabetic(ch))) {
                String hex = Integer.toHexString(ch);
                hex = zeros(4 - hex.length()) + hex;
                sb.append("\\u").append(hex);
            } else sb.append(ch);
        }
        sb.append(bracket);
        return sb.toString();
    }

    private static String zeros(int count) {
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < count; i++) {
            zeros.append("0");
        }
        return zeros.toString();
    }

    /**
     * Method converts JSON string to its usual view and append to {@link StringBuilder}.
     * String <strong>must contain</strong> begin and end brackets (").
     * @param json string that contains JSON string
     * @param sb target where usual string will be appended to
     * @throws IllegalArgumentException if json string is incorrect
     * @throws NullPointerException if any of arguments is null
     */
    public static void fromJSONString(String json, StringBuilder sb) {
        fromJSONString(json, json.indexOf('"') + 1, sb);
    }

    /**
     * Method converts JSON string to its usual view and append to {@link StringBuilder}.
     * Parser starts with given position. Given position must point to begin bracket of the string.
     * @param json string that contains JSON string
     * @param first position to start paring the string
     * @param sb target where usual string will be appended to
     * @throws IllegalArgumentException if json string or first index is incorrect
     * @throws NullPointerException if any of arguments is null
     */
    public static void fromJSONString(String json, int first, StringBuilder sb) {
        fromJSONString(json, first, '"', sb);
    }

    /**
     * Method converts JSON string to its usual view and append to {@link StringBuilder}.
     * Parser starts with given position. Given position must point to begin bracket of the string.
     * Bracket can be ' or ".
     * @param json string that contains JSON string
     * @param first position to start paring the string
     * @param bracket bracket of the string
     * @param sb target where usual string will be appended to
     * @throws IllegalArgumentException if json string, first index or bracket is incorrect
     * @throws NullPointerException if any of arguments is null
     */
    public static void fromJSONString(String json, int first, char bracket, StringBuilder sb) {
        if (bracket != '"' && bracket != '\'')
            throw new IllegalArgumentException("illegal bracket symbol: " + bracket);
        boolean alt = false;
        for (int i = first; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (alt) {
                if (ch == 'u') {
                    if (i > json.length() - 4)
                        throw new IllegalArgumentException("no place for unicode character");
                    String code = json.substring(i, i + 4);
                    int hex;
                    try {
                        hex = Integer.parseInt(code, 16);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("illegal hex code of character in json string");
                    }
                    if (hex < 0)
                        throw new IllegalArgumentException("negative character code");
                    sb.append((char) hex);
                    i += 3;
                }
                else if (ch == '"') sb.append('"');
                else if (ch == '\'') sb.append('\'');
                else if (ch == 'v') sb.append((char) 0x0B);
                else if (ch == '0') sb.append((char) 0);
                else if (ch == 'b') sb.append('\b');
                else if (ch == 'f') sb.append('\f');
                else if (ch == 't') sb.append('\t');
                else if (ch == 'r') sb.append('\r');
                else if (ch == 'n') sb.append('\n');
                else if (ch == '\\') sb.append('\\');
                else if (ch == '/') sb.append('/');
                else throw new IllegalArgumentException("unknown escape sequence \"\\" + ch + '"');
                alt = false;
            } else {
                if (ch == '\\') alt = true;
                else if (ch == bracket) return;
                else sb.append(ch);
            }
        }
        throw new IllegalStateException("no end of string found");
    }

    /**
     * Method converts JSON data in reader to string and append to {@link StringBuilder}.
     * First reader character <strong>must not be</strong> begin bracket (").
     * @param reader reader that contains JSON string
     * @param sb target where usual string will be appended to
     * @throws IllegalArgumentException if reader json is incorrect
     * @throws NullPointerException if any of arguments is null
     */
    public static void fromJSONString(Reader reader, StringBuilder sb) throws IOException {
        fromJSONString(reader, '"', sb);
    }

    /**
     * Method converts JSON data in reader to string and append to {@link StringBuilder}.
     * First reader character <strong>must not be</strong> begin bracket (").
     * Bracket can be ' or ".
     * @param reader reader that contains JSON string
     * @param bracket bracket of the string
     * @param sb target where usual string will be appended to
     * @throws IllegalArgumentException if reader json is incorrect
     * @throws NullPointerException if any of arguments is null
     */
    public static void fromJSONString(Reader reader, char bracket, StringBuilder sb) throws IOException {
        if (bracket != '"' && bracket != '\'')
            throw new IllegalArgumentException("illegal bracket symbol: " + bracket);
        boolean alt = false;
        for (char[] json = new char[1]; reader.read(json) != 0;) {
            char ch = json[0];
            if (alt) {
                if (ch == 'u') {
                    char[] code = new char[4];
                    if (reader.read(code) < 4)
                        throw new IllegalArgumentException("no place for unicode character");
                    int hex;
                    try {
                        hex = Integer.parseInt(new String(code), 16);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("illegal hex code of character in json string");
                    }
                    if (hex < 0)
                        throw new IllegalArgumentException("negative character code");
                    sb.append((char) hex);
                }
                else if (ch == '"') sb.append('"');
                else if (ch == '\'') sb.append('\'');
                else if (ch == 'v') sb.append((char) 0x0B);
                else if (ch == '0') sb.append((char) 0);
                else if (ch == 'b') sb.append('\b');
                else if (ch == 'f') sb.append('\f');
                else if (ch == 't') sb.append('\t');
                else if (ch == 'r') sb.append('\r');
                else if (ch == 'n') sb.append('\n');
                else if (ch == '\\') sb.append('\\');
                else throw new IllegalArgumentException("unknown escape sequence \"\\" + ch + '"');
                alt = false;
            } else {
                if (ch == '\\') alt = true;
                else if (ch == bracket) return;
                else sb.append(ch);
            }
        }
        throw new IllegalStateException("no end of string found");
    }

    /**
     * List of reserved names in JavaScript. It is used to check object key to be identifiable name.
     */
    private static final List<String> ECMA_RESERVED_NAMES = Arrays.asList(
            "break", "do", "instanceof", "typeof",
            "case", "else", "new", "var",
            "catch", "finally", "return", "void",
            "continue", "for", "switch", "while",
            "debugger", "function", "this", "with",
            "default", "if", "throw", "delete",
            "in", "try"
    );

    /**
     * Pattern that checks whether string is correct JavaScript identifier.
     */
    private static final Pattern ECMA_NAME_PATTERN = Pattern.compile(
            "^\\p{javaJavaIdentifierStart}\\p{javaUnicodeIdentifierPart}*$");

    /**
     * Method check string whether it can be used as JavaScript identifiable name.
     * @param key string to check
     *
     * @return {@code true} if given string can be used as JavaScript identifiable name, {@code false} otherwise.
     */
    public static boolean isECMAKey(String key) {
        if (!ECMA_NAME_PATTERN.matcher(key).matches())
            return false;
        return !ECMA_RESERVED_NAMES.contains(key);
    }
}
