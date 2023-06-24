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

import org.system_false.json.content.JsonArray;
import org.system_false.json.content.JsonElement;
import org.system_false.json.content.JsonStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("RegExpRedundantEscape")
public class JsonPath {
    static final Pattern DOTS = Pattern.compile("\\.{3,}");
    static final Pattern DOT_NOTATED_CHILD =
            Pattern.compile("\\.(?<child>[^\\v$\\[\\]]+|\\*)");
    static final Pattern BRACKET_NOTATED_CHILD = Pattern.compile("\\['?\\V'?(,\\s*'?\\V'?)*\\]");
    static final Pattern ARRAY_INDEXES =
            Pattern.compile("\\[(?<index>0|[1-9]\\d*)(,\\s*(?:0|[1-9]\\d*))*\\]");
    static final Pattern ARRAY_SLICE =
            Pattern.compile("\\[(?<start>0|-?[1-9]\\d*):(?<end>0|-?[1-9]\\d*)\\]");
    static final Pattern FILTER_EXPRESSION = Pattern.compile("\\[\\?\\((?<expression>.+)\\)\\]");

    private final String path;
    private final boolean subPath;
    private final LinkedList<Filter> filters;

    private boolean indefinite;

    private JsonPath(String path, boolean subPath) {
        this.path = path;
        this.subPath = subPath;
        filters = new LinkedList<>();
        compile();
    }

    JsonPath(Collection<Filter> filters, boolean indefinite) {
        path = null;
        subPath = false;
        this.filters = new LinkedList<>(filters);
        this.indefinite = indefinite;
    }

    private void compile() {
        if (path.charAt(0) != '$') {
            if (!subPath)
                throw new PathException("illegal path start: " + path.charAt(0));
            else if (path.charAt(0) != '@') {
                throw new PathException("illegal path start: " + path.charAt(0));
            }
        }
        StringBuilder sb = new StringBuilder(DOTS.matcher(path).replaceAll(".."));

        Matcher m = DOT_NOTATED_CHILD.matcher(sb);
        int end = 0;
        while (m.find(end)) {
            String replacement = '[' + m.group("child") + ']';
            sb.replace(m.start(), m.end(), replacement);
            end = m.end();
        }

        boolean deep = false, openBracket = false;
        for (int i = 1; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '.') {
                if (path.charAt(i - 1) == '.') {
                    deep = true;
                }
            } else if (c == '[') {
                if (openBracket) {
                    throw new PathException("unexpected open bracket at " + (i + 1));
                }
                openBracket = true;
            }
        }
    }

    public String path() {
        return path;
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public Optional<JsonElement> eval(JsonStructure root) {
        ArrayList<JsonElement> elements = new ArrayList<>();
        elements.add(root);
        for (Filter f : filters) {
            if (elements.isEmpty()) {
                break;
            }
            f.filter(root, elements);
        }
        if (indefinite) {
            return Optional.of(new JsonArray(elements));
        } else if (!elements.isEmpty()) {
            return Optional.of(elements.get(0));
        } else {
            return Optional.empty();
        }
    }

    public static JsonPath compile(String path) {
        if (path == null) {
            throw new PathException("null path");
        }
        if (path.isEmpty()) {
            throw new PathException("empty path");
        }
        return new JsonPath(path, false);
    }
}
