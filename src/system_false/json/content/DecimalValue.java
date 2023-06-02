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
 * Class for decimal number value.
 */
public final class DecimalValue extends NumberValue {
    /**
     * Number value.
     */
    final BigDecimal value;

    /**
     * Constructor creates object with given value.
     * @param value {@code double} value of the number
     */
    public DecimalValue(double value) {
        this(BigDecimal.valueOf(value));
    }

    /**
     * Constructor creates object with given value.
     * @param value {@code BigDecimal} value of the number
     * @throws NullPointerException if value is null
     */
    public DecimalValue(BigDecimal value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toJson5String() {
        return toJsonString();
    }

    @Override
    public boolean asBoolean() {
        return !value.equals(BigDecimal.ZERO);
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
        return value.toBigIntegerExact();
    }

    @Override
    public boolean isInteger() {
        return value.signum() == 0 || value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0;
    }

    @Override
    public double asDouble() {
        return value.doubleValue();
    }

    @Override
    public BigDecimal asBigDecimal() {
        return value;
    }

    @Override
    public boolean isDecimal() {
        return value.signum() != 0 && value.scale() > 0 && value.stripTrailingZeros().scale() > 0;
    }

    @Override
    public String asString() {
        return value.toPlainString();
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public String toJsonString() {
        return value.toEngineeringString();
    }

    @Override
    public String toJson2String(String path) {
        return path + toJsonString();
    }

    @Override
    public NumberValue clone() {
        DecimalValue clone;
        try {
            clone = (DecimalValue) super.clone();
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

        if (o.getClass().equals(DecimalValue.class)) {
            DecimalValue that = (DecimalValue) o;
            return value.equals(that.value);
        } else if (o.getClass().equals(IntegerValue.class)) {
            IntegerValue that = (IntegerValue) o;
            return new BigDecimal(that.value).equals(value);
        } else return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
