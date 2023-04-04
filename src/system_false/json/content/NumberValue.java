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
 * Class for number JSON value. It can be any variant of number including
 * NaN and Infinity.
 * <el>
 *     <li>boolean: 0 -> false, any other -> true</li>
 *     <li>string: string view</li>
 * </el>
 */
public abstract class NumberValue extends Number implements JsonValue {
    /**
     * Method returns whether this number is NaN.
     *
     * @return {@code true} if this number is NaN, {@code false} otherwise
     */
    public boolean isNaN() {
        return false;
    }

    /**
     * Method returns whether this number is Infinity.
     *
     * @return {@code true} if this number is Infinity, {@code false} otherwise
     */
    public boolean isInfinity() {
        return false;
    }

    /**
     * Method converts this number to {@code long} value if it is possible. If originally
     * object is not integer or to big for long type, an exception will be thrown.
     *
     * @return {@code long} representation of this number
     */
    public abstract long asLong();

    /**
     * Method converts this number to {@code BigInteger} value if it is possible. If originally
     * object is not integer, an exception will be thrown.
     *
     * @return {@code BigInteger} representation of this number
     */
    public abstract BigInteger asBigInteger();

    /**
     * Method returns whether this number is integer or not.
     *
     * @return {@code true} if this number is integer, {@code false} otherwise
     */
    public abstract boolean isInteger();

    /**
     * Method converts this number to {@code double} value. This method will never throw an
     * exception.
     *
     * @return {@code double} representation of this number
     */
    public abstract double asDouble();

    /**
     * Method converts this number to {@code BigDecimal} value.
     *
     * @return {@code BigDecimal} representation of this number
     */
    public abstract BigDecimal asBigDecimal();

    /**
     * Method returns whether this number is decimal fraction or not.
     *
     * @return {@code true} if this number is decimal fraction, {@code false} otherwise
     */
    public abstract boolean isDecimal();

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public NumberValue asNumber() {
        return this;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public int intValue() {
        return (int) asLong();
    }

    @Override
    public long longValue() {
        return asLong();
    }

    @Override
    public float floatValue() {
        return (float) asDouble();
    }

    @Override
    public double doubleValue() {
        return asDouble();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    @Override
    public NumberValue copy() {
        try {
            return (NumberValue) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
