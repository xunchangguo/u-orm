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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.uorm.orm.mapping.IJsonReader;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-7-5       郭训长            创建<br/>
 */
public class RowsResultSetJsonExtractor implements ResultSetExtractor<String> {

	private int offset = 0;
	private int max = Integer.MAX_VALUE;
	
	private IJsonReader jsonReader = null;
	
	public RowsResultSetJsonExtractor(IJsonReader jsonReader) {
		super();
		this.jsonReader = jsonReader;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
	@Override
	public String extractData(ResultSet rs) throws SQLException {
		StringBuilder json = new StringBuilder();
		json.append('[');
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		int len = 0;
		while (rs.next()) {
			if(pos >= offset){
				try {
					if (len == 0) {
						json.append(this.jsonReader.read(rs, rsmd));
					} else {
						json.append(',').append(this.jsonReader.read(rs, rsmd));
					}
					len ++;
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if(len >= max){
				break;
			}
			pos ++;
		}
		json.append(']');
		return json.toString();
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}
	
}
