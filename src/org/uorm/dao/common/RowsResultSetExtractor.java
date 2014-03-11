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
import java.util.ArrayList;
import java.util.List;

import org.uorm.orm.mapping.IObjectReader;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-20       郭训常            创建<br/>
 */
public class RowsResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private IObjectReader objectReader;
	private Class<T> cls = null;
	private int offset = 0;
	private int max = Integer.MAX_VALUE;
	
	/**
	 * @param objectReader
	 * @param cls
	 */
	public RowsResultSetExtractor(IObjectReader objectReader, Class<T> cls) {
		super();
		this.objectReader = objectReader;
		this.cls = cls;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
	@Override
	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<T>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		while (rs.next()) {
			if(pos >= offset){
				try {
					results.add(this.objectReader.read(cls, rs, rsmd));
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if(results.size() >= max){
				break;
			}
			pos ++;
		}
		return results;
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
