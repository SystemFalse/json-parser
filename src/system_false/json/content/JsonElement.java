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
 * Common class for all json components.<br />
 * It provides basic methods that can be used by any json element.
 */
public interface JsonElement extends Cloneable {
    /**
     * Method converts this json element to JSON string. <strong>Be careful!</strong>
     * If current element was created by parsing JSON5 string, some features will be not
     * supported by JSON.
     * @return JSON representation of this object
     */
    String toJsonString();

    /**
     * Method converts this json element toJSON5 string. Main differences with method
     * {@link #toJsonString()} are that this method will return JSON strings with '' brackets,
     * numbers with set radix, NaN and Infinity.
     * @return JSON5 representation of this object
     */
    default String toJson5String() {
        return toJsonString();
    }

    /**
     * Method converts this json element to JSON2 string. Read documentation on
     * <a href="https://github.com/vi/json2">GitHub page</a>.
     *
     * @return JSON2 representation of this object
     */
    default String toJson2String() {
        return toJson2String("");
    }

    /**
     * Method converts this json element to JSON2 string with set path. Read documentation
     * on <a href="https://github.com/vi/json2">GitHub page</a>.
     *
     * @param path path of this object
     * @return path + "=" + {JSON2 view}
     */
    default String toJson2String(String path) {
        throw new UnsupportedOperationException("not supported yet");
    }

    /**
     * Method returns copy of this object.
     * @apiNote this method should return clone()
     *
     * @return independent copy of this object
     */
    JsonElement copy();

    /**
     * Method returns string representation of this object. As object is
     * instance of {@code JsonElement}, this method will return result of
     * method {@link #toJsonString()}.
     * @return JSON representation of this object
     */
    String toString();

    /**
     * Method returns specified JSON path of this element. By default, it returns empty path.
     *
     * @return path of this element
     * @see JsonPath
     */
    default JsonPath getPath() {
        return JsonPath.empty();
    }

    /**
     * Method return whether this element is indexed or not. Element is indexed when path is not empty.
     *
     * @return {@code true} if this element is indexed, {@code false} - otherwise
     * @see #getPath()
     * @see JsonPath
     */
    default boolean isIndexed() {
        return !getPath().isEmpty();
    }
}
