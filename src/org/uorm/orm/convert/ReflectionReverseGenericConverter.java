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
package org.uorm.orm.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-12-5       郭训长            创建<br/>
 */
public class ReflectionReverseGenericConverter implements
IReverseGenericConverter {
	private final ConcurrentHashMap<Class<?>, Map<Class<?>, Constructor<?>>> _cache = new ConcurrentHashMap<Class<?>, Map<Class<?>, Constructor<?>>>();

	private static final Map<Class<?>, Constructor<?>> _EMPTY_CONSTRUCTOR_MAP = Collections.emptyMap();
	private static final List<Class<?>> _EMPTY_SOURCE_LIST = Collections.emptyList();

	/* (non-Javadoc)
	 * @see org.uorm.orm.convert.ITypeConverter#convert(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object convert(Object source, Class<?> targetType)
			throws TypeConvertException {
		Map<Class<?>, Constructor<?>> constructors = _getConstructorMapForTarget(targetType);
		Class<?> sourceClass = source.getClass();
		Constructor<?> c = constructors.get(sourceClass);

		// If direct match failed, check if the types are assignable
		if(c == null) {
			for(Map.Entry<Class<?>, Constructor<?>> entry: constructors.entrySet()) {
				if(entry.getKey().isAssignableFrom(sourceClass)) {
					c = entry.getValue();
					break;
				}
			}
		}
		if(c != null) {
			try {
				return c.newInstance(source);
			} catch(Exception e) {
				throw new TypeConvertException(source, targetType, e);
			}
		}
		throw new TypeConvertException(source, targetType);
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.convert.IReverseGenericConverter#getSourceTypes(java.lang.Class)
	 */
	@Override
	public List<Class<?>> getSourceTypes(Class<?> targetType) {
		Map<Class<?>, Constructor<?>> cachedConstructors = _getConstructorMapForTarget(targetType);

		if(cachedConstructors == _EMPTY_CONSTRUCTOR_MAP)
			return _EMPTY_SOURCE_LIST;
		else
			return new ArrayList<Class<?>>(cachedConstructors.keySet());
	}

	private Map<Class<?>, Constructor<?>> _getConstructorMapForTarget(Class<?> targetType) {
		Map<Class<?>, Constructor<?>> cachedConstructors = _cache.get(targetType);

		if(cachedConstructors == null) {
			cachedConstructors = _EMPTY_CONSTRUCTOR_MAP;

			Constructor<?> constructors[] = targetType.getConstructors();
			for(Constructor<?> c: constructors) {
				// Use only public non-depricated constructors
				if(Modifier.isPublic(c.getModifiers()) && c.getAnnotation(Deprecated.class) == null) {
					Class<?> params[] = c.getParameterTypes();

					// We are looking for all single-parameter constructors
					if(params.length == 1) {
						if(cachedConstructors == _EMPTY_CONSTRUCTOR_MAP)
							cachedConstructors = new HashMap<Class<?>, Constructor<?>>();

						cachedConstructors.put(params[0], c);
					}
				}
			}

			_cache.put(targetType, cachedConstructors);
		}
		return cachedConstructors;
	}

}
