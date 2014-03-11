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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-20       郭训常            创建<br/>
 */
public class JdbcUtils {

	private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

	/**
	 * close connection
	 * @param con
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Connection", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Connection", ex);
			}
			con = null;
		}
	}
	
	/**
	 * close statement
	 * @param stmt
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.trace("Could not close JDBC Statement", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.trace("Unexpected exception on closing JDBC Statement", ex);
			}
			stmt = null;
		}
	}
	
	/**
	 * close clob
	 * @param clob
	 */
	public static void closeClob(Clob clob) {
		if(clob != null){
			try {
				clob.free();
			} catch (SQLException e) {
				logger.trace("Could not close Clob", e);
			} catch (Throwable ex) {
				logger.trace("Unexpected exception on closing Clob", ex);
			}
			clob = null;
		}
	}
	
	/**
	 * close blob
	 * @param blob
	 */
	public static void closeBlob(Blob blob){
		if(blob != null){
			try {
				blob.free();
			} catch (SQLException e) {
				logger.trace("Could not close Blob", e);
			} catch (Throwable ex) {
				logger.trace("Unexpected exception on closing Blob", ex);
			}
			blob = null;
		}
	}
	
	/**
	 * close ResultSet
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				logger.trace("Could not close JDBC ResultSet", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.trace("Unexpected exception on closing JDBC ResultSet", ex);
			}
			rs = null;
		}
	}
	
	/**
	 * sql含有“distinct”关键字，返回null
	 * 没有，返回去除 select部分剩余sql
	 * @param sql
	 * @return
	 */
	public static String removeSelect(String sql) {
		int beginPos = sql.toLowerCase().indexOf("from");
		int dispos = sql.substring(0, beginPos).indexOf("distinct");
		if(dispos > 0) {
			return null;
		}else{
			return sql.substring(beginPos);
		}
	}
	
}
