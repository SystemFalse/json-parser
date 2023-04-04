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

package system_false.json.parser;

/**
 * Common exception for JSON Parser. If it was occurred this means that JSON source
 * contains syntax mistake or any other exceptions occurred during parsing.
 */
public class ParserException extends RuntimeException {
    /**
     * Constructor that creates an object with given message and cause.
     * @param message message to show
     * @param cause exception that was the cause of this exception
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor that creates an object with given message.
     * @param message message to show
     */
    public ParserException(String message) {
        super(message);
    }
}
