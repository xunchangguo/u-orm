/**
 * Copyright 2010-2016 the original author or authors.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uorm.orm.bytecode.javassist;

/**
 * An exception thrown while generating a bulk accessor.
 * 
 * @author Muga Nishizawa
 * @author modified by Shigeru Chiba
 */
public class BulkAccessorException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2316833124741308749L;
	
	private Throwable myCause;

    /**
     * Gets the cause of this throwable.
     * It is for JDK 1.3 compatibility.
     */
    public Throwable getCause() {
        return (myCause == this ? null : myCause);
    }

    /**
     * Initializes the cause of this throwable.
     * It is for JDK 1.3 compatibility.
     */
    public synchronized Throwable initCause(Throwable cause) {
        myCause = cause;
        return this;
    }

    private int index;

    /**
     * Constructs an exception.
     */
    public BulkAccessorException(String message) {
        super(message);
        index = -1;
        initCause(null);
    }

    /**
     * Constructs an exception.
     *
     * @param index     the index of the property that causes an exception.
     */
    public BulkAccessorException(String message, int index) {
        this(message + ": " + index);
        this.index = index;
    }

    /**
     * Constructs an exception.
     */
    public BulkAccessorException(String message, Throwable cause) {
        super(message);
        index = -1;
        initCause(cause);
    }

    /**
     * Constructs an exception.
     *
     * @param index     the index of the property that causes an exception.
     */
    public BulkAccessorException(Throwable cause, int index) {
        this("Property " + index);
        this.index = index;
        initCause(cause);
    }

    /**
     * Returns the index of the property that causes this exception.
     *
     * @return -1 if the index is not specified.
     */
    public int getIndex() {
        return this.index;
    }
}
