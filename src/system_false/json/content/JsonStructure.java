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

import java.util.NoSuchElementException;

/**
 * Common class for all JSON structures such are object and array.<br />
 * All these types can contain any json element.
 */
public interface JsonStructure extends JsonElement {
    /**
     * Method converts this structure to JSON with given indent.<br />
     * It is used to make JSON view structured and easy to understand.
     *
     * @param indent indent with left side of text
     * @return JSON representation of this object
     */
    String toJsonString(int indent);

    /**
     * Method converts this structure to JSON5 with given indent.<br />
     * It is used to make JSON view structured and easy to understand.
     * Single difference with method {@link #toJsonString(int)} is that
     * this method use {@link JsonElement#toJson5String()} methods for
     * elements instead of {@link JsonElement#toJsonString()}
     *
     * @param indent indent with left side of text
     * @return JSON5 representation of this object
     * @see JsonElement#toJson5String()
     * @see JsonElement#toJsonString()
     */
    default String toJson5String(int indent) {
        return toJsonString(indent);
    }

    /**
     * Method returns count of inner {@code JsonElement} values.
     *
     * @return count of inner {@code JsonElement} values
     */
    int size();

    /**
     * Method returns this object to formatted JSON string.<br />
     * This can be replaced with {@code obj.toJsonString(0)}.
     *
     * @return formatted JSON representation of this object
     * @see #toJsonString(int)
     */
    default String toFormattedJson() {
        return toJsonString(0);
    }

    /**
     * Method returns this object to formatted JSON5 string.<br />
     * This can be replaced with {@code obj.toJson5String(0)}.
     *
     * @return formatted JSON5 representation of this object
     * @see #toJson5String(int)
     */
    default String toFormattedJson5() {
        return toJson5String(0);
    }

    default JsonElement findElement(String path) {
        return findElement(JsonPath.compile(path));
    }

    default JsonElement findElement(JsonPath path) {
        return path.get(this);
    }

    default JsonValue findValue(String path) {
        return findValue(JsonPath.compile(path));
    }

    default JsonValue findValue(JsonPath path) {
        JsonElement element = path.get(this);
        if (element instanceof JsonValue) return (JsonValue) element;
        else throw new NoSuchElementException("incorrect end element type");
    }

    default JsonStructure findStructure(String path) {
        return findStructure(JsonPath.compile(path));
    }

    default JsonStructure findStructure(JsonPath path) {
        JsonElement element = path.get(this);
        if (element instanceof JsonStructure) return (JsonStructure) element;
        else throw new NoSuchElementException("incorrect end element type");
    }
}
