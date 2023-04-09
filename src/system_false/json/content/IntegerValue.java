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
import java.util.Objects;

/**
 * Class for integer number value. It also can contain radix of this
 * number. Radix influence only on string representation.
 */
public final class IntegerValue extends NumberValue {
    /**
     * Number value.
     */
    final BigInteger value;
    /**
     * Number radix.
     */
    final int radix;

    /**
     * Constructor creates object with given value. Radix is 10.
     * @param value {@code long} value of the number
     */
    public IntegerValue(long value) {
        this(value, 10);
    }

    /**
     * Constructor creates object with given value and radix. Radix can
     * be 2, 8, 10 or 16.
     * @param value {@code long} value of the number
     * @param radix radix for string conversion
     * @throws IllegalArgumentException if radix is not one of possible
     */
    public IntegerValue(long value, int radix) {
        this(BigInteger.valueOf(value), radix);
    }

    /**
     * Constructor creates object with given value. Radix is 10.
     * @param value {@code BigInteger} value of the number
     * @throws NullPointerException if value is null
     */
    public IntegerValue(BigInteger value) {
        this(value, 10);
    }

    /**
     * Constructor creates object with given value and radix. Radix can
     * be 2, 8, 10 or 16.
     * @param value {@code BigInteger} value of the number
     * @param radix radix for string conversion
     * @throws IllegalArgumentException if radix is not one of possible
     */
    public IntegerValue(BigInteger value, int radix) {
        Objects.requireNonNull(value);
        if (radix != 2 && radix != 8 && radix != 10 && radix != 16)
            throw new IllegalArgumentException("illegal radix");
        this.value = value;
        this.radix = radix;
    }

    @Override
    public String toJson5String() {
        if (radix == 2)
            return "0b" + value.toString(2);
        if (radix == 8)
            return "0o" + value.toString(8);
        if (radix == 16)
            return "0x" + value.toString(16);
        return value.toString();
    }

    @Override
    public boolean asBoolean() {
        return !value.equals(BigInteger.ZERO);
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public long asLong() {
        return value.longValueExact();
    }

    @Override
    public BigInteger asBigInteger() {
        return value;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    @Override
    public double asDouble() {
        return value.doubleValue();
    }

    @Override
    public BigDecimal asBigDecimal() {
        return new BigDecimal(value);
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public String asString() {
        return value.toString();
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public String toJsonString() {
        if (radix != 10)
            throw new UnsupportedOperationException("radix not equal to 10 is not supported by json");
        return toJson5String();
    }

    @Override
    public String toJson2String(String path) {
        if (radix != 10)
            throw new UnsupportedOperationException("radix not equal to 10 is not supported by json2");
        return path + toJson5String();
    }

    @Override
    public NumberValue clone() {
        IntegerValue clone;
        try {
            clone = (IntegerValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o.getClass().equals(IntegerValue.class)) {
            IntegerValue that = (IntegerValue) o;
            return value.equals(that.value);
        } else if (o.getClass().equals(DecimalValue.class)) {
            DecimalValue that = (DecimalValue) o;
            return new BigDecimal(value).equals(that.value);
        } else return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
