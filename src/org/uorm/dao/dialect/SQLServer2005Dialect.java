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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-11-15       郭训长            创建<br/>
 */
public class SQLServer2005Dialect extends SQLServerDialect {
	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String DISTINCT = "distinct";
	private static final String ORDER_BY = "order by";
	/**
	 * Regular expression for stripping alias
	 */
	private static final Pattern ALIAS_PATTERN = Pattern.compile( "\\sas\\s[^,]+(,?)" );
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.SQLServerDialect#supportsOffset()
	 */
	@Override
	public boolean supportsOffset() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.SQLServerDialect#getLimitString(java.lang.String, int, int)
	 */
	@Override
	public String getLimitString(String query, int offset, int limit) {
		if ( offset > 1 || limit > 1 ) {
			return getLimitString( query, true );
		}
		return query;
	}

	/**
	 * Add a LIMIT clause to the given SQL SELECT (HHH-2655: ROW_NUMBER for Paging)
	 *
	 * The LIMIT SQL will look like:
	 *
	 * <pre>
	 * WITH query AS (
	 *   original_select_clause_without_distinct_and_order_by,
	 *   ROW_NUMBER() OVER ([ORDER BY CURRENT_TIMESTAMP | original_order_by_clause]) as __hibernate_row_nr__
	 *   original_from_clause
	 *   original_where_clause
	 *   group_by_if_originally_select_distinct
	 * )
	 * SELECT * FROM query WHERE __hibernate_row_nr__ >= offset AND __hibernate_row_nr__ < offset + last
	 * </pre>
	 *
	 * @param querySqlString The SQL statement to base the limit query off of.
	 * @param hasOffset Is the query requesting an offset?
	 *
	 * @return A new SQL statement with the LIMIT clause applied.
	 */
	public String getLimitString(String querySqlString, boolean hasOffset) {
		StringBuilder sb = new StringBuilder( querySqlString.trim() );

		int orderByIndex = shallowIndexOfWord( sb, ORDER_BY, 0 );
		CharSequence orderby = orderByIndex > 0 ? sb.subSequence( orderByIndex, sb.length() )
				: "ORDER BY CURRENT_TIMESTAMP";

		// Delete the order by clause at the end of the query
		if ( orderByIndex > 0 ) {
			sb.delete( orderByIndex, orderByIndex + orderby.length() );
		}
		// HHH-5715 bug fix
		replaceDistinctWithGroupBy( sb );
		insertRowNumberFunction( sb, orderby );
		// Wrap the query within a with statement:
		sb.insert( 0, "WITH query AS (" ).append( ") SELECT * FROM query " );
		sb.append( "WHERE __UORM_ROW_NR__ >= ? AND __UORM_ROW_NR__ < ?" );

		return sb.toString();
	}
	
	/**
	 * We must place the row_number function at the end of select clause.
	 *
	 * @param sql the initial sql query without the order by clause
	 * @param orderby the order by clause of the query
	 */
	protected void insertRowNumberFunction(StringBuilder sql, CharSequence orderby) {
		// Find the end of the select clause
		int selectEndIndex = shallowIndexOfWord( sql, FROM, 0 );

		// Insert after the select clause the row_number() function:
		sql.insert( selectEndIndex - 1, ", ROW_NUMBER() OVER (" + orderby + ") as __UORM_ROW_NR__" );
	}
	
	/**
	 * Returns index of the first case-insensitive match of search term surrounded by spaces
	 * that is not enclosed in parentheses.
	 *
	 * @param sb String to search.
	 * @param search Search term.
	 * @param fromIndex The index from which to start the search.
	 * @return Position of the first match, or {@literal -1} if not found.
	 */
	private static int shallowIndexOfWord(final StringBuilder sb, final String search, int fromIndex) {
		final int index = shallowIndexOf( sb, ' ' + search + ' ', fromIndex );
		return index != -1 ? ( index + 1 ) : -1; // In case of match adding one because of space placed in front of search term.
	}

	/**
	 * Returns index of the first case-insensitive match of search term that is not enclosed in parentheses.
	 *
	 * @param sb String to search.
	 * @param search Search term.
	 * @param fromIndex The index from which to start the search.
	 * @return Position of the first match, or {@literal -1} if not found.
	 */
	private static int shallowIndexOf(StringBuilder sb, String search, int fromIndex) {
		final String lowercase = sb.toString().toLowerCase(); // case-insensitive match
		final int len = lowercase.length();
		final int searchlen = search.length();
		int pos = -1, depth = 0, cur = fromIndex;
		do {
			pos = lowercase.indexOf( search, cur );
			if ( pos != -1 ) {
				for ( int iter = cur; iter < pos; iter++ ) {
					char c = sb.charAt( iter );
					if ( c == '(' ) {
						depth = depth + 1;
					}
					else if ( c == ')' ) {
						depth = depth - 1;
					}
				}
				cur = pos + searchlen;
			}
		} while ( cur < len && depth != 0 && pos != -1 );
		return depth == 0 ? pos : -1;
	}
	
	/**
	 * Utility method that checks if the given sql query is a select distinct one and if so replaces the distinct select
	 * with an equivalent simple select with a group by clause.
	 *
	 * @param sql an sql query
	 */
	protected static void replaceDistinctWithGroupBy(StringBuilder sql) {
		int distinctIndex = shallowIndexOfWord( sql, DISTINCT, 0 );
		int selectEndIndex = shallowIndexOfWord( sql, FROM, 0 );
		if (distinctIndex > 0 && distinctIndex < selectEndIndex) {
			sql.delete( distinctIndex, distinctIndex + DISTINCT.length() + " ".length());
			sql.append( " group by" ).append( getSelectFieldsWithoutAliases( sql ) );
		}
	}
	
	public static final String SELECT_WITH_SPACE = SELECT + ' ';
	/**
	 * This utility method searches the given sql query for the fields of the select statement and returns them without
	 * the aliases.
	 *
	 * @param sql sql query
	 *
	 * @return the fields of the select statement without their alias
	 */
	protected static CharSequence getSelectFieldsWithoutAliases(StringBuilder sql) {
		final int selectStartPos = shallowIndexOf( sql, SELECT_WITH_SPACE, 0 );
		final int fromStartPos = shallowIndexOfWord( sql, FROM, selectStartPos );
		String select = sql.substring( selectStartPos + SELECT.length(), fromStartPos );

		// Strip the as clauses
		return stripAliases( select );
	}
	
	/**
	 * Utility method that strips the aliases.
	 *
	 * @param str string to replace the as statements
	 *
	 * @return a string without the as statements
	 */
	protected static String stripAliases(String str) {
		Matcher matcher = ALIAS_PATTERN.matcher( str );
		return matcher.replaceAll( "$1" );
	}
}
