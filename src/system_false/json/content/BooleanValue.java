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

/**
 * Class for boolean JSON value. It can be true or false.
 * It converts to other types based on value.
 * <el>
 *     <li>number: true -> 1, false -> 0</li>
 *     <li>string: true -> "true", false -> "false"</li>
 * </el>
 */
public final class BooleanValue implements JsonValue {
    /**
     * Path to this element.
     */
    JsonPath.BuildablePath path = new JsonPath.BuildablePath();

    /**
     * Primitive {@code boolean} value of this object.
     */
    private final boolean value;

    /**
     * Constructor creates object with default value {@code false}.
     */
    public BooleanValue() {
        this(false);
    }

    /**
     * Constructor creates object with given value.
     * @param value {@code boolean} value
     */
    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public NumberValue asNumber() {
        return new IntegerValue(value ? 1 : 0);
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public String asString() {
        return asBoolean() ? "true" : "false";
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public String toJsonString() {
        return value ? "true" : "false";
    }

    @Override
    public String toJson2String(String path) {
        return path + '=' + toJsonString();
    }

    @Override
    public BooleanValue copy() {
        return clone();
    }

    @Override
    public JsonPath getPath() {
        return path;
    }

    @Override
    public BooleanValue clone() {
        BooleanValue clone;
        try {
            clone = (BooleanValue) super.clone();
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

        BooleanValue that = (BooleanValue) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
