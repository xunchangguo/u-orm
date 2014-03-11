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

import java.beans.PropertyVetoException;
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
public class C3p0DataSourceCreator implements IDataSourceCreator {

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.pool.IDataSourceCreator#createDatasource(org.uorm.dao.common.DatasourceConfig)
	 */
	@Override
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
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
