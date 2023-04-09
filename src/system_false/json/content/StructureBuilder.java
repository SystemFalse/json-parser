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
 * Interface for {@link JsonArrayBuilder} and {@link JsonObjectBuilder}.
 * It contains only method common for both class.
 */
public interface StructureBuilder {
    /**
     * Method returns current count of elements added to building structure.
     *
     * @return count of added elements
     */
    int size();

    /**
     * Method build JSON structure and returns it. After invoking this method
     * any other methods that modify structure will be not available.
     * @return built JSON structure
     */
    JsonStructure build();
}
