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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
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
public interface IObjectReader {
	/**
	 * read ResultSet to Class cls instance
	 * @param <T>
	 * @param cls
	 * @param result
	 * @param rsmd
	 * @return
	 * @throws Exception
	 */
	public <T> T read(Class<T> cls, ResultSet result, ResultSetMetaData rsmd) throws Exception;
	
	/**
	 * read object value to map, key is table colName
	 * @param pojo
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> readValue2Map(Object pojo) throws Exception;
	
	/**
	 * write pk value to object
	 * @param pojo
	 * @param pkcolumnName
	 * @return
	 * @throws Exception
	 */
	public boolean writePkVal2Pojo(Object pojo, Object pkval, String pkcolumnName) throws Exception;

	/**
	 *  read ResultSet to Map
	 * @param rs
	 * @param rsmd
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> readToMap(ResultSet rs, ResultSetMetaData rsmd) throws Exception;
	
	/**
	 * read ResultSet to Object[]
	 * @param rs
	 * @param rsmd
	 * @return
	 * @throws Exception
	 */
	public Object[] readToArray(ResultSet rs, ResultSetMetaData rsmd) throws Exception;
	
	/**
	 * get class  mapping info 
	 * @param <T>
	 * @param cls
	 * @return
	 */
	public <T> ClassMapping getClassMapping(Class<T> cls);
	
	/**
	 * get cls mapping table's primary keys, order by @param keyOrder
	 * @param cls
	 * @param keyOrder
	 * @return
	 */
	public FieldMapping[] getClassPrimaryKeys(Class<?> cls, String keyOrder);
	
	/**
	 * get object FieldMapping info 
	 * @param cls
	 * @return
	 */
	public Map<String, FieldMapping> getObjectFieldMap(Class<?> cls);
	
	/**
	 * get target sql class by SQL types
	 * @param sqlType: SQL type: @see {@link Types}
	 * @return
	 */
	public Class<?> getTargetSqlClass(int sqlType);
	
}
