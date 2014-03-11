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
public class TransactionManager {
	private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
	
	private static ThreadLocal<Connection> localConnection = new ThreadLocal<Connection>();
	private static int defaultIsolationLevel = -999;
	private TransactionManager(){
		super();
	}
	
	/**
	 * get connection
	 * @param connectionFactory
	 * @return
	 * @throws SQLException 
	 */
	public static Connection getConnection(ConnectionFactory connectionFactory) throws SQLException{
		Connection connection = localConnection.get();
		if(connection == null){
			if (logger.isDebugEnabled()) {
				logger.debug("open connection.");
			}
			connection = connectionFactory.openConnection();
			if(defaultIsolationLevel == -999) {//init default
				defaultIsolationLevel = connection.getTransactionIsolation();
				return connection;
			}
		}
		if(defaultIsolationLevel != -999) {
			if(defaultIsolationLevel != connection.getTransactionIsolation()) {
				connection.setTransactionIsolation(defaultIsolationLevel);
			}
		}
		return connection;
	}
	
	/**
	 * close connection
	 */
	public static void closeConnection(Connection connection){
		if(localConnection.get() == null){
			if (logger.isDebugEnabled()) {
				logger.debug("close connection.");
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
		if(localConnection.get() == null){
			if (logger.isDebugEnabled()) {
				logger.debug("beginTransation.");
			}
			Connection connection = getConnection(connectionFactory);
			connection.setAutoCommit(false);
			if(isolationLevel != null) {
				connection.setTransactionIsolation(isolationLevel);
			}
			localConnection.set(connection);
		}
	}
	
	/**
	 * is transtion started
	 * @return
	 */
	public static boolean isManagedConnectionStarted(){
		return localConnection.get() != null;
	}
	
	/**
	 * commit
	 * @throws SQLException
	 */
	public static void commit() throws SQLException {
		final Connection connection = localConnection.get();
		if(connection != null){
			if (logger.isDebugEnabled()) {
				logger.debug("commitTransation.");
			}
			connection.commit();
		}
	}
	
	/**
	 * roll back
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		final Connection connection = localConnection.get();
		if(connection != null){
			if (logger.isDebugEnabled()) {
				logger.debug("rollbackTransation.");
			}
			connection.rollback();
		}
	}
	

	/**
	 * close connection
	 */
	public static void closeManagedConnection(){
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

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		closeManagedConnection();
		super.finalize();
	}
	
}
