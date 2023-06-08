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

import java.math.BigDecimal;
import java.math.BigInteger;
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
    void resolvePath(JsonPath path) {
        for (int i = 0; i < values.size(); i++) {
            JsonPath curPath = path.array(i);
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
    public void sort(Comparator<? super JsonElement> c) {
        values.sort(c);
    }

    /**
     * Returns {@code true} if this list contains null element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one null element.
     *
     * @return {@code true} if this list contains null element
     */
    public boolean containsNull() {
        for (JsonElement je : values) {
            if (je instanceof NullValue) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains NaN element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one NaN element.
     *
     * @return {@code true} if this list contains NaN element
     */
    public boolean containsNaN() {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && Double.isNaN(((NumberValue) je).doubleValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains infinity element with specified sign.
     * More formally, returns {@code true} if and only if this list contains
     * at least one infinity element with equal sign.
     * @param negative sign of infinity to find
     *
     * @return {@code true} if this list contains infinity element with specified sign
     */
    public boolean containsInfinity(boolean negative) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && Double.isInfinite(((NumberValue) je).asDouble())) {
                if (((NumberValue) je).doubleValue() > 0 != negative) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains boolean element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one boolean element with equal value.
     * @param value {@code boolean} value to find
     *
     * @return {@code true} if this list contains boolean element with specified value
     */
    public boolean contains(boolean value) {
        for (JsonElement je : values) {
            if (je instanceof BooleanValue && ((BooleanValue) je).asBoolean() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code byte} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(byte value) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).doubleValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code short} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(short value) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).doubleValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code int} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(int value) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).doubleValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code float} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(float value) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).doubleValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code long} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(long value) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).doubleValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code double} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(double value) {
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).doubleValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code BigInteger} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(BigInteger value) {
        if (value == null) return false;
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).asBigInteger().equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains number element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code BigDecimal} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(BigDecimal value) {
        if (value == null) return false;
        for (JsonElement je : values) {
            if (je instanceof NumberValue && ((NumberValue) je).asBigDecimal().equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this list contains string element with specified value.
     * More formally, returns {@code true} if and only if this list contains
     * at least one number element with equal value.
     * @param value {@code String} value to find
     *
     * @return {@code true} if this list contains number element with specified value
     */
    public boolean contains(String value) {
        if (value == null) return false;
        for (JsonElement je : values) {
            if (je instanceof StringValue && ((StringValue) je).asString().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private class Itr implements Iterator<JsonElement> {
        int index;

        @Override
        public boolean hasNext() {
            return index < values.size();
        }

        @Override
        public JsonElement next() {
            return values.get(index++);
        }
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return new Itr();
    }

    private class ListItr implements ListIterator<JsonElement> {
        int index;

        ListItr(int start) {
            index = start;
        }

        @Override
        public boolean hasNext() {
            return index < values.size();
        }

        @Override
        public JsonElement next() {
            return values.get(index++);
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public JsonElement previous() {
            return values.get(--index);
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void set(JsonElement jsonElement) {
            throw new UnsupportedOperationException("set");
        }

        @Override
        public void add(JsonElement jsonElement) {
            throw new UnsupportedOperationException("add");
        }
    }

    @Override
    public ListIterator<JsonElement> listIterator(int index) {
        return new ListItr(index);
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
        return path.asJsonPath();
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
