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
 * Class that helps to create JSON arrays. Majority of methods returns
 * current object, so structure can be built using sequence of
 * methods add(...). To finish constructing, method {@link #build()}
 * must be used. Default constructor is private so to create an instance
 * of this class methods {@link #create()} and {@link #create(JsonArray)}
 * should be used.
 * @see #build(boolean)
 * @see #create()
 * @see #create(JsonArray)
 */
public class JsonArrayBuilder implements StructureBuilder {
    private static final String PACKAGE_NAME = JsonArrayBuilder.class.getPackage().getName();

    /**
     * Field that contains constructing instance of {@code JsonArray}.
     */
    JsonArray value;
    /**
     * Field that shows whether method {@link #build()} was invoked.
     */
    private boolean built;

    /**
     * Private constructor that creates empty object.
     */
    private JsonArrayBuilder() {
        value = new JsonArray();
    }

    /**
     * Method that checks whether field {@link #built} is {@code true} and
     * throws an exception.
     * @throws IllegalStateException if field {@link #built} is {@code true}
     */
    private void checkBuilt() {
        if (built) throw new IllegalStateException("array is already built");
    }

    /**
     * Method adds to the end of JSON array {@code null} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder addNull() {
        return add(new NullValue());
    }

    /**
     * Method adds to the end of JSON array {@code boolean} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see BooleanValue#BooleanValue(boolean)
     */
    public JsonArrayBuilder addBoolean(boolean value) {
        return add(new BooleanValue(value));
    }

    /**
     * Method adds to the end of JSON array {@code long} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see InfinityValue#InfinityValue()
     */
    public JsonArrayBuilder addNumber(long value) {
        return add(new IntegerValue(value));
    }

    /**
     * Method adds to the end of JSON array {@code long} element with given
     * radix.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see IntegerValue#IntegerValue(long, int)
     */
    public JsonArrayBuilder addNumber(long value, int radix) {
        return add(new IntegerValue(value, radix));
    }

    /**
     * Method adds to the end of JSON array {@code BigInteger} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see IntegerValue#IntegerValue(BigInteger)
     */
    public JsonArrayBuilder addNumber(BigInteger value) {
        return add(new IntegerValue(value));
    }

    /**
     * Method adds to the end of JSON array {@code BigInteger} element with
     * given radix.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see IntegerValue#IntegerValue(BigInteger, int)
     */
    public JsonArrayBuilder addNumber(BigInteger value, int radix) {
        return add(new IntegerValue(value, radix));
    }

    /**
     * Method adds to the end of JSON array {@code double} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see DecimalValue#DecimalValue(double)
     */
    public JsonArrayBuilder addNumber(double value) {
        return add(new DecimalValue(value));
    }

    /**
     * Method adds to the end of JSON array {@code BigDecimal} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see DecimalValue#DecimalValue(BigDecimal)
     */
    public JsonArrayBuilder addNumber(BigDecimal value) {
        return add(new DecimalValue(value));
    }

    /**
     * Method adds to the end of JSON array {@code NaN} element. Be careful!
     * JSON does not support this feature, so method {@link JsonElement#toJsonString()}
     * will throw an exception.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see NaNValue
     * @see JsonElement#toJsonString()
     */
    public JsonArrayBuilder addNaN() {
        return add(new NaNValue());
    }

    /**
     * Method adds to the end of JSON array {@code Infinity} element. Be careful!
     * JSON does not support this feature, so method {@link JsonElement#toJsonString()}
     * will throw an exception.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see InfinityValue#InfinityValue()
     * @see JsonElement#toJsonString()
     */
    public JsonArrayBuilder addInfinity() {
        return add(new InfinityValue());
    }

    /**
     * Method adds to the end of JSON array {@code Infinity} element. <strong>Be careful!</strong>
     * JSON does not support this feature, so method {@link JsonElement#toJsonString()}
     * will throw an exception.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see InfinityValue#InfinityValue(boolean)
     * @see JsonElement#toJsonString()
     */
    public JsonArrayBuilder addInfinity(boolean negative) {
        return add(new InfinityValue(negative));
    }

    /**
     * Method adds to the end of JSON array {@code String} element.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see StringValue#StringValue(String)
     */
    public JsonArrayBuilder addString(String value) {
        return add(new StringValue(value));
    }

    /**
     * Method adds to the end of JSON array clone of {@code JsonArrayBuilder} element.
     *
     * @return this builder
     * @throws NullPointerException if builder is null
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder addArray(JsonArrayBuilder builder) {
        return add(builder.value.clone());
    }

    /**
     * Method adds to the end of JSON array clone of {@code JsonObjectBuilder} element.
     *
     * @return this builder
     * @throws NullPointerException if builder is null
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see JsonObjectBuilder
     */
    public JsonArrayBuilder addObject(JsonObjectBuilder builder) {
        return add(builder.value.clone());
    }

    /**
     * Method adds to the end of JSON array JSON element. If element is null,
     * it will be skipped without exception.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder add(JsonElement element) {
        checkBuilt();
        if (element == null)
            throw new NullPointerException("null element");
        if (!element.getClass().getPackage().getName().equals(PACKAGE_NAME))
            throw new IllegalArgumentException("element class is not supported");
        value.values.add(element.copy());
        return this;
    }

    /**
     * Method adds all elements from given structure to this JSON array. If given structure is
     * object, this method will add only values without keys.
     *
     * @return this builder
     */
    public JsonArrayBuilder addValues(JsonStructure structure) {
        checkBuilt();
        if (structure instanceof JsonArray) {
            value.values.addAll(((JsonArray) structure));
        } else if (structure instanceof JsonObject) {
            value.values.addAll(((JsonObject) structure).values());
        }
        return this;
    }

    /**
     * Method removes element at given index from JSON array.
     *
     * @param index index of element to be removed
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @throws IndexOutOfBoundsException if index is incorrect
     */
    public JsonArrayBuilder remove(int index) {
        checkBuilt();
        value.values.remove(index);
        return this;
    }

    /**
     * Method removes first {@code null} element from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder removeNull() {
        return remove(new NullValue());
    }

    /**
     * Method removes first {@code boolean} element with given value from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see BooleanValue#BooleanValue(boolean)
     */
    public JsonArrayBuilder removeBoolean(boolean value) {
        return remove(new BooleanValue(value));
    }

    /**
     * Method removes first {@code long} element with given value from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see IntegerValue#IntegerValue(long)
     */
    public JsonArrayBuilder removeNumber(long value) {
        return remove(new IntegerValue(value));
    }

    /**
     * Method removes first {@code BigInteger} element with given value from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see IntegerValue#IntegerValue(BigInteger)
     */
    public JsonArrayBuilder removeNumber(BigInteger value) {
        return remove(new IntegerValue(value));
    }

    /**
     * Method removes first {@code double} element with given value from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see DecimalValue#DecimalValue(double)
     */
    public JsonArrayBuilder removeNumber(double value) {
        return remove(new DecimalValue(value));
    }

    /**
     * Method removes first {@code BigDecimal} element with given value from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see DecimalValue#DecimalValue(BigDecimal)
     */
    public JsonArrayBuilder removeNumber(BigDecimal value) {
        return remove(new DecimalValue(value));
    }

    /**
     * Method removes first {@code String} element with given value from JSON array.
     *
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     * @see StringValue#StringValue(String)
     */
    public JsonArrayBuilder removeString(String value) {
        return remove(new StringValue(value));
    }

    /**
     * Method removes first {@code JsonArray} element from JsonArrayBuilder with given
     * value from JSON array.
     *
     * @return this builder
     * @throws NullPointerException if builder is null
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder removeArray(JsonArrayBuilder builder) {
        return remove(builder.value);
    }

    /**
     * Method removes first {@code JsonObject} element from JsonArrayBuilder with given
     * value from JSON array.
     *
     * @return this builder
     * @throws NullPointerException if builder is null
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder removeObject(JsonObjectBuilder builder) {
        return remove(builder.value);
    }

    /**
     * Method removes given element from JSON array.
     *
     * @param element element to be removed
     * @return this builder
     * @throws IllegalStateException if method {@link #build(boolean)} was invoked before
     */
    public JsonArrayBuilder remove(JsonElement element) {
        checkBuilt();
        if (element == null) return this;
        if (element.isIndexed()) {
            if (element instanceof JsonArray)
                ((JsonArray) element).resolvePath(JsonPath.empty());
            else if (element instanceof JsonObject)
                ((JsonObject) element).resolvePath(JsonPath.empty());
            else if (element.getPath() instanceof JsonPath.BuildablePath)
                ((JsonPath.BuildablePath) element.getPath()).clear();
        }
        value.values.remove(element);
        return this;
    }

    @Override
    public int size() {
        return value.values.size();
    }

    @Override
    public JsonArray build() {
        return build(false);
    }

    @Override
    public JsonArray build(boolean setPath) {
        if (built) return value;
        if (setPath) value.resolvePath(JsonPath.empty());
        built = true;
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonArrayBuilder that = (JsonArrayBuilder) o;

        if (built != that.built) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Method is static constructor for this class. It creates empty object.
     *
     * @return instance of {@code JsonArrayBuilder}
     */
    public static JsonArrayBuilder create() {
        return new JsonArrayBuilder();
    }

    /**
     * Method is static constructor for this class. It creates object with
     * specified JSON array base value. Given object will not be modified.
     * @param instance JSON array that will be base of new object
     *
     * @return instance of {@code JsonArrayBuilder}
     */
    public static JsonArrayBuilder create(JsonArray instance) {
        JsonArrayBuilder builder = new JsonArrayBuilder();
        builder.value = instance.clone();
        return builder;
    }
}
