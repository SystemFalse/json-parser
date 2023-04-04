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
 * Common class for all JSON values such are null, boolean, number and string.<br />
 * All of these type can be converted to each other with rare exceptions. For
 * example, null will be converted to false, 0, and "null". Boolean value will
 * be true if number is not equal to 0 and true otherwise. It is important
 * to check for incorrect strings and special numbers before converting them
 * to number and boolean values.
 */
public interface JsonValue extends JsonElement {
    /**
     * Method returns whether this object originally was null value.
     *
     * @return whether this object originally was null value
     */
    boolean isNull();
    /**
     * Methods converts this object to boolean value.
     *
     * @return boolean representation of this object
     */
    boolean asBoolean();

    /**
     * Method returns whether this object originally was {@code boolean} value.
     *
     * @return whether this object originally was boolean value
     */
    boolean isBoolean();

    /**
     * Method converts this object to {@code NumberValue} value.
     *
     * @return {@code NumberValue} representation of this object
     * @see NumberValue
     */
    NumberValue asNumber();

    /**
     * Method returns whether this object originally was {@code NumberValue}.
     *
     * @return whether this object originally was {@code NumberValue} value
     * @see NumberValue
     */
    boolean isNumber();

    /**
     * Method converts this object to {@code String} value.
     *
     * @return {@code String} representation of this object
     */
    String asString();

    /**
     * Method returns whether this object originally was {@code String}.
     *
     * @return whether this object originally was {@code String} value
     */
    boolean isString();
}
