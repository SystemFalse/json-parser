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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Special view of path for JSON elements. This is similar to files system, but it has some
 * differences. Objects of this class is used for indexing JSON elements. Path construction:
 * <p>
 * Root element can be any {@code JsonStructure}, more specifically {@link JsonObject} and {@link JsonArray}.
 * To point to any element it is needed to write it's key, if root element is {@code JsonObject} or index
 * in [] brackets, if root element is {@code JsonArray}.<br />
 * For example, path "foo" will point to the element in {@code JsonObject}, named "foo".<br />
 * Path "[2] will point to the element at index 2 in {@code JsonArray}.
 * To point to next level object it is needed to add dot and to write key for next element.
 * To point to next level array dot should not be set.<br />
 * Two dots can not be placed to each other. It is incorrect path format. To point to empty named element,
 * "\0" should be used.
 * There are some restrictions for object names:
 * <ul>
 *     <li>name can not include brackets []</li>
 *     <li>name can not include dots</li>
 *     <li>name can not be \0, because of all this records will be replaced with empty strings</li>
 * </ul>
 *
 * Example of usage:
 * <pre>
 * There is this JSON text:
 * {
 *     "foo": {
 *         "bar": [
 *             56,
 *             90,
 *             36
 *         ],
 *         "foo": {
 *             "": 20
 *         }
 *     },
 *     "bar": [
 *         [
 *             88,
 *             69
 *         ],
 *         42
 *     ]
 * }
 * </pre>
 * Path "foo.bar[0]" will point to the element named "foo", then to the element named "bar" and
 * to the zero index element in the "bar". So, this path is pointing to 56.<br />
 * Path "bar[0]" will point to the element named "bar", then to the zero index element in "bar".
 * So, this path is pointing to [88, 69]<br />
 * Path "bar[0][1]" will point to the array like previous one and to the first index element in it.
 * So, this path is pointing to 69.<br />
 * Path "foo.foo.\0" will point to the element named "foo", then to the element named "foo" in
 * "foo", and then to the empty named element in second "foo". So, this path is pointing to 20.
 * </p>
 */
public class JsonPath implements Cloneable, Serializable {
    /**
     * This field is used in serialization.
     */
    private static final long serialVersionUID = -92387874838787L;

    /**
     * {@code String} view of {@code JsonPath}.
     */
    private String path;
    /**
     * List of functional converters.
     */
    private transient LinkedList<Resolver> resolvers;
    /**
     * Final functional converter.
     */
    private transient Resolver getter;

    /**
     * Private constructor that create {@code JsonPath} object with given path, resolvers and getter.
     *
     * @param path string view of {@code JsonPath}
     * @param resolvers list of resolvers
     * @param getter final resolver
     */
    private JsonPath(String path, LinkedList<Resolver> resolvers, Resolver getter) {
        this.path = path;
        this.resolvers = resolvers;
        this.getter = getter;
    }

    /**
     * Method returns {@code String} view of this path.
     *
     * @return {@code String} view of this path
     */
    public String getPath() {
        return path;
    }

    /**
     * Package-private method that finds element in given {@link JsonStructure}. If this path is empty,
     * any search with will return given structure.
     * @param structure root element for search
     *
     * @return found element
     * @throws NoSuchElementException if given structure does not contain any of path elements
     */
    JsonElement get(JsonStructure structure) {
        JsonElement element = structure;
        for (Resolver fn : resolvers) {
            try {
                element = fn.apply(element);
            } catch (RuntimeException e) {
                throw exception(fn, e.getMessage());
            }
        }
        try {
            return getter.apply(element);
        } catch (RuntimeException e) {
            throw exception(getter, e.getMessage());
        }
    }

    /**
     * Method returns whether this path is empty. If this path is empty, any search with it will
     * return given structure.
     *
     * @return {@code true} if this path is empty, {@code false} - otherwise
     */
    public boolean isEmpty() {
        return path.isEmpty();
    }

    /**
     * Method creates exception with message that contains path and element name.
     * @param res resolver in which an exception occurred
     * @param message common message
     *
     * @return new exception with specified message
     */
    private NoSuchElementException exception(Resolver res, String message) {
        return new NoSuchElementException("path \"" + res.path() + "\", element \"" + res.current() + "\": " + message);
    }

    @Override
    public JsonPath clone() {
        try {
            JsonPath clone = (JsonPath) super.clone();
            LinkedList<Resolver> resolvers = new LinkedList<>();
            clone.path = path.intern();
            clone.getter = compile0(path, resolvers);
            clone.resolvers = resolvers;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonPath path1 = (JsonPath) o;

        return path.equals(path1.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return path;
    }

    /**
     * Method writes this object to output stream during serialization.
     * @param stream output stream
     *
     * @throws IOException if any exception during serialization occurred
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeUTF(path);
    }

    /**
     * Method read object from input stream during serialization.
     * @param stream input stream
     *
     * @throws IOException if any exception during serialization occurred
     */
    private void readObject(ObjectInputStream stream) throws IOException {
        path = stream.readUTF();
        resolvers = new LinkedList<>();
        if (!path.isEmpty()) getter = compile0(path, resolvers);
        else getter = new EmptyPath();
    }

    /**
     * Method makes this object as empty path.
     */
    private void readObjectNoData() {
        path = "";
        resolvers = new LinkedList<>();
        getter = new EmptyPath();
    }

    /**
     * Interface that represents {@code JsonElement} resolver.
     */
    private interface Resolver extends Function<JsonElement, JsonElement> {
        /**
         * Method returns {@code String} path to this resolver.
         *
         * @return {@code String} path to this resolver
         */
        String path();

        /**
         * Method returns name of this resolver.
         *
         * @return name of this resolver
         */
        String current();
    }

    /**
     * Class that represents resolver that gets {@code JsonElement} from object.
     */
    private static class ObjectGet implements Resolver {
        /**
         * {@code String} view of path for this resolver.
         */
        final String path;
        /**
         * Name of this resolver.
         */
        final String name;

        /**
         * Constructor that creates resolver object using path and name.
         * @param path path to this resolver
         * @param name of this resolver
         */
        public ObjectGet(String path, String name) {
            this.path = path;
            this.name = name;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public String current() {
            return name;
        }

        @Override
        public JsonElement apply(JsonElement je) {
            if (!(je instanceof JsonObject)) {
                throw new RuntimeException("element is not object");
            }
            JsonObject jo = (JsonObject) je;
            return jo.get(name);
        }
    }

    /**
     * Class that represents resolver that gets {@code JsonElement} from array.
     */
    private static class ArrayGet implements Resolver {
        /**
         * {@code String} view of path for this resolver.
         */
        final String path;
        /**
         * Index of this resolver.
         */
        final int index;

        /**
         * Constructor that creates resolver object using path and index.
         * @param path path to this resolver
         * @param index of this resolver
         */
        public ArrayGet(String path, int index) {
            this.path = path;
            this.index = index;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public String current() {
            return "[" + index + "]";
        }

        @Override
        public JsonElement apply(JsonElement je) {
            if (!(je instanceof JsonArray)) {
                throw new RuntimeException("element is not array");
            }
            JsonArray ja = (JsonArray) je;
            return ja.get(index);
        }
    }

    /**
     * Class that represents resolver that returns given {@code JsonElement}.
     */
    private static class EmptyPath implements Resolver {
        @Override
        public String path() {
            return "";
        }

        @Override
        public String current() {
            return "";
        }

        @Override
        public JsonElement apply(JsonElement jsonElement) {
            return jsonElement;
        }
    }

    /**
     * Pattern that checks {@code JsonPath} string.
     */
    private static final Pattern correctPath = Pattern.compile("(?:(?:[^\\[\\]\\.]+|\\\\0)|\\[(?:0|[1-9][0-9]*)\\])(\\.(?:[^\\[\\]\\.]+|\\\\0)|\\[(?:0|[1-9][0-9]*)\\])*");
    /**
     * Pattern that finds object name and array index in the path.
     */
    private static final Pattern pathPattern = Pattern.compile("(?<name>(?<=\\A|\\.|\\])[^\\[\\]\\.]+|\\\\0(?=\\z|\\.|\\[))|(?<index>(?<=\\[)0|[1-9][0-9]*(?=\\]))");

    /**
     * Method that creates final resolver using {@code String} view.
     *
     * @param path {@code String} view of path
     * @param resolvers list of resolvers
     *
     * @return final resolver of given path
     */
    private static Resolver compile0(String path, LinkedList<Resolver> resolvers) {
        if (path.isEmpty()) throw new IllegalArgumentException("path can not be empty");
        if (!correctPath.matcher(path).matches()) throw new IllegalArgumentException("incorrect path");
        Matcher m = pathPattern.matcher(path);
        int begin = 0;
        while (m.find(begin)) {
            if (m.group("name") != null) {
                if (m.group("name").equals("\\0"))
                    resolvers.add(new ObjectGet(path.substring(0, m.start()), ""));
                else
                    resolvers.add(new ObjectGet(path.substring(0, m.start()), m.group("name")));
            } else if (m.group("index") != null) {
                resolvers.add(new ArrayGet(path.substring(0, m.start() - 1), Integer.parseInt(m.group("index"))));
            }
            begin = m.end();
        }
        return resolvers.removeLast();
    }

    /**
     * Method creates new {@code JsonPath} using given path.
     * @param path {@code String} view of path
     *
     * @return compiled {@code JsonPath}
     */
    public static JsonPath compile(String path) {
        LinkedList<Resolver> resolvers = new LinkedList<>();
        Resolver getter = compile0(path, resolvers);
        return new JsonPath(path, resolvers, getter);
    }

    /**
     * Method creates empty {@code JsonPath}. If this path will be used in method
     * {@link JsonStructure#findElement(JsonPath)}, it will return the same object.
     *
     * @return empty {@code JsonPath}
     */
    public static JsonPath empty() {
        return new JsonPath("", new LinkedList<>(), new EmptyPath());
    }

    /**
     * Method indexes given JSON structure, that is, it assigns a path to each element and sub-element.
     * To clear all paths, method {@link #clearPath(JsonElement)} should be used.
     * @param structure JSON structure to index
     *
     * @see #clearPath(JsonElement)
     */
    public static void indexElements(JsonStructure structure) {
        if (structure instanceof JsonArray) {
            ((JsonArray) structure).resolvePath(new BuildablePath());
        } else if (structure instanceof JsonObject) {
            ((JsonObject) structure).resolvePath(new BuildablePath());
        }
    }

    /**
     * Method clears all paths from all elements and sub-elements of given JSON element.
     * This method does the opposite of the method {@link #indexElements(JsonStructure)}.
     * @param element JSON element to clear paths
     *
     * @see #indexElements(JsonStructure)
     */
    public static void clearPath(JsonElement element) {
        if (element instanceof JsonArray) {
            ((JsonArray) element).clearPath();
        } else if (element instanceof JsonObject) {
            ((JsonObject) element).clearPath();
        } else if (element.getPath() instanceof JsonPath.BuildablePath) {
            ((BuildablePath) element.getPath()).clear();
        }
    }

    /**
     * Method checks given name whether it can be used in indexed element and throws an exception
     * if it can not be used that way.
     * @param name name of {@code JsonElement} to check
     *
     * @throws IllegalArgumentException if given name can not be used for indexed {@code JsonElement}
     */
    public static void checkName(String name) {
        if (name.equals("\\0")) throw new IllegalArgumentException();
        else if (name.contains("[") || name.contains("]") || name.contains(".")) throw new IllegalArgumentException();
    }

    /**
     * Class that represents modifiable {@link JsonPath}.
     */
    static class BuildablePath extends JsonPath {

        /**
         * Constructor that creates new {@code BuildablePath} object.
         */
        BuildablePath() {
            super("", new LinkedList<>(), new EmptyPath());
        }

        /**
         * Constructor that creates new {@code BuildablePath} object copying fields from
         * given {@code Jsonpath}. It is used in method {@link #clone()}.
         * @param copy {@code JsonPath} from which fields will be copied
         */
        private BuildablePath(JsonPath copy) {
            super(copy.path, copy.resolvers, copy.getter);
        }

        /**
         * Method adds object name to the end of this path. For example, if path was "foo",
         * after invoking method add("bar"), path will be "foo.bar".
         * @param objKey object key to add
         */
        void add(String objKey) {
            if (!(super.getter instanceof EmptyPath)) {
                super.resolvers.add(super.getter);
            }
            super.getter = new ObjectGet(super.path + ".", objKey);
            super.path += "." + objKey;
        }

        /**
         * Method add array index to the end of this path. For example, if path was "foo",
         * after invoking method add(1), path will be "foo[1]".
         * @param arrIndex array index to add
         */
        void add(int arrIndex) {
            if (!(super.getter instanceof EmptyPath)) {
                super.resolvers.add(super.getter);
            }
            super.getter = new ArrayGet(super.path, arrIndex);
            super.path += "[" + arrIndex + "]";
        }

        /**
         * Method sets array field like in given path.
         * @param path path which fields will be set to this object
         */
        void set(JsonPath path) {
            super.path = path.path;
            super.resolvers.clear();
            super.resolvers.addAll(path.resolvers);
            super.getter = path.getter;
        }

        /**
         * Method clears all information of path. {@link #path} will be empty,
         * {@link #resolvers} will be cleared, {@link #getter} will be set to {@link EmptyPath}.
         */
        void clear() {
            super.path = "";
            super.resolvers.clear();
            super.getter = new EmptyPath();
        }

        @Override
        public BuildablePath clone() {
            return new BuildablePath(super.clone());
        }
    }
}
