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
package org.uorm.orm.bytecode;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Fast access to class information
 *
 * @author Muga Nishizawa
 */
public class FastClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8620546530431740340L;

	private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

	private final Class type;

	/**
	 * Constructs a FastClass
	 *
	 * @param type The class to optimize
	 *
	 * @return The fast class access to the given class
	 */
	public static FastClass create(Class type) {
		return new FastClass( type );
	}

	private FastClass(Class type) {
		this.type = type;
	}

	/**
	 * Access to invoke a method on the class that this fast class handles
	 *
	 * @param name The name of the method to invoke,
	 * @param parameterTypes The method parameter types
	 * @param obj The instance on which to invoke the method
	 * @param args The parameter arguments
	 *
	 * @return The method result
	 *
	 * @throws InvocationTargetException Indicates a problem performing invocation
	 */
	public Object invoke(
			String name,
			Class[] parameterTypes,
			Object obj,
			Object[] args) throws InvocationTargetException {
		return this.invoke( this.getIndex( name, parameterTypes ), obj, args );
	}

	/**
	 * Access to invoke a method on the class that this fast class handles by its index
	 *
	 * @param index The method index
	 * @param obj The instance on which to invoke the method
	 * @param args The parameter arguments
	 *
	 * @return The method result
	 *
	 * @throws InvocationTargetException Indicates a problem performing invocation
	 */
	public Object invoke(
			int index,
			Object obj,
			Object[] args) throws InvocationTargetException {
		final Method[] methods = this.type.getMethods();
		try {
			return methods[index].invoke( obj, args );
		}
		catch ( ArrayIndexOutOfBoundsException e ) {
			throw new IllegalArgumentException(
					"Cannot find matching method/constructor"
			);
		}
		catch ( IllegalAccessException e ) {
			throw new InvocationTargetException( e );
		}
	}

	/**
	 * Invoke the default constructor
	 *
	 * @return The constructed instance
	 *
	 * @throws InvocationTargetException Indicates a problem performing invocation
	 */
	public Object newInstance() throws InvocationTargetException {
		return this.newInstance( this.getIndex( EMPTY_CLASS_ARRAY ), null );
	}

	/**
	 * Invoke a parameterized constructor
	 *
	 * @param parameterTypes The parameter types
	 * @param args The parameter arguments to pass along
	 *
	 * @return The constructed instance
	 *
	 * @throws InvocationTargetException Indicates a problem performing invocation
	 */
	public Object newInstance(
			Class[] parameterTypes,
			Object[] args) throws InvocationTargetException {
		return this.newInstance( this.getIndex( parameterTypes ), args );
	}

	/**
	 * Invoke a constructor by its index
	 *
	 * @param index The constructor index
	 * @param args The parameter arguments to pass along
	 *
	 * @return The constructed instance
	 *
	 * @throws InvocationTargetException Indicates a problem performing invocation
	 */
	public Object newInstance(
			int index,
			Object[] args) throws InvocationTargetException {
		final Constructor[] constructors = this.type.getConstructors();
		try {
			return constructors[index].newInstance( args );
		}
		catch ( ArrayIndexOutOfBoundsException e ) {
			throw new IllegalArgumentException( "Cannot find matching method/constructor" );
		}
		catch ( InstantiationException e ) {
			throw new InvocationTargetException( e );
		}
		catch ( IllegalAccessException e ) {
			throw new InvocationTargetException( e );
		}
	}

	/**
	 * Locate the index of a method
	 *
	 * @param name The method name
	 * @param parameterTypes The method parameter types
	 *
	 * @return The index
	 */
	public int getIndex(String name, Class[] parameterTypes) {
		final Method[] methods = this.type.getMethods();
		boolean eq;

		for ( int i = 0; i < methods.length; ++i ) {
			if ( !Modifier.isPublic( methods[i].getModifiers() ) ) {
				continue;
			}
			if ( !methods[i].getName().equals( name ) ) {
				continue;
			}
			final Class[] params = methods[i].getParameterTypes();
			if ( params.length != parameterTypes.length ) {
				continue;
			}
			eq = true;
			for ( int j = 0; j < params.length; ++j ) {
				if ( !params[j].equals( parameterTypes[j] ) ) {
					eq = false;
					break;
				}
			}
			if ( eq ) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Locate the index of a constructor
	 *
	 * @param parameterTypes The constructor parameter types
	 *
	 * @return The index
	 */
	public int getIndex(Class[] parameterTypes) {
		final Constructor[] constructors = this.type.getConstructors();
		boolean eq;

		for ( int i = 0; i < constructors.length; ++i ) {
			if ( !Modifier.isPublic( constructors[i].getModifiers() ) ) {
				continue;
			}
			final Class[] params = constructors[i].getParameterTypes();
			if ( params.length != parameterTypes.length ) {
				continue;
			}
			eq = true;
			for ( int j = 0; j < params.length; ++j ) {
				if ( !params[j].equals( parameterTypes[j] ) ) {
					eq = false;
					break;
				}
			}
			if ( eq ) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the wrapped class name
	 *
	 * @return The class name
	 */
	public String getName() {
		return this.type.getName();
	}

	/**
	 * Get the wrapped java class reference
	 *
	 * @return The class reference
	 */
	public Class getJavaClass() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.type.toString();
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof FastClass
				&& this.type.equals( ((FastClass) o).type );
	}
}