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
import java.util.HashMap;
import java.util.Map;

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
public class OneThreadMultiConnectionTransactionManager {
	private static final Logger logger = LoggerFactory.getLogger(OneThreadMultiConnectionTransactionManager.class);
	
	private static ThreadLocal<Map<String, Connection>> localConnectionMap = new ThreadLocal<Map<String,Connection>>();
//	private static ThreadLocal<Connection> localConnection = new ThreadLocal<Connection>();
//	private static Map<String, Integer> defaultIsolationLevelMap = new HashMap<String, Integer>();
	
	private OneThreadMultiConnectionTransactionManager(){
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
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection == null){
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", open connection.");
			}
			connection = connectionFactory.openConnection();
		}else{
			connection = localConnection.get(connectionFactory.toString());
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
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection == null || localConnection.get(connectionFactory.toString()) == null){
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
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection == null || localConnection.get(connectionFactory.toString()) == null){
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", beginTransation.");
			}
			Connection connection = getConnection(connectionFactory);
			connection.setAutoCommit(false);
			if(isolationLevel != null) {
				connection.setTransactionIsolation(isolationLevel);
			}
			if(localConnection == null){
				localConnection = new HashMap<String, Connection>();
				localConnection.put(connectionFactory.toString(), connection);
				localConnectionMap.set(localConnection);
			}else{
				localConnection.put(connectionFactory.toString(), connection);
			}
		}
	}
	
	/**
	 * is transtion started
	 * @return
	 */
	public static boolean isManagedConnectionStarted(ConnectionFactory connectionFactory){
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection == null){
			return false;
		}else{
			return localConnection.get(connectionFactory.toString()) != null;
		}
	}
	
	/**
	 * commit
	 * @throws SQLException
	 */
	public static void commit(ConnectionFactory connectionFactory) throws SQLException {
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection != null){
			final Connection connection = localConnection.get(connectionFactory.toString());
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
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection != null){
			final Connection connection = localConnection.get(connectionFactory.toString());
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
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection != null){
			final Connection connection = localConnection.remove(connectionFactory.toString());
			if(connection != null){
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration().getJdbcUrl() + ", close connection.");
				}
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("",e);
				} finally {
					if(localConnection.isEmpty()) {
						localConnectionMap.set(null);
					}
				}
			}
		}
	}

	/**
	 */
	private void closeManagedConnection() {
		Map<String, Connection> localConnection = localConnectionMap.get();
		if(localConnection != null && !localConnection.isEmpty()) {
			for (Connection connection : localConnection.values()) {
				if (logger.isDebugEnabled()) {
					logger.debug("close connection.");
				}
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("",e);
				}
			}
		}
		localConnection.clear();
		localConnectionMap.set(null);
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
