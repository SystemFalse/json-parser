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
     * Path to this element.
     */
    JsonPath.BuildablePath path = new JsonPath.BuildablePath();

    /**
     * Map which contains key-value pairs.
     */
    TreeMap<String, JsonElement> values;

    /**
     * Constructor creates empty object.
     */
    public JsonObject() {
        this(Collections.emptyMap());
    }

    /**
     * Constructor creates object with given key-value pairs.
     * @param values map which contains keys JSON values
     *
     * @throws NullPointerException is any key or value is null
     */
    public JsonObject(Map<String, ? extends JsonElement> values) {
        values.forEach((key, value) -> {
            if (key == null)
                throw new NullPointerException("null key with value " + value);
            if (value == null)
                throw new NullPointerException("null value with key " + key);
        });
        this.values = new TreeMap<>(String::compareTo);
        this.values.putAll(values);
    }

    /**
     * Method that index all sub elements and if they contain elements, them will be
     * indexed too.
     * @param path path to use for this object
     */
    void resolvePath(JsonPath path) {
        for (Entry<String, ? extends JsonElement> entry : values.entrySet()) {
            try {
                JsonPath.checkName(entry.getKey());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("path \"" + path + "\", element \""
                        + entry.getKey() + "\": name is incorrect");
            }
            JsonPath curPath;
            if (!entry.getKey().isEmpty()) curPath = path.object(entry.getKey());
            else curPath = path.object("\\0");
            JsonElement je = entry.getValue();
            if (je instanceof JsonArray) {
                ((JsonArray) je).resolvePath(curPath);
            } else if (je instanceof JsonObject) {
                ((JsonObject) je).resolvePath(curPath);
            } else if (je.getPath() instanceof JsonPath.BuildablePath) {
                ((JsonPath.BuildablePath) je.getPath()).set(curPath);
            }
        }
        this.path.set(path);
    }

    /**
     * Method clears all paths from all elements and sub-elements of this object.
     */
    void clearPath() {
        for (JsonElement je : values.values()) {
            if (je instanceof JsonArray) {
                ((JsonArray) je).clearPath();
            } else if (je instanceof JsonObject) {
                ((JsonObject) je).clearPath();
            } else if (je.getPath() instanceof JsonPath.BuildablePath) {
                ((JsonPath.BuildablePath) je.getPath()).clear();
            }
        }
        path.clear();
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

    /**
     * Returns the value to which the specified key is mapped,
     * or throws {@link NoSuchElementException} if this map contains no mapping for the key.
     * <p>
     * More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}. (There can be at most one such mapping.)
     * </p>
     * @param key the key whose associated value is to be returned
     *
     * @return the value to which the specified key is mapped
     * @throws ClassCastException if the key is of an inappropriate type for this map
     * @throws NoSuchElementException if the specified key is not mapped
     */
    @Override
    public JsonElement get(Object key) {
        if (!containsKey(key)) throw new NoSuchElementException("no value for given key");
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
        return get(key) instanceof JsonValue;
    }

    /**
     * Method returns element with given key and cast it to {@code JsonValue}.
     * @param key key of element to return.
     *
     * @return element at given index casted to {@code JsonValue}
     * @see JsonValue
     */
    public JsonValue getValue(String key) {
        return (JsonValue) get(key);
    }

    /**
     * Method checks whether element with given key is {@code JsonArray} or not.
     * @param key key of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonArray}, {@code false} otherwise
     * @see JsonArray
     */
    public boolean isArray(String key) {
        return get(key) instanceof JsonArray;
    }

    /**
     * Method returns element with given key and cast it to {@code JsonArray}.
     * @param key key of element to return.
     *
     * @return element at given index casted to {@code JsonArray}
     * @see JsonArray
     */
    public JsonArray getArray(String key) {
        return (JsonArray) get(key);
    }

    /**
     * Method checks whether element with given key is {@code JsonObject} or not.
     * @param key key of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonObject}, {@code false} otherwise
     * @see JsonObject
     */
    public boolean isObject(String key) {
        return get(key) instanceof JsonObject;
    }

    /**
     * Method returns element with given key and cast it to {@code JsonObject}.
     * @param key key of element to return.
     *
     * @return element at given index casted to {@code JsonObject}
     * @see JsonObject
     */
    public JsonObject getObject(String key) {
        return (JsonObject) get(key);
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
    public JsonElement getOrDefault(Object key, JsonElement defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@link NullValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     *
     * @return the value to which the specified key is mapped, or
     * {@code NullValue} if this map contains no mapping for the key
     * @see NullValue
     */
    public JsonElement getOrNull(String key) {
        JsonElement je;
        return (je = values.get(key)) != null ? je : NullValue.NULL;
    }

    /**
     * Returns the value casted into {@link BooleanValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code BooleanValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code BooleanValue} if this map contains no mapping for the key
     * @see BooleanValue
     */
    public BooleanValue getOrDefault(String key, boolean defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (BooleanValue) je : new BooleanValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrDefault(String key, byte defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : new IntegerValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrDefault(String key, short defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : new IntegerValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrDefault(String key, int defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : new IntegerValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrDefault(String key, float defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : new DecimalValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrDefault(String key, long defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : new IntegerValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrDefault(String key, double defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : new DecimalValue(defaultValue);
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     *
     * @return the value to which the specified key is mapped, or
     * {@code NaN} wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrNaN(String key) {
        JsonElement je;
        return (je = values.get(key)) != null ? (NumberValue) je : NaNValue.NaN;
    }

    /**
     * Returns the value casted into {@link NumberValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code NumberValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param negative will defaultValue be negative or not infinity
     *
     * @return the value to which the specified key is mapped, or
     * +/-Infinity wrapped into {@code NumberValue} if this map contains no mapping for the key
     * @see NumberValue
     */
    public NumberValue getOrInfinity(String key, boolean negative) {
        JsonElement je;
        return  (je = values.get(key)) != null ? (NumberValue) je : new InfinityValue(negative);
    }

    /**
     * Returns the value casted into {@link StringValue} to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code StringValue} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue value that will be returned if specified key is not mapped
     *
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} wrapped into {@code StringValue} if this map contains no mapping for the key
     * @see StringValue
     */
    public StringValue getOrDefault(String key, String defaultValue) {
        JsonElement je;
        return (je = values.get(key)) != null ? (StringValue) je : new StringValue(defaultValue);
    }

    @Override
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        int i = 0, size = values.size();
        for (Entry<String, JsonElement> entry : values.entrySet()) {
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
        for (Entry<String, JsonElement> entry : values.entrySet()) {
            sb.append(spaces(indent * 4)).append(StringValue.toJSONString(entry.getKey())).append(": ");
            JsonElement je = entry.getValue();
            if (je instanceof JsonValue) {
                JsonValue jv = (JsonValue) je;
                sb.append(jv.toJsonString());
            } else if (je instanceof JsonStructure) {
                JsonStructure js = (JsonStructure) je;
                sb.append(js.toJsonString(indent));
            } else sb.append('"').append(je.getClass().getCanonicalName()).append('"');
            if (i++ < size - 1)
                sb.append(",\n");
            else sb.append('\n');
        }
        sb.append(spaces(--indent * 4)).append('}');
        return sb.toString();
    }

    @Override
    public String toJson5String() {
        StringBuilder sb = new StringBuilder();
        int i = 0, size = values.size();
        sb.append('{');
        for (Entry<String, JsonElement> entry : values.entrySet()) {
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
        for (Entry<String, JsonElement> entry : values.entrySet()) {
            sb.append(spaces(indent * 4));
            if (StringValue.isECMAKey(entry.getKey()))
                sb.append(entry.getKey());
            else sb.append(StringValue.toJSONString(entry.getKey(), '\''));
            sb.append(": ");
            JsonElement je = entry.getValue();
            if (je instanceof JsonValue) {
                JsonValue jv = (JsonValue) je;
                sb.append(jv.toJson5String());
            } else if (je instanceof JsonStructure) {
                JsonStructure js = (JsonStructure) je;
                sb.append(js.toJson5String(indent));
            } else sb.append('"').append(je.getClass().getCanonicalName()).append('"');
            if (i++ < size - 1)
                sb.append(",\n");
            else sb.append('\n');
        }
        sb.append(spaces(4 * --indent)).append('}');
        return sb.toString();
    }

    @Override
    public String toJson2String(String path) {
        if (isEmpty()) return path + "={}";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Entry<String, JsonElement> entry : values.entrySet()) {
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
    public JsonPath getPath() {
        return path.asJsonPath();
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
        for (Entry<String, JsonElement> entry : this.values.entrySet()) {
            clone.values.put(entry.getKey(), entry.getValue().copy());
        }
        clone.path = path.clone();
        return clone;
    }

    private String spaces(int count) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < count; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
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
