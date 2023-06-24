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
 * Class that helps to create JSON objects. Majority of methods returns
 * current object, so structure can be built using sequence of
 * methods add(...). To finish constructing, method {@link #build()}
 * must be used. Default constructor is private so to create an instance
 * of this class methods {@link #create()} and {@link #create(JsonObject)}
 * should be used.
 * @see #build()
 * @see #create()
 * @see #create(JsonObject)
 */
public class JsonObjectBuilder implements StructureBuilder {
    /**
     * Package name of this class.
     */
    private static final String PACKAGE_NAME = JsonObjectBuilder.class.getPackage().getName();

    /**
     * Field that contains constructing instance of JsonObject.
     */
    JsonObject value;
    /**
     * Field that shows whether method build() was invoked.
     */
    private boolean built;

    /**
     * Private constructor that creates empty object.
     */
    private JsonObjectBuilder() {
        value = new JsonObject();
    }

    /**
     * Method that checks whether field {@link #built} is {@code true} and
     * throws an exception.
     * @throws IllegalStateException if field {@link #built} is {@code true}
     */
    private void checkBuilt() {
        if (built) throw new IllegalStateException("object is already built");
    }

    /**
     * Method puts {@code null} to the JSON object and associate it with given key.
     * @param key key with which the {@code null} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if key is null
     */
    public JsonObjectBuilder putNull(String key) {
        return put(key, new NullValue());
    }

    /**
     * Method puts {@code boolean} value to the JSON object and associate it with given key.
     * @param key key with which the {@code boolean} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if key is null
     * @see BooleanValue
     */
    public JsonObjectBuilder putBoolean(String key, boolean value) {
        return put(key, new BooleanValue(value));
    }

    /**
     * Method puts {@code long} value to the JSON object and associate it with given key.
     * @param key key with which the {@code long} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if key is null
     * @see IntegerValue#IntegerValue(long)
     */
    public JsonObjectBuilder putNumber(String key, long value) {
        return put(key, new IntegerValue(value));
    }

    /**
     * Method puts {@code long} value with radix to the JSON object and associate it with given key.
     * @param key key with which the {@code long} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if key is null
     * @see IntegerValue#IntegerValue(long, int)
     */
    public JsonObjectBuilder putNumber(String key, long value, int radix) {
        return put(key, new IntegerValue(value, radix));
    }

    /**
     * Method puts {@code BigInteger} value to the JSON object and associate it with given key.
     * @param key key with which the {@code BigInteger} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     * @see IntegerValue#IntegerValue(BigInteger)
     */
    public JsonObjectBuilder putNumber(String key, BigInteger value) {
        return put(key, new IntegerValue(value));
    }

    /**
     * Method puts {@code BigInteger} value with radix to the JSON object and associate it with given key.
     * @param key key with which the {@code BigInteger} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     * @see IntegerValue#IntegerValue(BigInteger, int)
     */
    public JsonObjectBuilder putNumber(String key, BigInteger value, int radix) {
        return put(key, new IntegerValue(value, radix));
    }

    /**
     * Method puts {@code double} value to the JSON object and associate it with given key.
     * @param key key with which the {@code double} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if key is null
     * @see DecimalValue#DecimalValue(double)
     */
    public JsonObjectBuilder putNumber(String key, double value) {
        return put(key, new DecimalValue(value));
    }

    /**
     * Method puts {@code BigDecimal} value to the JSON object and associate it with given key.
     * @param key key with which the {@code BigDecimal} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     * @see DecimalValue#DecimalValue(BigDecimal)
     */
    public JsonObjectBuilder putNumber(String key, BigDecimal value) {
        return put(key, new DecimalValue(value));
    }

    /**
     * Method puts {@code String} value to the JSON object and associate it with given key.
     * @param key key with which the {@code String} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     * @see StringValue#StringValue(String)
     */
    public JsonObjectBuilder putString(String key, String value) {
        return put(key, new StringValue(value));
    }

    /**
     * Method puts {@code JsonArray} from {@link JsonArrayBuilder} to the JSON object and
     * associate it with given key.
     * @param key key with which the {@code JsonArray} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     * @see JsonArrayBuilder
     */
    public JsonObjectBuilder putArray(String key, JsonArrayBuilder builder) {
        return put(key, builder.value.clone());
    }

    /**
     * Method puts {@code JsonObject} from {@link JsonObjectBuilder} to the JSON object and
     * associate it with given key.
     * @param key key with which the {@code JsonObject} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     * @see JsonObjectBuilder
     */
    public JsonObjectBuilder putObject(String key, JsonObjectBuilder builder) {
        return put(key, builder.value.clone());
    }

    /**
     * Method puts {@code JsonElement} to the JSON object and associate it with given key.
     * @param key key with which the {@code JsonElement} is to be associated
     *
     * @return this builder
     * @throws NullPointerException if any argument is null
     */
    public JsonObjectBuilder put(String key, JsonElement element) {
        checkBuilt();
        if (element == null)
            throw new NullPointerException("null element");
        if (!element.getClass().getPackage().getName().equals(PACKAGE_NAME))
            throw new IllegalArgumentException("element class is not supported");
        value.values.put(key, element.copy());
        return this;
    }

    /**
     * Removes the mapping for this key from JSON object if present.
     * @param key key for which mapping should be removed
     *
     * @return this builder
     */
    public JsonObjectBuilder remove(String key) {
        checkBuilt();
        if (key == null) return this;
        JsonElement element = value.values.remove(key);
        if (element != null && element.isIndexed()) {
            if (element instanceof JsonArray)
                ((JsonArray) element).resolvePath(JsonPath.empty());
            else if (element instanceof JsonObject)
                ((JsonObject) element).resolvePath(JsonPath.empty());
            else if (element.getPath() instanceof JsonPath.BuildablePath)
                ((JsonPath.BuildablePath) element.getPath()).clear();
        }
        return this;
    }

    @Override
    public int size() {
        return value.values.size();
    }

    @Override
    public JsonObject build() {
        return build(false);
    }

    @Override
    public JsonObject build(boolean setPath) {
        if (built) return value;
        if (setPath) value.resolvePath(JsonPath.empty());
        built = true;
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonObjectBuilder that = (JsonObjectBuilder) o;

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
     * @return instance of {@code JsonObjectBuilder}
     */
    public static JsonObjectBuilder create() {
        return new JsonObjectBuilder();
    }

    /**
     * Method is static constructor for this class. It creates object with
     * specified JSON object base value. Given object will not be modified.
     * @param instance JSON object that will be base of new object
     *
     * @return instance of {@code JsonObjectBuilder}
     */
    public static JsonObjectBuilder create(JsonObject instance) {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        builder.value = instance.clone();
        return builder;
    }
}
