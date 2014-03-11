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
package org.uorm.orm.mapping;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;


/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class ObjectMappingCache {
	private static final String _FILTERED_FIELD = "class";
	private static ObjectMappingCache _instance = new ObjectMappingCache();
	private Map<Class<?>, Map<String, PropertyDescriptor>> objectPropertyMap = null;
	private Map<Class<?>, Map<String, FieldMapping>> objectFieldMap = null;
	private Map<Class<?>, ClassMapping> classMappingMap = null;

	private ObjectMappingCache() {
		objectPropertyMap = new HashMap<Class<?>, Map<String,PropertyDescriptor>>();
		objectFieldMap = new HashMap<Class<?>, Map<String,FieldMapping>>();
		classMappingMap = new HashMap<Class<?>, ClassMapping>();
	}
	
	public static ObjectMappingCache getInstance() {
		return _instance;
	}
	
	/**
	 * 获取get方法
	 * @param cls
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	public Map<String, Method> getPojoSetMethod(Class<?> cls) {
		Map<String, PropertyDescriptor> propertys = objectPropertyMap.get(cls);
		if(propertys == null){
			//2013-9-13 add
			if(getClassMapping(cls) == null) {
				return null;
			}
			//2013-9-13 add end
			propertys = initClassProperty(cls);
		}
		Map<String, Method> methodsMap = new HashMap<String, Method>();
		for(String key : propertys.keySet()){
			Method method = propertys.get(key).getWriteMethod();
			if ( Modifier.isPublic( method.getModifiers() ) ) {
				methodsMap.put(key, method);
			}
		}
		return methodsMap;
	}

	/**
	 *  获取所有get方法
	 * @param cls
	 * @param tableName
	 * @return
	 */
	public Map<String, Method> getPojoGetMethod(Class<?> cls) {
		Map<String, PropertyDescriptor> propertys = objectPropertyMap.get(cls);
		if(propertys == null){
			//2013-9-13 add
			if(getClassMapping(cls) == null) {
				return null;
			}
			//2013-9-13 add end
			propertys = initClassProperty(cls);
		}
		Map<String, Method> methodsMap = new HashMap<String, Method>();
		for(String key : propertys.keySet()){
			Method method = propertys.get(key).getReadMethod();
			if ( Modifier.isPublic( method.getModifiers() ) ) {
				methodsMap.put(key, method);
			}
		}
		return methodsMap;
	}
	
	public ClassMapping getClassMapping(Class<?> cls) {
		ClassMapping clsMapping = classMappingMap.get(cls);
		if(clsMapping == null){
			clsMapping = cls.getAnnotation(ClassMapping.class);
			if(clsMapping != null) {
				classMappingMap.put(cls, clsMapping);
			}
		}
		return clsMapping;
	}
	
	private Map<String, PropertyDescriptor> initClassProperty(Class<?> cls) {
		Map<String, PropertyDescriptor> propMap = new HashMap<String, PropertyDescriptor>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			//filter field class
			Map<String, FieldMapping> fieldMappings = objectFieldMap.get(cls);
			if(fieldMappings == null){
				fieldMappings = initClassFieldMapping(cls);
			}
			for (PropertyDescriptor propertyDescriptor : props) {
				String fieldName = propertyDescriptor.getName();
				if( Character.isUpperCase(fieldName.charAt(0)) ) {
					char chars[] = fieldName.toCharArray();
					chars[0] = Character.toLowerCase(chars[0]);
					fieldName = new String(chars);
				}
				if( !_FILTERED_FIELD.equals( fieldName ) ) {
					FieldMapping fieldmapping = fieldMappings.get(fieldName);
					if( fieldmapping == null ) {
						//变量名upper
						propMap.put(fieldName.toUpperCase(), propertyDescriptor);
					} else {
						propMap.put(fieldmapping.columnName(), propertyDescriptor);
					}
				}
			}
			objectPropertyMap.put(cls, propMap);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return propMap;
	}

	private Map<String, FieldMapping> initClassFieldMapping(Class<?> cls) {
		Map<String, FieldMapping> fieldMappings = new HashMap<String, FieldMapping>();
		while (!(cls ==null || cls.equals(Object.class))) {
			Field[] flds = cls.getDeclaredFields();
			for (Field field : flds) {
				//filter static and final
				if ((field.getModifiers() & Modifier.STATIC)!=0
						|| (field.getModifiers() & Modifier.FINAL) != 0) {
					continue;
				}
				FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
				if(fieldMapping != null){
					fieldMappings.put(field.getName(), fieldMapping);
				}
			}
			cls = cls.getSuperclass();
		}
		objectFieldMap.put(cls, fieldMappings);
		return fieldMappings;
	}

	/**
	 * @return the object FieldMapping
	 */
	public Map<String, FieldMapping> getObjectFieldMap(Class<?> cls) {
		Map<String, FieldMapping> fieldMappings = objectFieldMap.get(cls);
		if(fieldMappings == null){
			fieldMappings = initClassFieldMapping(cls);
		}
		return fieldMappings;
	}
	
	public Map<String, PropertyDescriptor> getObjectPropertyMap(Class<?> cls) {
		Map<String, PropertyDescriptor> propMap = objectPropertyMap.get(cls);
		if ( propMap == null ) {
			propMap = initClassProperty(cls);
		}
		return propMap;
	}
	
}
