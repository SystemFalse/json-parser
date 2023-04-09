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
 * Class for null JSON value. It is the simplest element.
 * It converts to other type as if them were default.
 * <el>
 *     <li>boolean: false</li>
 *     <li>number: 0</li>
 *     <li>string: "null"</li>
 * </el>
 */
public final class NullValue implements JsonValue {
    /**
     * Default null value.
     */
    public static final NullValue NULL = new NullValue();

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public NumberValue asNumber() {
        return new IntegerValue(0);
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public String toJsonString() {
        return "null";
    }

    @Override
    public String toJson2String(String path) {
        return path + "=null";
    }

    @Override
    public NullValue copy() {
        return clone();
    }

    @Override
    public NullValue clone() {
        NullValue clone;
        try {
            clone = (NullValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        return obj instanceof NullValue;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
