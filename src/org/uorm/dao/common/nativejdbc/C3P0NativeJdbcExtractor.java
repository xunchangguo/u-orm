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
package org.uorm.dao.common.nativejdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-1-18       郭训长            创建<br/>
 */
public class C3P0NativeJdbcExtractor implements INativeJdbcExtractor {

	private final Method getRawConnectionMethod;

	/**
	 * This method is not meant to be used directly; it rather serves
	 * as callback method for C3P0's "rawConnectionOperation" API.
	 * @param con a native Connection handle
	 * @return the native Connection handle, as-is
	 */
	public static Connection getRawConnection(Connection con) {
		return con;
	}
	
	public C3P0NativeJdbcExtractor() {
		try {
			this.getRawConnectionMethod = getClass().getMethod("getRawConnection", new Class[] {Connection.class});
		}
		catch (NoSuchMethodException ex) {
			throw new IllegalStateException("Internal error in C3P0NativeJdbcExtractor: " + ex.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.nativejdbc.INativeJdbcExtractor#doGetNativeConnection(java.sql.Connection)
	 */
	@Override
	public Connection doGetNativeConnection(Connection con) throws SQLException {
		if (con instanceof com.mchange.v2.c3p0.C3P0ProxyConnection) {
			com.mchange.v2.c3p0.C3P0ProxyConnection cpCon = (com.mchange.v2.c3p0.C3P0ProxyConnection) con;
			try {
				return (Connection) cpCon.rawConnectionOperation(
						this.getRawConnectionMethod, null, new Object[] {com.mchange.v2.c3p0.C3P0ProxyConnection.RAW_CONNECTION});
			} catch (SQLException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new SQLException(ex);
			}
		}
		return con;
	}

}
