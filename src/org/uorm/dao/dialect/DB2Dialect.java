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
public class DB2Dialect implements Dialect {

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
		if ( offset == 0 ) {
			return sql + " fetch first " + limit + " rows only";
		}
		//nest the main query in an outer select
		return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
				+ sql + " fetch first " + (offset + limit) + " rows only ) as inner2_ ) as inner1_ where rownumber_ > "
				+ offset + " order by rownumber_";
//		int startOfSelect = sql.toLowerCase().indexOf("select");
//
//		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 )
//		.append( sql.substring(0, startOfSelect) )	// add the comment
//		.append("select * from ( select ") 			// nest the main query in an outer select
//		.append( getRowNumber(sql) ); 				// add the rownnumber bit into the outer query select list
//
//		if ( hasDistinct(sql) ) {
//			pagingSelect.append(" row_.* from ( ")			// add another (inner) nested select
//			.append( sql.substring(startOfSelect) ) // add the main query
//			.append(" ) as row_"); 					// close off the inner nested select
//		}
//		else {
//			pagingSelect.append( sql.substring( startOfSelect + 6 ) ); // add the main query
//		}
//
//		pagingSelect.append(" ) as temp_ where rownumber_ ");
//
//		//add the restriction to the outer select
//		if (offset > 0) {
//			pagingSelect.append("between ").append(offset).append("+1 and ").append((limit+offset));
//			//			pagingSelect.append("between ?+1 and ?");
//		} else {
//			pagingSelect.append("<= ").append(limit);
//			//			pagingSelect.append("<= ?");
//		}
//
//		return pagingSelect.toString();
	}

//	private String getRowNumber(String sql) {
//		StringBuffer rownumber = new StringBuffer(50).append("rownumber() over(");
//
//		int orderByIndex = sql.toLowerCase().indexOf("order by");
//
//		if ( orderByIndex>0 && !hasDistinct(sql) ) {
//			rownumber.append( sql.substring(orderByIndex) );
//		}
//
//		rownumber.append(") as rownumber_,");
//
//		return rownumber.toString();
//	}
//
//	private static boolean hasDistinct(String sql) {
//		return sql.toLowerCase().indexOf("select distinct")>=0;
//	}

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
		return "values nextval for " + SequenceGenerator.getDefaultSequenceName(tablename);
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
//		return "SELECT COUNT(0) FROM (SELECT 0 " + JdbcUtils.removeSelect(query) +") AS _TCOUNT";
//		return "SELECT COUNT(0) FROM (" + query +")";
	}

}
