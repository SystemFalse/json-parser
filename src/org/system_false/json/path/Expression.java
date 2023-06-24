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

package org.system_false.json.path;

import org.system_false.json.content.JsonElement;

@FunctionalInterface
public interface Expression {
    boolean test(JsonElement root, JsonElement current);

    default Expression not() {
        return (r, c) -> !test(r, c);
    }

    default Expression or(Expression exp) {
        return (r, c) -> test(r, c) || exp.test(r, c);
    }

    default Expression and(Expression exp) {
        return (r, c) -> test(r, c) && exp.test(r, c);
    }
}
