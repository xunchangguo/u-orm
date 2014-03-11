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

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-25       郭训常            创建<br/>
 */
public class Oracle9iDialect extends Oracle8iDialect {

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.Oracle8iDialect#getLimitString(java.lang.String, int, int)
	 */
	@Override
	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		String forUpdateClause = null;
		boolean isForUpdate = false;
		final int forUpdateIndex = sql.toLowerCase().lastIndexOf( "for update") ;
		if ( forUpdateIndex > -1 ) {
			// save 'for update ...' and then remove it
			forUpdateClause = sql.substring( forUpdateIndex );
			sql = sql.substring( 0, forUpdateIndex-1 );
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
			pagingSelect.append(" ) row_ where rownum <= ").append((limit + offset)).append(") where rownum_ > ").append(offset);
		}
		else {
			pagingSelect.append(" ) where rownum <= ").append(limit);
		}

		if ( isForUpdate ) {
			pagingSelect.append( ' ' );
			pagingSelect.append( forUpdateClause );
		}

		return pagingSelect.toString();
	}

}
