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
package org.uorm.dao.common.pool;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.uorm.dao.common.DatasourceConfig;
import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-3-29       郭训长            创建<br/>
 */
public class JdbcPoolDataSourceCreator implements IDataSourceCreator {

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.pool.IDataSourceCreator#createDatasource(org.uorm.dao.common.DatasourceConfig)
	 */
	@Override
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		DataSource datasource = null;
		Boolean XA = Utils.str2Boolean(datasourcecfg.getPoolPerperties().get("isXADataSource"));
		if(XA != null){
			datasource = createJdbcPoolDataSource(datasourcecfg, XA);
		}else{
			datasource = createJdbcPoolDataSource(datasourcecfg, false);
		}
		return datasource;
	}

	/**
	 * create jdbc-pool DataSource
	 * @param datasourcecfg
	 * @return
	 */
	private static DataSource createJdbcPoolDataSource(
			DatasourceConfig datasourcecfg, boolean XA) {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = XA?new org.apache.tomcat.jdbc.pool.XADataSource():new org.apache.tomcat.jdbc.pool.DataSource();
		dataSource.setDriverClassName(datasourcecfg.getDriverClass());
		dataSource.setUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map<String, String> props = datasourcecfg.getPoolPerperties();
		Boolean defaultAutoCommit = Utils.str2Boolean(props.get("defaultAutoCommit"));
		if(defaultAutoCommit != null){
			dataSource.setDefaultAutoCommit(defaultAutoCommit);
		}
		Boolean defaultReadOnly = Utils.str2Boolean(props.get("defaultReadOnly"));
		if(defaultReadOnly != null){
			dataSource.setDefaultReadOnly(defaultReadOnly);
		}
		Integer defaultTransactionIsolation = Utils.strToInteger(props.get("defaultTransactionIsolation"));
		if(defaultTransactionIsolation != null){
			dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
		}
		String defaultCatalog = props.get("defaultCatalog");
		if(defaultCatalog != null && defaultCatalog.trim().length() > 0){
			dataSource.setDefaultCatalog(defaultCatalog);
		}
		
		int maxActive = Utils.strToInt(props.get("maxActive"));
		if(maxActive >0){
			dataSource.setMaxActive(maxActive);
		}
		int maxIdle = Utils.strToInt(props.get("maxIdle"));
		if(maxIdle >0){
			dataSource.setMaxIdle(maxIdle);
		}
		int minIdle = Utils.strToInt(props.get("minIdle"));
		if(minIdle >0){
			dataSource.setMinIdle(minIdle);
		}
		int initialSize = Utils.strToInt(props.get("initialSize"));
		if(initialSize >0){
			dataSource.setInitialSize(initialSize);
		}
		int maxWait = Utils.strToInt(props.get("maxWait"));
		if(maxWait >0){
			dataSource.setMaxWait(maxWait);
		}
		
		Boolean testOnBorrow = Utils.str2Boolean(props.get("testOnBorrow"));
		if(testOnBorrow != null){
			dataSource.setTestOnBorrow(testOnBorrow);
		}
		Boolean testOnReturn = Utils.str2Boolean(props.get("testOnReturn"));
		if(testOnReturn != null){
			dataSource.setTestOnReturn(testOnReturn);
		}
		Boolean testWhileIdle = Utils.str2Boolean(props.get("testWhileIdle"));
		if(testWhileIdle != null){
			dataSource.setTestWhileIdle(testWhileIdle);
		}
		
		String validationQuery = props.get("validationQuery");
		if(validationQuery != null && validationQuery.trim().length() > 0){
			dataSource.setValidationQuery(validationQuery);
		}
		String validatorClassName = props.get("validatorClassName");
		if(validatorClassName != null && validatorClassName.trim().length() > 0){
			dataSource.setValidatorClassName(validatorClassName);
		}
		Integer timeBetweenEvictionRunsMillis = Utils.strToInteger(props.get("timeBetweenEvictionRunsMillis"));
		if(timeBetweenEvictionRunsMillis != null){
			dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		}
		Integer numTestsPerEvictionRun = Utils.strToInteger(props.get("numTestsPerEvictionRun"));
		if(numTestsPerEvictionRun != null){
			dataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		}
		int minEvictableIdleTimeMillis = Utils.strToInt(props.get("minEvictableIdleTimeMillis"));
		if(minEvictableIdleTimeMillis >0){
			dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}

		Boolean accessToUnderlyingConnectionAllowed = Utils.str2Boolean(props.get("accessToUnderlyingConnectionAllowed"));
		if(accessToUnderlyingConnectionAllowed != null){
			dataSource.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
		}
		Boolean removeAbandoned = Utils.str2Boolean(props.get("removeAbandoned"));
		if(removeAbandoned != null){
			dataSource.setRemoveAbandoned(removeAbandoned);
		}
		int removeAbandonedTimeout = Utils.strToInt(props.get("removeAbandonedTimeout"));
		if(removeAbandonedTimeout >0){
			dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		}
		Boolean logAbandoned = Utils.str2Boolean(props.get("logAbandoned"));
		if(logAbandoned != null){
			dataSource.setLogAbandoned(logAbandoned);
		}
		
		String connectionProperties = props.get("connectionProperties");
		if(connectionProperties != null && connectionProperties.trim().length() > 0){
			dataSource.setConnectionProperties(connectionProperties);
		}
		String jdbcInterceptors = props.get("jdbcInterceptors");
		if(jdbcInterceptors != null && jdbcInterceptors.trim().length() > 0){
			dataSource.setJdbcInterceptors(jdbcInterceptors);
		}
		Integer validationInterval = Utils.strToInteger(props.get("validationInterval"));
		if(validationInterval != null){
			dataSource.setValidationInterval(validationInterval);
		}
		Boolean jmxEnabled = Utils.str2Boolean(props.get("jmxEnabled"));
		if(jmxEnabled != null){
			dataSource.setJmxEnabled(jmxEnabled);
		}
		Boolean fairQueue = Utils.str2Boolean(props.get("fairQueue"));
		if(fairQueue != null){
			dataSource.setFairQueue(fairQueue);
		}
		Integer abandonWhenPercentageFull = Utils.strToInteger(props.get("abandonWhenPercentageFull"));
		if(abandonWhenPercentageFull != null){
			dataSource.setAbandonWhenPercentageFull(abandonWhenPercentageFull);
		}
		Integer maxAge = Utils.strToInteger(props.get("maxAge"));
		if(maxAge != null){
			dataSource.setMaxAge(maxAge);
		}
		Boolean useEquals = Utils.str2Boolean(props.get("useEquals"));
		if(useEquals != null){
			dataSource.setUseEquals(useEquals);
		}
		Integer suspectTimeout = Utils.strToInteger(props.get("suspectTimeout"));
		if(suspectTimeout != null){
			dataSource.setSuspectTimeout(suspectTimeout);
		}
		Boolean alternateUsernameAllowed = Utils.str2Boolean(props.get("alternateUsernameAllowed"));
		if(alternateUsernameAllowed != null){
			dataSource.setAlternateUsernameAllowed(alternateUsernameAllowed);
		}
		return dataSource;
	}
}
