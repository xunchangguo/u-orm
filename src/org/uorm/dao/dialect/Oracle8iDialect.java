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
public class Oracle8iDialect implements Dialect {

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
	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		boolean isForUpdate = false;
		if ( sql.toLowerCase().endsWith(" for update") ) {
			sql = sql.substring( 0, sql.length()-11 );
			isForUpdate = true;
		}

		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		if (offset > 0) {
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		}
		else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (offset > 0) {
			pagingSelect.append(" ) row_ ) where rownum_ <= ").append((limit + offset)).append(" and rownum_ > ").append(offset);
		}
		else {
			pagingSelect.append(" ) where rownum <= ").append(limit);
		}

		if ( isForUpdate ) {
			pagingSelect.append( " for update" );
		}

		return pagingSelect.toString();
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getSelectGUIDString()
	 */
	@Override
	public String getSelectGUIDString() {
		return "select rawtohex(sys_guid()) from dual";
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Dialect#getSequenceNextValString(java.lang.String)
	 */
	@Override
	public String getSequenceNextValString(String tablename) {
		return "select " + getSelectSequenceNextValString( fixOracleSeqName(tablename) ) + " from dual";
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}
	
	/**
	 * oracle seq name 不能超过30个字符，超过从后往前截取30个字符当行的seq
	 * @param seqname
	 * @return
	 */
	private String fixOracleSeqName(String tablename) {
		String seqname = SequenceGenerator.getDefaultSequenceName(tablename);
		if(seqname.length() > 30){
			seqname = seqname.substring(seqname.length()-30);
		}
		return seqname;
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
