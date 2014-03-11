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

import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.uorm.orm.bytecode.FastClass;
import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.mapping.ObjectMappingCache;
import org.uorm.orm.mapping.ObjectReader;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-9-12       郭训长            创建<br/>
 */
public class JavassistObjectReader extends ObjectReader {
	
	@SuppressWarnings("unchecked")
	public <T> T read(Class<T> cls, ResultSet result, ResultSetMetaData rsmd) throws Exception {
		int count = rsmd.getColumnCount();
		Map<String, Method> setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(cls);
		if( ((count == 1) && (setMethods == null || setMethods.isEmpty())) ||
				(( (count == 1) && ObjectMappingCache.getInstance().getClassMapping(cls) == null)) ) {
			//for simple obj class
			Object val = result.getObject(1);
			if(val == null){
				return null;
			}else{
				if(GenericConverterFactory.getInstance().needConvert(val.getClass(), cls)) {
					try{
						return (T)getValue(result, 1, cls);
					} catch (SQLException e) {
						ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), cls);
						if(converter != null) {
							return (T)converter.convert(val, cls);
						}
						throw e;
					}
				}else{
					return (T)val;
				}
			}
		} else {
			String[] fields = null;
			BulkAccessor accessor = BulkAccessor.getCacheBulkAccessor(cls.getName());
			if(accessor == null) {
				Map<String, Method> getMap = org.uorm.orm.mapping.ObjectMappingCache.getInstance().getPojoGetMethod(cls);
				fields = new String[setMethods.size()];
				Method[] getters = new Method[fields.length];
				Method[] setters = new Method[fields.length];
				int i = 0;
				for(String key : getMap.keySet()) {
					fields[i] = key;
					getters[i] = getMap.get(key);
					setters[i] = setMethods.get(key);
					i ++;
				}
				accessor = BulkAccessor.create(cls, getters, setters, fields);
			}
			if(fields == null) {
				fields = accessor.getFields();
			}
			FastClass fastClass = org.uorm.orm.bytecode.FastClass.create( cls );
			Object instance = fastClass.newInstance();
			Object[] values = new Object[fields.length];
			for(int i = 1; i <= count; i++){
				String columnName = rsmd.getColumnLabel(i);//rsmd.getColumnName(i);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				//get field position
				int pos = getFieldPosition(columnName.toUpperCase(), fields);
				if(pos >= 0) {
					Object val = result.getObject(i);
					if(val != null ){
						Class<?> memberType = accessor.getSetters()[pos].getParameterTypes()[0];
						if(val instanceof Clob){
							if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType))) {
								values[pos] = clob2bytes((Clob)val);
							}else if(String       .class.equals(memberType)) {
								values[pos] = clob2String((Clob)val);
							}else{
								values[pos] = getValue(result, i, memberType);
							}
						}else if(val instanceof Blob) {
							if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType))) {
								values[pos] = blob2bytes((Blob)val);
							}else{
								values[pos] = getValue(result, i, memberType);
							}
						}else{
							if(GenericConverterFactory.getInstance().needConvert(val.getClass(), cls)) {//convert
								try{
									val = getValue(result, i, memberType);
								} catch (SQLException e) {
									ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), cls);
									if(converter != null) {
										val = converter.convert(val, cls);
									}
									throw e;
								}
							}
							values[pos] = val;
						}
					}//end if val not null
				}//end if has this property
			}
			accessor.setPropertyValues(instance, values);
			return (T) instance;
		}
	}
	
	/**
	 * index of field Object
	 * @param field4Idx
	 * @param fields
	 * @return
	 */
	private int getFieldPosition(String field4Idx, String[] fields) {
		for(int i = 0; i < fields.length; i ++) {
			if(field4Idx.equals(fields[i])) {
				return i;
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.ObjectReader#readValue2Map(java.lang.Object)
	 */
	public Map<String, Object> readValue2Map(Object pojo) throws Exception {
		Class<?> cls = pojo.getClass();
		String[] fields = null;
		BulkAccessor accessor = BulkAccessor.getCacheBulkAccessor(cls.getName());
		if(accessor == null) {
			Map<String, Method> setMap = org.uorm.orm.mapping.ObjectMappingCache.getInstance().getPojoSetMethod(cls);
			Map<String, Method> getMap = org.uorm.orm.mapping.ObjectMappingCache.getInstance().getPojoGetMethod(cls);
			fields = new String[setMap.size()];
			Method[] getters = new Method[fields.length];
			Method[] setters = new Method[fields.length];
			int i = 0;
			for(String key : getMap.keySet()) {
				fields[i] = key;
				getters[i] = getMap.get(key);
				setters[i] = setMap.get(key);
				i ++;
			}
			accessor = BulkAccessor.create(cls, getters, setters, fields);
		}
		Object[] vals = accessor.getPropertyValues(pojo);
		if(fields == null) {
			fields = accessor.getFields();
		}
		Map<String, Object> model = new HashMap<String, Object>();
		for(int i = 0; i < fields.length; i ++) {
			if(vals[i] != null) {
				model.put(fields[i], vals[i]);
			}
		}
		return model;
	}

}
