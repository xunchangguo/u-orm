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

import java.util.Map;

import org.uorm.DataBaseType;

/**
 * Datasource setting
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class DatasourceConfig {
	public static final String _POOL_TYPE = "___POOL_TYPE_";
	private DataBaseType databasetype = null;
	private String dialectClass = null;//dialect.class
	
	private String driverClass;
	private String jdbcUrl;
	private String username;
	private String password;
	/**连接池属性信息,null：无连接池。not null：有连接池*/
	private Map<String, String> poolPerperties = null;

	public DatasourceConfig() {
		super();
	}

	public DatasourceConfig(String driverClass, String jdbcUrl, String username,
			String password) {
		super();
		this.driverClass = driverClass;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}

	public DatasourceConfig(DataBaseType databasetype, String driverClass,
			String jdbcUrl, String username, String password) {
		super();
		this.databasetype = databasetype;
		this.driverClass = driverClass;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 获取连接池类型
	 * @return
	 */
	public String getPoolType() {
		if(poolPerperties != null) {
			return poolPerperties.get(_POOL_TYPE);
		}
		return null;
	}

	/**
	 * @return the databasetype
	 */
	public DataBaseType getDatabasetype() {
		if(databasetype == null){
			databasetype = guessDataBaseType(this);
		}
		return databasetype;
	}

	/**
	 * @param databasetype the databasetype to set
	 */
	public void setDatabasetype(DataBaseType databasetype) {
		this.databasetype = databasetype;
	}

	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @return the jdbcUrl
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * @param jdbcUrl the jdbcUrl to set
	 */
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the dialectClass
	 */
	public String getDialectClass() {
		return dialectClass;
	}

	/**
	 * @param dialectClass the dialectClass to set
	 */
	public void setDialectClass(String dialectClass) {
		this.dialectClass = dialectClass;
	}

	/**
	 * @return the poolPerperties
	 */
	public Map<String, String> getPoolPerperties() {
		return poolPerperties;
	}

	/**
	 * @param poolPerperties the poolPerperties to set
	 */
	public void setPoolPerperties(Map<String, String> poolPerperties) {
		this.poolPerperties = poolPerperties;
	}


	/**
	 * guess database type
	 * @param datasourcecfg
	 * @return
	 */
	public static DataBaseType guessDataBaseType(DatasourceConfig datasourcecfg) {
		String dbDriver = datasourcecfg.getDriverClass() == null ? "" : datasourcecfg.getDriverClass().toLowerCase();
		if (dbDriver.indexOf("oracledriver") >= 0) {
			return DataBaseType.ORACLE;
		} else if (dbDriver.indexOf("db2driver") >= 0) {
			return DataBaseType.DB2;
		} else if (dbDriver.indexOf("postgresql") >= 0) {
			return DataBaseType.PSQL;
		} else if (dbDriver.indexOf("sqlserverdriver") >= 0) {
			return DataBaseType.SQLSERVER;
		} else if (dbDriver.indexOf("mysql") >= 0) {
			return DataBaseType.MYSQL;
		} else if (dbDriver.indexOf("h2") >= 0) {//org.h2.Driver
			return DataBaseType.H2;
		} else if (dbDriver.indexOf("sqlite") >= 0) {//org.sqlite.JDBC 
			return DataBaseType.SQLITE;
		} else if (dbDriver.indexOf("hsqldb") >= 0) {
			return DataBaseType.HSQL;
		} else if (dbDriver.indexOf("derby") >= 0) {
			return DataBaseType.DERBY;
		} else if (dbDriver.indexOf("firebirdsql") >= 0) {
			return DataBaseType.FIREBIRD;
		} else if (dbDriver.indexOf("interbase") >= 0) {
			return DataBaseType.INTERBASE;
		} else if (dbDriver.indexOf("informix") >= 0) {
			return DataBaseType.INFORMIX;
		} else if (dbDriver.indexOf("ingres") >= 0) {
			return DataBaseType.INGRES10;
		} else if (dbDriver.indexOf("rdms2200") >= 0) {
			return DataBaseType.RDMS2200;
		} else if (dbDriver.indexOf("timesten") >= 0) {
			return DataBaseType.TIMESTEN;
		} else if (dbDriver.indexOf("mariadb") >= 0) {
			return DataBaseType.MARIADB;
		} else if (dbDriver.indexOf("sybase") >= 0) {
			return DataBaseType.SYBASE;
		}
		if("sun.jdbc.odbc.jdbcodbcdriver".equals(dbDriver)) {
			if(datasourcecfg.getJdbcUrl().indexOf("Microsoft Access Driver") > 0) {
				return DataBaseType.ACCESS;
			}
		}
		return DataBaseType.OTHER;
	}
}
