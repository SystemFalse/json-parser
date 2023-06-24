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

package org.system_false.json.content;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Class for NaN number value. Object of this class can not be
 * converted to any type except {@code String} and {@code double}.
 */
public final class NaNValue extends NumberValue {
    /**
     * Default NaN value.
     */
    public static final NaNValue NaN = new NaNValue();

    /**
     * Path to this element.
     */
    JsonPath.BuildablePath path = new JsonPath.BuildablePath();

    @Override
    public boolean isNaN() {
        return true;
    }

    @Override
    public String toJson5String() {
        return "NaN";
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("NaN can not be converted to boolean");
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public long asLong() {
        throw new UnsupportedOperationException("NaN can not be converted to long");
    }

    @Override
    public BigInteger asBigInteger() {
        throw new UnsupportedOperationException("NaN can not be converted to BigInteger");
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    @Override
    public double asDouble() {
        return Double.NaN;
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new UnsupportedOperationException("NaN can not be converted to BigDecimal");
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public String asString() {
        return toJson5String();
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public String toJsonString() {
        throw new UnsupportedOperationException("NaN is not supported by json");
    }

    @Override
    public String toJson2String(String path) {
        throw new UnsupportedOperationException("NaN is not supported by json2");
    }

    @Override
    public NumberValue clone() {
        NaNValue clone;
        try {
            clone = (NaNValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.path = path.clone();
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        return o.getClass().equals(NaNValue.class);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "NaN";
    }
}
