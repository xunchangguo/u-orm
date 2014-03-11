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
public class BoneCPDataSourceCreator implements IDataSourceCreator {

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.pool.IDataSourceCreator#createDatasource(org.uorm.dao.common.DatasourceConfig)
	 */
	@Override
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
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

}
