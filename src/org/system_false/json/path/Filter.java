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

import org.system_false.json.content.*;

import java.util.*;
import java.util.function.IntFunction;

public abstract class Filter {
    final boolean deep;

    public Filter(boolean deep) {
        this.deep = deep;
    }

    public final Optional<JsonElement> filter(JsonElement root, JsonElement current) {
        ArrayList<JsonElement> list = new ArrayList<>(List.of(current));
        filter(root, list);
        if (list.isEmpty()) {
            return Optional.empty();
        } else if (list.size() == 1) {
            return Optional.of(list.get(0));
        } else {
            return Optional.of(new JsonArray(list));
        }
    }

    protected abstract void filter(JsonElement root, List<JsonElement> elements);
}

class NameFilter extends Filter {
    final String name;

    NameFilter(String name, boolean deep) {
        super(deep);
        this.name = name;
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement jo = elements.remove(i);
            if (jo instanceof JsonObject) {
                for (var entry : ((JsonObject) jo).entrySet()) {
                    if (name.equals(entry.getKey())) {
                        elements.add(i++, entry.getValue());
                        break;
                    }
                }
            }
        }
    }
}

class NamesFilter extends Filter {
    final String[] names;

    public NamesFilter(Set<String> names, boolean deep) {
        super(deep);
        this.names = names.stream().sorted().toArray(String[]::new);
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement jo = elements.remove(i);
            if (jo instanceof JsonObject) {
                int found = 0;
                for (var entry : ((JsonObject) jo).entrySet()) {
                    if (Arrays.binarySearch(names, entry.getKey()) >= 0) {
                        elements.add(i++, entry.getValue());
                        if (++found == names.length) break;
                    }
                }
            }
        }
    }
}

class IndexFilter extends Filter {
    final int index;

    public IndexFilter(int index, boolean deep) {
        super(deep);
        this.index = index;
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement ja = elements.remove(i);
            if (ja instanceof JsonArray && index < ((JsonArray) ja).size()) {
                elements.add(i++, ((JsonArray) ja).get(index));
            }
        }
    }
}

class IndexesFilter extends Filter {
    final int[] indexes;

    public IndexesFilter(Set<Integer> indexes, boolean deep) {
        super(deep);
        this.indexes = indexes.stream().mapToInt(i -> i).sorted().toArray();
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement ja = elements.remove(i);
            if (ja instanceof JsonArray) {
                for (int index : indexes) {
                    if (index >= ((JsonArray) ja).size()) {
                        break;
                    }
                    elements.add(i++, ((JsonArray) ja).get(index));
                }
            }
        }
    }
}

class SliceFilter extends Filter {
    final int start, end;

    public SliceFilter(int start, int end, boolean deep) {
        super(deep);
        this.start = start;
        this.end = end;
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement ja = elements.remove(i);
            if (ja instanceof JsonArray) {
                JsonArray arr = (JsonArray) ja;
                int start = this.start >= 0 ? this.start : arr.size() + this.start;
                int end = this.end >= 0 ? this.end : arr.size() + this.end;
                if (start < 0 || end < 0) continue;
                for (int j = start; j <= end; j++) {
                    elements.add(i++, arr.get(j));
                }
            }
        }
    }
}

class ExpressionFilter extends Filter {
    final Expression expression;

    public ExpressionFilter(Expression expression, boolean deep) {
        super(deep);
        this.expression = expression;
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement ja = elements.remove(i);
            if (ja instanceof JsonArray) {
                for (JsonElement je : (JsonArray) ja) {
                    if (expression.test(root, je)) {
                        elements.add(i++, je);
                    }
                }
            }
        }
    }
}

class WildcardFilter extends Filter {
    WildcardFilter(boolean deep) {
        super(deep);
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        for (int i = 0; i < elements.size();) {
            JsonElement js = elements.remove(i);
            if (js instanceof JsonArray) {
                for (JsonElement je : (JsonArray) js) {
                    elements.add(i++, je);
                }
            } else if (js instanceof JsonObject) {
                for (JsonElement je : ((JsonObject) js).values()) {
                    elements.add(i++, je);
                }
            }
        }
    }
}

class DeepFilter extends Filter {
    final Filter filter;

    public DeepFilter(Filter filter) {
        super(true);
        this.filter = filter;
    }

    @Override
    protected void filter(JsonElement root, List<JsonElement> elements) {
        ArrayList<JsonElement> copy = new ArrayList<>();
        ArrayList<JsonElement> filtered = new ArrayList<>();
        while (!elements.isEmpty()) {
            copy.addAll(elements);
            filter.filter(root, copy);
            filtered.addAll(copy);
            for (int i = 0; i < elements.size();) {
                JsonElement js = elements.remove(i);
                if (js instanceof JsonArray) {
                    for (JsonElement je : (JsonArray) js) {
                        elements.add(i++, je);
                    }
                } else if (js instanceof JsonObject) {
                    for (JsonElement je : ((JsonObject) js).values()) {
                        elements.add(i++, je);
                    }
                }
            }
            copy.clear();
        }
        elements.addAll(filtered);
    }
}
