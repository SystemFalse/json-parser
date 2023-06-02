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

/**
 * Class for Infinity number value. Object of this class can not be
 * converted to any type except {@code String} and {@code double}.
 */
public final class InfinityValue extends NumberValue {
    /**
     * Whether infinity is negative of not.
     */
    final boolean negative;

    /**
     * Constructor creates object with default positive infinity value
     */
    public InfinityValue() {
        this(false);
    }

    /**
     * Constructor creates object with given sign
     *
     * @param negative whether infinity is negative or not
     */
    public InfinityValue(boolean negative) {
        this.negative = negative;
    }

    @Override
    public String toJson5String() {
        return String.valueOf(negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    }

    @Override
    public boolean isInfinity() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("Infinity can not be converted to boolean");
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public long asLong() {
        throw new UnsupportedOperationException("Infinity can not be converted to long");
    }

    @Override
    public BigInteger asBigInteger() {
        throw new UnsupportedOperationException("Infinity can not be converted to BigInteger");
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    @Override
    public double asDouble() {
        return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new UnsupportedOperationException("Infinity can not be converted to BigDecimal");
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
        throw new UnsupportedOperationException("Infinity is not supported by json");
    }

    @Override
    public String toJson2String(String path) {
        throw new UnsupportedOperationException("Infinity is not supported by json2");
    }

    @Override
    public NumberValue clone() {
        IntegerValue clone;
        try {
            clone = (IntegerValue) super.clone();
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

        InfinityValue that = (InfinityValue) o;

        return negative == that.negative;
    }

    @Override
    public int hashCode() {
        return (negative ? 1 : 0);
    }

    @Override
    public String toString() {
        return toJson5String();
    }
}
