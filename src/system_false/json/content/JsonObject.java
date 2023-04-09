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

import java.util.*;

/**
 * Class for JSON object structure. It contains key-value pairs, where
 * key is {@link String} and value is any {@link JsonElement}. All
 * elements are sorted by names using {@link String#compareTo(String)}
 * method. In JSON string all keys will be replaced with JSON strings.
 * This object is unmodifiable and if it is needed to create specified
 * pairs, you should use {@link JsonObjectBuilder} instead.
 * @see JsonObjectBuilder
 */
public final class JsonObject extends AbstractMap<String, JsonElement> implements JsonStructure {

    /**
     * Map which contains key-value pairs.
     */
    TreeMap<String, JsonElement> values;

    /**
     * Constructor creates empty object.
     */
    public JsonObject() {
        this(Map.of());
    }

    /**
     * Constructor creates object with given key-value pairs.
     * @param values map which contains keys JSON values
     * @throws NullPointerException is any key or value is null
     */
    public JsonObject(Map<String, ? extends JsonElement> values) {
        for (var entry : values.entrySet()) {
            if (entry.getKey() == null)
                throw new NullPointerException("null key with value " + entry.getValue());
            if (entry.getValue() == null)
                throw new NullPointerException("null value with key " + entry.getKey());
        }
        this.values = new TreeMap<>(String::compareTo);
        this.values.putAll(values);
    }

    @Override
    public Set<Entry<String, JsonElement>> entrySet() {
        return values.entrySet();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public JsonElement get(Object key) {
        return values.get(key);
    }

    /**
     * Method checks whether element with given key is {@code JsonValue} or not.
     * @param key key of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonValue}, {@code false} otherwise
     * @see JsonValue
     */
    public boolean isValue(String key) {
        return values.get(key) instanceof JsonValue;
    }

    /**
     * Method returns element with given key and cast it to {@code JsonValue}.
     * @param key key of element to return.
     *
     * @return element at given index casted to {@code JsonValue}
     * @see JsonValue
     */
    public JsonValue getValue(String key) {
        return (JsonValue) values.get(key);
    }

    /**
     * Method checks whether element with given key is {@code JsonArray} or not.
     * @param key key of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonArray}, {@code false} otherwise
     * @see JsonArray
     */
    public boolean isArray(String key) {
        return values.get(key) instanceof JsonArray;
    }

    /**
     * Method returns element with given key and cast it to {@code JsonArray}.
     * @param key key of element to return.
     *
     * @return element at given index casted to {@code JsonArray}
     * @see JsonArray
     */
    public JsonArray getArray(String key) {
        return (JsonArray) values.get(key);
    }

    /**
     * Method checks whether element with given key is {@code JsonObject} or not.
     * @param key key of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonObject}, {@code false} otherwise
     * @see JsonObject
     */
    public boolean isObject(String key) {
        return values.get(key) instanceof JsonObject;
    }

    /**
     * Method returns element with given key and cast it to {@code JsonObject}.
     * @param key key of element to return.
     *
     * @return element at given index casted to {@code JsonObject}
     * @see JsonObject
     */
    public JsonObject getObject(String key) {
        return (JsonObject) values.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<JsonElement> values() {
        return values.values();
    }

    @Override
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        int i = 0, size = values.size();
        for (var entry : values.entrySet()) {
            sb.append(StringValue.toJSONString(entry.getKey())).append(": ");
            sb.append(entry.getValue().toJsonString());
            if (i++ < size - 1)
                sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toJsonString(int indent) {
        if (indent < 0)
            throw new IllegalArgumentException("negative indent");
        if (values.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        indent++;
        int i = 0, size = values.size();
        for (var entry : values.entrySet()) {
            sb.append(" ".repeat(indent * 4)).append(StringValue.toJSONString(entry.getKey())).append(": ");
            JsonElement je = entry.getValue();
            if (je instanceof JsonValue jv)
                sb.append(jv.toJsonString());
            else if (je instanceof JsonStructure js)
                sb.append(js.toJsonString(indent));
            else sb.append('"').append(je.getClass().getCanonicalName()).append('"');
            if (i++ < size - 1)
                sb.append(",\n");
            else sb.append('\n');
        }
        sb.append(" ".repeat(--indent * 4)).append('}');
        return sb.toString();
    }

    @Override
    public String toJson5String() {
        StringBuilder sb = new StringBuilder();
        int i = 0, size = values.size();
        sb.append('{');
        for (var entry : values.entrySet()) {
            if (StringValue.isECMAKey(entry.getKey()))
                sb.append(entry.getKey());
            else sb.append(StringValue.toJSONString(entry.getKey(), '\''));
            sb.append(": ");
            sb.append(entry.getValue().toJson5String());
            if (i++ < size - 1)
                sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toJson5String(int indent) {
        if (indent < 0)
            throw new IllegalArgumentException("negative indent");
        if (values.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder();
        int i = 0, size = values.size();
        sb.append("{\n");
        indent++;
        for (var entry : values.entrySet()) {
            sb.append(" ".repeat(indent * 4));
            if (StringValue.isECMAKey(entry.getKey()))
                sb.append(entry.getKey());
            else sb.append(StringValue.toJSONString(entry.getKey(), '\''));
            sb.append(": ");
            JsonElement je = entry.getValue();
            if (je instanceof JsonValue jv)
                sb.append(jv.toJson5String());
            else if (je instanceof JsonStructure js)
                sb.append(js.toJson5String(indent));
            else sb.append('"').append(je.getClass().getCanonicalName()).append('"');
            if (i++ < size - 1)
                sb.append(",\n");
            else sb.append('\n');
        }
        sb.append(" ".repeat(4 * --indent)).append('}');
        return sb.toString();
    }

    @Override
    public String toJson2String(String path) {
        if (isEmpty()) return path + "={}";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (var entry : values.entrySet()) {
            String key = StringValue.toJSONString(entry.getKey()).replaceAll("(?<!\\\\)/", "\\\\/");
            String curPath = path + '/' + key.substring(1, key.length() - 1);
            sb.append(entry.getValue().toJson2String(curPath));
            if (i++ < size() - 1) sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public JsonObject copy() {
        return clone();
    }

    @Override
    public JsonObject clone() {
        JsonObject clone;
        try {
            clone = (JsonObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.values = new TreeMap<>(String::compareTo);
        for (var entry : this.values.entrySet()) {
            clone.values.put(entry.getKey(), entry.getValue().copy());
        }
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonObject that = (JsonObject) o;

        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
