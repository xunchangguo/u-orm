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

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class DataSourceCreator {
//	private static Set<String> driverloaded = new HashSet<String>();

	public static DataSource createDatasource(String pooltype, DatasourceConfig datasourcecfg) throws SQLException {
		return genPoolDataSource(pooltype, datasourcecfg);
	}

	/**
	 * 生成连接池的Datasource
	 * @param pooltype
	 * @param datasourcecfg
	 * @return
	 * @throws SQLException 
	 */
	private static DataSource genPoolDataSource(String pooltype, DatasourceConfig datasourcecfg) throws SQLException {
		DataSource datasource = null;
		if ( DefaultConnectionFactory._POOL_TYPE_C3P0.equalsIgnoreCase(pooltype) ) {
			datasource = createC3p0DataSource(datasourcecfg);
		} else if ( DefaultConnectionFactory._POOL_TYPE_BONECP.equalsIgnoreCase(pooltype) ) {
			datasource = createBoneCPDataSource(datasourcecfg);
		} else if ( DefaultConnectionFactory._POOL_TYPE_DBCP.equalsIgnoreCase(pooltype) ) {
			datasource = createDBCPDataSource(datasourcecfg);
		}else if ( DefaultConnectionFactory._POOL_TYPE_JDBC_POOL.equalsIgnoreCase(pooltype) ) {
			Boolean XA = Utils.str2Boolean(datasourcecfg.getPoolPerperties().get("isXADataSource"));
			if(XA != null){
				datasource = createJdbcPoolDataSource(datasourcecfg, XA);
			}else{
				datasource = createJdbcPoolDataSource(datasourcecfg, false);
			}
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

	/**
	 * create DBCP DataSource
	 * @param datasourcecfg
	 * @return
	 */
	private static DataSource createDBCPDataSource(
			DatasourceConfig datasourcecfg) {
		org.apache.commons.dbcp.BasicDataSource dataSource = new org.apache.commons.dbcp.BasicDataSource();
		dataSource.setDriverClassName(datasourcecfg.getDriverClass());
		dataSource.setUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map<String, String> props = datasourcecfg.getPoolPerperties();
		
		String connectionProperties = props.get("connectionProperties");
		if(connectionProperties != null && connectionProperties.trim().length() > 0){
			dataSource.setConnectionProperties(connectionProperties);
		}
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
		
		int initialSize = Utils.strToInt(props.get("initialSize"));
		if(initialSize >0){
			dataSource.setInitialSize(initialSize);
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
		int maxWait = Utils.strToInt(props.get("maxWait"));
		if(maxWait >0){
			dataSource.setMaxWait(maxWait);
		}
		
		String validationQuery = props.get("validationQuery");
		if(validationQuery != null && validationQuery.trim().length() > 0){
			dataSource.setValidationQuery(validationQuery);
		}
		Integer validationQueryTimeout = Utils.strToInteger(props.get("validationQueryTimeout"));
		if(validationQueryTimeout != null){
			dataSource.setValidationQueryTimeout(validationQueryTimeout);
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
		return dataSource;
	}

	/**
	 * create BoneCP DataSource
	 * @param datasourcecfg
	 * @return
	 */
	private static DataSource createBoneCPDataSource(DatasourceConfig datasourcecfg) {
		com.jolbox.bonecp.BoneCPDataSource dataSource = new com.jolbox.bonecp.BoneCPDataSource();
		dataSource.setDriverClass(datasourcecfg.getDriverClass());
		dataSource.setJdbcUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map<String, String> props = datasourcecfg.getPoolPerperties();
		int idleConTestPeriod = Utils.strToInt(props.get("idleConnectionTestPeriod"));
		if ( idleConTestPeriod <= 0 ) {
			idleConTestPeriod = 1;
		}
		dataSource.setIdleConnectionTestPeriod(idleConTestPeriod);
		int idleMaxAge = Utils.strToInt(props.get("idleMaxAge"));
		if ( idleMaxAge <= 0 ) {
			idleMaxAge = 40;
		}
		dataSource.setIdleMaxAge(idleMaxAge);
		int maxConnectionsPerPartition = Utils.strToInt(props.get("maxConnectionsPerPartition"));
		if ( maxConnectionsPerPartition <= 0 ) {
			maxConnectionsPerPartition = 20;
		}
		dataSource.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
		int minConnectionsPerPartition = Utils.strToInt(props.get("minConnectionsPerPartition"));
		if ( minConnectionsPerPartition <= 0 ) {
			minConnectionsPerPartition = 5;
		}
		dataSource.setMinConnectionsPerPartition(minConnectionsPerPartition);
		int partitionCount = Utils.strToInt(props.get("partitionCount"));
		if ( partitionCount <= 0 ) {
			partitionCount = 3;
		}
		dataSource.setPartitionCount(partitionCount);
		int acquireIncrement = Utils.strToInt(props.get("acquireIncrement"));
		if ( acquireIncrement <= 0 ) {
			acquireIncrement = 2;
		}
		dataSource.setAcquireIncrement(acquireIncrement);
		int acquireRetryAttempts = Utils.strToInt(props.get("acquireRetryAttempts"));
		if ( acquireRetryAttempts <= 0 ) {
			acquireRetryAttempts = 10;
		}
		dataSource.setAcquireRetryAttempts(acquireRetryAttempts);
		int acquireRetryDelay = Utils.strToInt(props.get("acquireRetryDelay"));
		if ( acquireRetryDelay <= 0 ) {
			acquireRetryDelay = 500;
		}
		dataSource.setAcquireRetryDelay(acquireRetryDelay);
		int connectionTimeout = Utils.strToInt(props.get("connectionTimeout"));
		if ( connectionTimeout <= 0 ) {
			connectionTimeout = 5000;
		}
		dataSource.setConnectionTimeout(connectionTimeout);
		int statementsCacheSize = Utils.strToInt(props.get("statementsCacheSize"));
		if ( statementsCacheSize <= 0 ) {
			statementsCacheSize = 50;
		}
		dataSource.setStatementsCacheSize(statementsCacheSize);
		int releaseHelperThreads = Utils.strToInt(props.get("releaseHelperThreads"));
		if ( releaseHelperThreads <= 0 ) {
			releaseHelperThreads = 3;
		}
		dataSource.setReleaseHelperThreads(releaseHelperThreads);
		return dataSource;
	}

	private static DataSource createC3p0DataSource (DatasourceConfig datasourcecfg) throws SQLException {
//		if(!driverloaded.contains(datasourcecfg.getDriverClass())){
//			try {
//				Class.forName(datasourcecfg.getDriverClass());
//				driverloaded.add(datasourcecfg.getDriverClass());
//			} catch (ClassNotFoundException e) {
//				throw new SQLException("can not load driver", e);
//			}
//		}
//		DataSource ds = com.mchange.v2.c3p0.DataSources.unpooledDataSource(datasourcecfg.getJdbcUrl(), datasourcecfg.getUsername(), datasourcecfg.getPassword());
//		DataSource dataSource = com.mchange.v2.c3p0.DataSources.pooledDataSource(ds);
		com.mchange.v2.c3p0.ComboPooledDataSource dataSource = new com.mchange.v2.c3p0.ComboPooledDataSource();
		try {
			dataSource.setDriverClass(datasourcecfg.getDriverClass());
		} catch (PropertyVetoException e) {
			throw new SQLException(e);
		}
		dataSource.setJdbcUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUser(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map<String, String> props = datasourcecfg.getPoolPerperties();
		int checkoutTimeout = Utils.strToInt(props.get("checkoutTimeout"));
		if( checkoutTimeout <= 0) {
			checkoutTimeout = 5000;
		}
		dataSource.setCheckoutTimeout(checkoutTimeout);
		int initPoolSize = Utils.strToInt(props.get("initialPoolSize"));
		if(initPoolSize > 0){
			dataSource.setInitialPoolSize(initPoolSize);
		}
		int minpoolsize = Utils.strToInt(props.get("minPoolSize"));
		if(minpoolsize <= 0) {
			minpoolsize = 5;
		}
		dataSource.setMinPoolSize(minpoolsize);
		int maxpoolsize = Utils.strToInt(props.get("maxPoolSize"));
		if(maxpoolsize <= 0) {
			maxpoolsize = 100;
		}
		dataSource.setMaxPoolSize(maxpoolsize);
		int acqInc = Utils.strToInt(props.get("acquireIncrement"));
		if(acqInc <= 0) {
			acqInc = 5;
		}
		dataSource.setAcquireIncrement(acqInc);
		int maxIdleTime = Utils.strToInt(props.get("maxIdleTime"));
		if ( maxIdleTime > 0 ) {
			dataSource.setMaxIdleTime( maxIdleTime );
		}
		int idleConTestPeriod = Utils.strToInt(props.get("idleConnectionTestPeriod"));
		if ( idleConTestPeriod > 0 ) {
			dataSource.setIdleConnectionTestPeriod(idleConTestPeriod);
		}
		int acqRetryAttempts = Utils.strToInt(props.get("acquireRetryAttempts"));
		if ( acqRetryAttempts <= 0 ) {
			acqRetryAttempts = 10;
		}
		dataSource.setAcquireRetryAttempts(acqRetryAttempts);
		int acqRetryDelay = Utils.strToInt(props.get("acquireRetryDelay"));
		if ( acqRetryDelay <= 0 ) {
			acqRetryDelay = 500;
		}
		dataSource.setAcquireRetryDelay(acqRetryDelay);
		return dataSource;
	}
}
