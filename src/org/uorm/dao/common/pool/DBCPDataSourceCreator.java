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
public class DBCPDataSourceCreator implements IDataSourceCreator {

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.pool.IDataSourceCreator#createDatasource(org.uorm.dao.common.DatasourceConfig)
	 */
	@Override
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
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

}
