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

import java.util.*;

public class PathBuilder {
    final LinkedList<Filter> filters = new LinkedList<>();
    boolean deepScan = false;
    boolean indefinite = false;

    private PathBuilder() {}

    public PathBuilder deepScan() {
        deepScan = true;
        indefinite = true;
        return this;
    }

    public PathBuilder name(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (deepScan) {
            filters.add(new DeepFilter(new NameFilter(name, true)));
        } else {
            filters.add(new NameFilter(name, false));
        }
        deepScan = false;
        return this;
    }

    public PathBuilder names(String... names) {
        for (int i = 0; i < names.length; i++) {
            if (names[i] == null) {
                throw new NullPointerException("null name at " + i);
            }
        }
        if (deepScan) {
            filters.add(new DeepFilter(new NamesFilter(Set.of(names), true)));
        } else {
            filters.add(new NamesFilter(Set.of(names), false));
        }
        deepScan = false;
        indefinite = true;
        return this;
    }

    public PathBuilder index(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("negative index");
        }
        if (deepScan) {
            filters.add(new DeepFilter(new IndexFilter(index, true)));
        } else {
            filters.add(new IndexFilter(index, false));
        }
        deepScan = false;
        return this;
    }

    public PathBuilder indexes(int... indexes) {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] < 0) {
                throw new IllegalArgumentException("negative index at " + i);
            }
        }
        if (deepScan) {
            filters.add(new DeepFilter(new IndexesFilter(Arrays.stream(indexes).collect(HashSet::new, HashSet::add,
                    AbstractCollection::addAll), deepScan)));
        } else {
            filters.add(new IndexesFilter(Arrays.stream(indexes).collect(HashSet::new, HashSet::add,
                    AbstractCollection::addAll), deepScan));
        }
        deepScan = false;
        indefinite = true;
        return this;
    }

    public PathBuilder sliceFrom(int from) {
        return slice(from, -1);
    }

    public PathBuilder sliceTo(int to) {
        return slice(0, to);
    }

    public PathBuilder slice(int from, int to) {
        if (from >= 0 && to >= 0) {
            if (to > from) {
                throw new IllegalArgumentException("from index is bigger than to index");
            }
        } else if (from < 0 && to < 0) {
            if (to < from) {
                throw new IllegalArgumentException("from index is bigger than to index");
            }
        }
        if (deepScan) {
            filters.add(new DeepFilter(new SliceFilter(from, to, true)));
        } else {
            filters.add(new SliceFilter(from, to, false));
        }
        deepScan = false;
        indefinite = true;
        return this;
    }

    public PathBuilder expression(Expression exp) {
        if (exp == null) {
            throw new NullPointerException("expression");
        }
        if (deepScan) {
            filters.add(new DeepFilter(new ExpressionFilter(exp, true)));
        } else {
            filters.add(new ExpressionFilter(exp, false));
        }
        deepScan = false;
        indefinite = true;
        return this;
    }

    public PathBuilder wildcard() {
        if (deepScan) {
            filters.add(new DeepFilter(new WildcardFilter(true)));
        } else {
            filters.add(new WildcardFilter(false));
        }
        deepScan = false;
        indefinite = true;
        return this;
    }

    public JsonPath build() {
        return new JsonPath(filters, true);
    }

    public static PathBuilder builder() {
        return new PathBuilder();
    }
}
