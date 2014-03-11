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
package org.uorm.dao.dialect;

import org.uorm.dao.common.JdbcUtils;
import org.uorm.dao.id.SequenceGenerator;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-25       郭训常            创建<br/>
 */
public class H2Dialect implements Dialect {

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#supportsLimit()
	 */
	@Override
	public boolean supportsOffset() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getLimitString(java.lang.String, int, int)
	 */
	@Override
	public String getLimitString(String query, int offset, int limit) {
		StringBuffer sb = new StringBuffer( query.length() + 20 );
		sb.append( query );
		if(offset > 0){
			sb.append(" limit ").append(limit).append(" offset ").append(offset);
		}else{
			sb.append(" limit ").append(limit);
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getSelectGUIDString()
	 */
	@Override
	public String getSelectGUIDString() {
		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getSequenceNextValString(java.lang.String)
	 */
	@Override
	public String getSequenceNextValString(String tablename) {
		return "call next value for " + SequenceGenerator.getDefaultSequenceName(tablename);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getCountSql(java.lang.String)
	 */
	@Override
	public String getCountSql(String query) {
		String rsel = JdbcUtils.removeSelect(query);
		if(rsel == null) {
			return "SELECT COUNT(0) FROM (" + query +") AS _TCOUNT";
		} else {
			return "SELECT COUNT(0) " + rsel;
		}
//		return "SELECT COUNT(0) " + JdbcUtils.removeSelect(query);
	}
}
