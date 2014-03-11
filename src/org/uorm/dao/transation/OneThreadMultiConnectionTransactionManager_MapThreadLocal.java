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
package org.uorm.dao.transation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.dao.common.ConnectionFactory;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class OneThreadMultiConnectionTransactionManager_MapThreadLocal {
	private static final Logger logger = LoggerFactory.getLogger(OneThreadMultiConnectionTransactionManager_MapThreadLocal.class);
	
	private static Map<String, ThreadLocal<Connection>> localConnectionMap = new ConcurrentHashMap<String, ThreadLocal<Connection>>();
//	private static ThreadLocal<Connection> localConnection = new ThreadLocal<Connection>();
	
	private OneThreadMultiConnectionTransactionManager_MapThreadLocal(){
		super();
	}
	
	/**
	 * get connection
	 * @param connectionFactory
	 * @return
	 * @throws SQLException 
	 */
	public static Connection getConnection(ConnectionFactory connectionFactory) throws SQLException{
		Connection connection = null;
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection == null){
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", open connection.");
			}
			connection = connectionFactory.openConnection();
		}else{
			connection = localConnection.get();
			if(connection == null){
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", open connection.");
				}
				connection = connectionFactory.openConnection();
			}
		}
		return connection;
	}
	
	/**
	 * close connection
	 */
	public static void closeConnection(Connection connection, ConnectionFactory connectionFactory){
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection == null || localConnection.get() == null){
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", close connection.");
			}
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("",e);
			}
			connection = null;
		}
	}
	
	/**
	 * start Managed Connection
	 * @param connectionFactory
	 * @throws SQLException 
	 */
	public static void startManagedConnection(ConnectionFactory connectionFactory, Integer isolationLevel) throws SQLException {
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection == null || localConnection.get() == null){
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", beginTransation.");
			}
			Connection connection = getConnection(connectionFactory);
			connection.setAutoCommit(false);
			if(isolationLevel != null) {
				connection.setTransactionIsolation(isolationLevel);
			}
			if(localConnection == null){
				localConnection = new ThreadLocal<Connection>();
				localConnection.set(connection);
				localConnectionMap.put(connectionFactory.toString(), localConnection);
			}else{
				localConnection.set(connection);
			}
		}
	}
	
	/**
	 * is transtion started
	 * @return
	 */
	public static boolean isManagedConnectionStarted(ConnectionFactory connectionFactory){
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection == null){
			return false;
		}else{
			return localConnection.get() != null;
		}
	}
	
	/**
	 * commit
	 * @throws SQLException
	 */
	public static void commit(ConnectionFactory connectionFactory) throws SQLException {
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection != null){
			final Connection connection = localConnection.get();
			if(connection != null){
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", commitTransation.");
				}
				connection.commit();
			}
		}
	}
	
	/**
	 * roll back
	 * @throws SQLException
	 */
	public static void rollback(ConnectionFactory connectionFactory) throws SQLException {
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection != null){
			final Connection connection = localConnection.get();
			if(connection != null){
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", rollbackTransation.");
				}
				connection.rollback();
			}
		}
	}
	

	/**
	 * close connection
	 */
	public static void closeManagedConnection(ConnectionFactory connectionFactory){
		ThreadLocal<Connection> localConnection = localConnectionMap.get(connectionFactory.toString());
		if(localConnection != null){
			final Connection connection = localConnection.get();
			if(connection != null){
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", close connection.");
				}
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("",e);
				} finally {
					localConnection.set(null);
				}
			}
		}
	}

	/**
	 */
	private void closeManagedConnection() {
		for(ThreadLocal<Connection> localConnection : localConnectionMap.values()){
			final Connection connection = localConnection.get();
			if(connection != null){
				if (logger.isDebugEnabled()) {
					logger.debug("close connection.");
				}
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("",e);
				} finally {
					localConnection.set(null);
				}
			}
		}
		localConnectionMap.clear();
		localConnectionMap = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		closeManagedConnection();
		super.finalize();
	}

	
}
