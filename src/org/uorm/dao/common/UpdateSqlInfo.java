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
package org.uorm.dao.common;

import java.util.ArrayList;
import java.util.List;

import org.uorm.orm.annotation.FieldMapping;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-27       郭训常            创建<br/>
 */
public class UpdateSqlInfo {
	private String sql = null;
	private List<FieldMapping> parameterMappings = null;

	/**
	 * 
	 */
	public UpdateSqlInfo() {
		super();
		this.parameterMappings = new ArrayList<FieldMapping>();
	}

	/**
	 * @param sql
	 */
	public UpdateSqlInfo(String sql) {
		super();
		this.sql = sql;
		this.parameterMappings = new ArrayList<FieldMapping>();
	}
	
	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}
	
	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	/**
	 * @return the parameterMappings
	 */
	public List<FieldMapping> getParameterMappings() {
		return parameterMappings;
	}
	
	public void addParameter(FieldMapping field) {
		this.parameterMappings.add(field);
	}

}
