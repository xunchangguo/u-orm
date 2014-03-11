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

import java.io.Serializable;
import java.lang.reflect.Method;

import org.uorm.utils.PropertyHolderUtil;
import org.uorm.utils.SoftLimitMRUCache;
import org.uorm.utils.Utils;

/**
 * A JavaBean accessor.
 * <p/>
 * <p>This object provides methods that set/get multiple properties
 * of a JavaBean at once.  This class and its support classes have been
 * developed for the comaptibility with cglib
 * (<tt>http://cglib.sourceforge.net/</tt>).
 *
 * @author Muga Nishizawa
 * @author modified by Shigeru Chiba
 * @author modified by <a href="mailto:xunchangguo@gmail.com">xunchangguo</a>
 * 
 */
public abstract class BulkAccessor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4609107500157630488L;
	
	private static SoftLimitMRUCache<String, BulkAccessor> cache;
	protected Class target;
	protected Method[] getters, setters;
	protected String[] fields;
	
	static {
		String prop = PropertyHolderUtil.getProperty("cache.max.strong.references");
		if(prop != null && prop.trim().length() > 0) {
			int maxsoftref = 2048;
			int maxstrongref = Utils.strToInt(prop);
			prop = PropertyHolderUtil.getProperty("cache.max.soft.references");
			if(prop != null && prop.trim().length() > 0) {
				maxsoftref = Utils.strToInt(prop);
			}
			cache = new SoftLimitMRUCache<String, BulkAccessor>(maxstrongref, maxsoftref);
		} else {
			cache = new SoftLimitMRUCache<String, BulkAccessor>();
		}
	}

	protected BulkAccessor() {
	}

	/**
	 * Obtains the values of properties of a given bean.
	 *
	 * @param bean   JavaBean.
	 * @param values the obtained values are stored in this array.
	 */
	public abstract void getPropertyValues(Object bean, Object[] values);

	/**
	 * Sets properties of a given bean to specified values.
	 *
	 * @param bean   JavaBean.
	 * @param values the values assinged to properties.
	 */
	public abstract void setPropertyValues(Object bean, Object[] values);

	/**
	 * Returns the values of properties of a given bean.
	 *
	 * @param bean JavaBean.
	 */
	public Object[] getPropertyValues(Object bean) {
		Object[] values = new Object[getters.length];
		getPropertyValues( bean, values );
		return values;
	}

	/**
	 * Returns the setter of properties.
	 */
	public Method[] getGetters() {
		return getters;
	}

	/**
	 * Returns the getter of the properties.
	 */
	public Method[] getSetters() {
		return setters;
	}

	/**
	 * @return the fields
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * Creates a new instance of <code>BulkAccessor</code>.
	 * The created instance provides methods for setting/getting
	 * specified properties at once.
	 *
	 * @param beanClass the class of the JavaBeans accessed
	 *                  through the created object.
	 * @param getters   the setter methods for specified properties.
	 * @param setters   the getter methods for specified properties.
	 */
	public static BulkAccessor create(
			Class beanClass,
			Method[] getters,
			Method[] setters,
			String[] fields) {
		String key = beanClass.getName();
		BulkAccessor accessor = cache.get(key);
		if(accessor != null) {
			return accessor;
		}
		BulkAccessorFactory factory = new BulkAccessorFactory( beanClass, getters, setters, fields);
		accessor = factory.create0();
		if(accessor != null) {
			cache.put(key, accessor);
		}
		return accessor;
	}
	
	/**
	 * get BulkAccessor from Cache
	 * @param bulkAccessorKey
	 * @return
	 */
	public static BulkAccessor getCacheBulkAccessor(String bulkAccessorKey) {
		return cache.get(bulkAccessorKey);
	}
}
