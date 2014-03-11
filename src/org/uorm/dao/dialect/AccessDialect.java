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

/**
 * 如要实现快速Access分页，使用
 * select top 每页数量 * from 表 where id > (select max(id) from (select top (页数-1)*每页数量 id from 表 order by id) a) order by id
 * 类似这种SQL语句，直接使用普通查询，而不是用分页查询
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-8-29       郭训长            创建<br/>
 */
public class AccessDialect implements Dialect {

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#supportsOffset()
	 */
	@Override
	public boolean supportsOffset() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getLimitString(java.lang.String, int, int)
	 */
	@Override
	public String getLimitString(String query, int offset, int limit) {
		if ( offset > 0 ) {
			throw new UnsupportedOperationException( "query result offset is not supported" );
		}
		return new StringBuffer( query.length() + 8 )
		.append( query )
		.insert( getAfterSelectInsertPoint( query ), " top " + limit )
		.toString();
	}
	
	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getSelectGUIDString()
	 */
	@Override
	public String getSelectGUIDString() {
		throw new UnsupportedOperationException( getClass().getName() + " does not support GUID with sql" );
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getSequenceNextValString(java.lang.String)
	 */
	@Override
	public String getSequenceNextValString(String tablename) {
		throw new UnsupportedOperationException( getClass().getName() + " does not support Sequence" );
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
			return "SELECT COUNT(0) FROM (SELECT 0 " + rsel +") AS _TCOUNT";
		}
//		return "SELECT COUNT(0) FROM (SELECT 0 " + JdbcUtils.removeSelect(query) +")";
//		return "SELECT COUNT(0) FROM (" + query +")";
	}

}
