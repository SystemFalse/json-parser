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
 * Class for JSON array structure. It contains ordinal sequence of {@link JsonElement}.
 * {@code JsonArray} is unmodifiable structure so all elements must be set before
 * creating object. If it is needed to create array with specified values, you should
 * use {@link JsonArrayBuilder} instead.
 * @see JsonArrayBuilder
 */
public final class JsonArray extends AbstractList<JsonElement> implements JsonStructure {
    /**
     * Path to this element.
     */
    JsonPath.BuildablePath path = new JsonPath.BuildablePath();

    /**
     * List that contains all elements.
     */
    ArrayList<JsonElement> values;

    /**
     * Constructor creates empty object.
     */
    public JsonArray() {
        this(Collections.emptyList());
    }

    /**
     * Constructor creates object with given elements.
     * @param values elements added in array
     */
    public JsonArray(Collection<JsonElement> values) {
        int i = 0;
        for (JsonElement e : values) {
            if (e == null)
                throw new NullPointerException("null value at index " + i);
            i++;
        }
        this.values = new ArrayList<>(values);
    }

    /**
     * Method that index all sub elements and if they contain elements, them will be
     * indexed too.
     * @param path path to use for this object
     */
    void resolvePath(JsonPath.BuildablePath path) {
        for (int i = 0; i < values.size(); i++) {
            JsonPath.BuildablePath curPath = path.clone();
            curPath.add(i);
            JsonElement je = values.get(i);
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
     * Method clears all paths from all elements and sub-elements of this array.
     */
    void clearPath() {
        for (JsonElement je : this) {
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
    public JsonElement get(int index) {
        return values.get(index);
    }

    /**
     * Method checks whether element at given index is {@code JsonValue} or not.
     * @param index index of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonValue}, {@code false} otherwise
     * @see JsonValue
     */
    public boolean isValue(int index) {
        return values.get(index) instanceof JsonValue;
    }

    /**
     * Method returns element at given index and cast it to {@code JsonValue}.
     * @param index index of element to return.
     *
     * @return element at given index casted to {@code JsonValue}
     * @see JsonValue
     */
    public JsonValue getValue(int index) {
        return (JsonValue) values.get(index);
    }

    /**
     * Method checks whether element at given index is {@code JsonArray} or not.
     * @param index index of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonArray}, {@code false} otherwise
     * @see JsonArray
     */
    public boolean isArray(int index) {
        return values.get(index) instanceof JsonArray;
    }

    /**
     * Method returns element at given index and cast it to {@code JsonArray}.
     * @param index index of element to return.
     *
     * @return element at given index casted to {@code JsonArray}
     * @see JsonArray
     */
    public JsonArray getArray(int index) {
        return (JsonArray) values.get(index);
    }

    /**
     * Method checks whether element at given index is {@code JsonObject} or not.
     * @param index index of element to check.
     *
     * @return {@code true} if pointed element is {@code JsonObject}, {@code false} otherwise
     * @see JsonObject
     */
    public boolean isObject(int index) {
        return values.get(index) instanceof JsonObject;
    }

    /**
     * Method returns element at given index and cast it to {@code JsonObject}.
     * @param index index of element to return.
     *
     * @return element at given index casted to {@code JsonObject}
     * @see JsonObject
     */
    public JsonObject getObject(int index) {
        return (JsonObject) values.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    @Override
    public JsonElement[] toArray() {
        return values.toArray(new JsonElement[0]);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return values.containsAll(c);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i).toJsonString());
            if (i < values.size() - 1) sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String toJsonString(int indent) {
        if (indent < 0)
            throw new IllegalArgumentException("negative indent");
        if (values.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        indent++;
        for (int i = 0, size = values.size(); i < size; i++) {
            sb.append(spaces(indent * 4));
            JsonElement je = values.get(i);
            if (je instanceof JsonValue) {
                JsonValue jv = (JsonValue) je;
                sb.append(jv.toJsonString());
            } else if (je instanceof JsonStructure) {
                JsonStructure js = (JsonStructure) je;
                sb.append(js.toJsonString(indent));
            } else sb.append('"').append(je.getClass().getCanonicalName()).append('"');
            if (i < size - 1)
                sb.append(",\n");
            else sb.append('\n');
        }
        sb.append(spaces(--indent * 4)).append(']');
        return sb.toString();
    }

    @Override
    public String toJson5String(int indent) {
        if (indent < 0)
            throw new IllegalArgumentException("negative indent");
        if (values.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        indent++;
        for (int i = 0, size = values.size(); i < size; i++) {
            sb.append(spaces(indent * 4));
            JsonElement je = values.get(i);
            if (je instanceof JsonValue) {
                JsonValue jv = (JsonValue) je;
                sb.append(jv.toJson5String());
            } else if (je instanceof JsonStructure) {
                JsonStructure js = (JsonStructure) je;
                sb.append(js.toJson5String(indent));
            } else sb.append('"').append(je.getClass().getCanonicalName()).append('"');
            if (i < size - 1)
                sb.append(",\n");
            else sb.append('\n');
        }
        sb.append(spaces(--indent * 4)).append(']');
        return sb.toString();
    }

    @Override
    public String toJson2String(String path) {
        if (isEmpty()) return path + "=[]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            String curPath = path + '/' + i;
            sb.append(get(i).toJson2String(curPath));
            if (i < size() - 1) sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public JsonArray copy() {
        return clone();
    }

    @Override
    public JsonPath getPath() {
        return path;
    }

    @Override
    public JsonArray clone() {
        JsonArray clone;
        try {
            clone = (JsonArray) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.values = new ArrayList<>(this.values.size());
        for (int i = 0; i < this.values.size(); i++) {
            clone.values.add(this.values.get(i).copy());
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

        JsonArray that = (JsonArray) o;

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
