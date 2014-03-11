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
package org.uorm.dao.id;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import org.uorm.dao.common.ICommonDao;
import org.uorm.dao.dialect.Dialect;
import org.uorm.orm.annotation.FieldMapping;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-30       郭训长            创建<br/>
 */
public interface IdentifierGenerator {

	/**
	 * Generate a new identifier.
	 * @param dialect
	 * @param dao
	 * @param pojo the entity for which the id is being generated
	 * @param idFieldInfo the id FieldMapping info
	 * @param writevalue is write generated value to the pojo entity
	 * @return
	 * @throws SQLException
	 */
	public Serializable generate(Dialect dialect, ICommonDao dao, Object pojo, FieldMapping idFieldInfo, boolean writevalue) throws SQLException;
	
	/**
	 * Generate a new identifier.
	 * @param dialect
	 * @param dao
	 * @param cls  the class for which the id is being generated
	 * @param valmap
	 * @param idFieldInfo the id FieldMapping info
	 * @param writevalue is write generated value to the valmap
	 * @return
	 * @throws SQLException
	 */
	public Serializable generate(Dialect dialect, ICommonDao dao, Class<?> cls, Map<String, Object> valmap, FieldMapping idFieldInfo, boolean writevalue) throws SQLException;
	
	/**
	 * Generate new identifiers. for batch
	 * @param dialect
	 * @param dao
	 * @param pojos
	 * @param idFieldInfo
	 * @param writevalue
	 * @return
	 * @throws SQLException
	 */
	public Serializable[] generate(Dialect dialect, ICommonDao dao, Object[] pojos, FieldMapping idFieldInfo, boolean writevalue) throws SQLException;
	
	/**
	 * Generate new identifiers. for batch
	 * @param dialect
	 * @param dao
	 * @param cls
	 * @param valmaps
	 * @param idFieldInfo
	 * @param writevalue
	 * @return
	 * @throws SQLException
	 */
	public Serializable[] generate(Dialect dialect, ICommonDao dao, Class<?> cls, Map<String, Object>[] valmaps, FieldMapping idFieldInfo, boolean writevalue) throws SQLException;
	
}
