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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.DataBaseType;
import org.uorm.dao.common.nativejdbc.INativeJdbcExtractor;
import org.uorm.dao.dialect.AccessDialect;
import org.uorm.dao.dialect.DB2Dialect;
import org.uorm.dao.dialect.DefaultDialect;
import org.uorm.dao.dialect.DerbyDialect;
import org.uorm.dao.dialect.Dialect;
import org.uorm.dao.dialect.FirebirdDialect;
import org.uorm.dao.dialect.H2Dialect;
import org.uorm.dao.dialect.HSQLDialect;
import org.uorm.dao.dialect.InformixDialect;
import org.uorm.dao.dialect.Ingres10Dialect;
import org.uorm.dao.dialect.Ingres9Dialect;
import org.uorm.dao.dialect.IngresDialect;
import org.uorm.dao.dialect.InterbaseDialect;
import org.uorm.dao.dialect.MariadbDialect;
import org.uorm.dao.dialect.MySQLDialect;
import org.uorm.dao.dialect.Oracle10gDialect;
import org.uorm.dao.dialect.PostgreSQLDialect;
import org.uorm.dao.dialect.RDMSOS2200Dialect;
import org.uorm.dao.dialect.SQLServerDialect;
import org.uorm.dao.dialect.SqliteDialect;
import org.uorm.dao.dialect.SybaseDialect;
import org.uorm.dao.dialect.TimesTenDialect;
import org.uorm.dao.id.DefaultIdentifierGeneratorFactory;
import org.uorm.dao.id.IdentifierGeneratorFactory;
import org.uorm.dao.transation.TransactionManager;
import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.annotation.KeyGenertator;
import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.mapping.IObjectReader;
import org.uorm.utils.Assert;
import org.uorm.utils.PropertyHolderUtil;
import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class JdbcTemplate {
	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);
	
	private ConnectionFactory connectionFactory;
	private IObjectReader objectReader;
	private Dialect dialect = null;
	private IdentifierGeneratorFactory identifierGeneratorFactory = null;
	/**默认自动管理事务*/
	protected boolean autoManagerTransaction = true;
	
	private INativeJdbcExtractor nativeJdbcExtractor = null;
	private boolean inited = false;
	/**
	 * 
	 */
	public JdbcTemplate() {
		super();
		if("javassist".equalsIgnoreCase(PropertyHolderUtil.getProperty("uorm.bytecode.provider"))) {
			this.objectReader = new org.uorm.orm.bytecode.javassist.JavassistObjectReader();
		} else {
			this.objectReader = new org.uorm.orm.mapping.ObjectReader();
		}
		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
	}

	/**
	 * @param connectionFactory
	 */
	public JdbcTemplate(ConnectionFactory connectionFactory) {
		super();
		this.connectionFactory = connectionFactory;
		if("jdk".equalsIgnoreCase(PropertyHolderUtil.getProperty("uorm.bytecode.provider"))) {
			this.objectReader = new org.uorm.orm.mapping.ObjectReader();
		} else {
			this.objectReader = new org.uorm.orm.bytecode.javassist.JavassistObjectReader();
		}
		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
	}
	
	/**
	 * @param connectionFactory
	 * @param objectReader
	 */
	public JdbcTemplate(ConnectionFactory connectionFactory,
			IObjectReader objectReader) {
		super();
		this.connectionFactory = connectionFactory;
		this.objectReader = objectReader;
		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
	}

	public <T> T doExecute(ConnectionCallback<T> action) throws SQLException{
		Assert.notNull(action, "Callback object must not be null");
		Connection connection = TransactionManager.getConnection(connectionFactory);
		try {
			T result = action.doInConnection(connection);
			return result;
		} catch (SQLException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			TransactionManager.closeConnection(connection);
		}
	}
	
	protected <T> T doExecuteInTransation(ConnectionCallback<T> action) throws SQLException{
		Assert.notNull(action, "Callback object must not be null");
		TransactionManager.startManagedConnection(connectionFactory, null);
		Connection connection = TransactionManager.getConnection(connectionFactory);
		try {
			T result = action.doInConnection(connection);
			TransactionManager.commit();
			return result;
		} catch (SQLException ex) {
			TransactionManager.rollback();
			throw ex;
		} catch (RuntimeException ex) {
			TransactionManager.rollback();
			throw ex;
		} finally {
			TransactionManager.closeManagedConnection();
		}
	}
	
	public <T> T doExecute(StatementCallback<T> action) throws SQLException{
		Assert.notNull(action, "Callback object must not be null");
		Connection connection = TransactionManager.getConnection(connectionFactory);
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			T result = action.doInStatement(stmt);
			return result;
		} catch (SQLException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			TransactionManager.closeConnection(connection);
		}
	}
	/**
	 * 带参数的
	 * @param <T>
	 * @param action
	 * @param sql
	 * @param paramClass: 参数@param params参考的ORM class，参数@param params中定义的优先,如参数@param params中的OrmClass都已经定义，则此参数可给null
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	protected <T> T doExecute(StatementCallback<T> action, String sql, final Class<?> paramClass, SqlParameter... params) throws SQLException{
		Assert.notNull(action, "Callback object must not be null");
		Assert.notNull(sql, "sql must not be null");
		Connection connection = TransactionManager.getConnection(connectionFactory);
		PreparedStatement stmt = null;
		try {
			if (logger.isDebugEnabled()) {
				if(params != null && params.length >0){
					logger.debug("Executing SQL statement [" + sql + "] values: " + Arrays.asList(params));
				}else{
					logger.debug("Executing SQL statement [" + sql + "]");
				}
			}
			stmt = connection.prepareStatement(sql);
			int idx = 1;
			for(SqlParameter param : params) {
				int sqltype = 12;
				Map<String, FieldMapping> fieldMappings = null;
				if(param.getOrmClass() != null){
					fieldMappings = this.objectReader.getObjectFieldMap(param.getOrmClass());
				}else if(paramClass != null){
					fieldMappings = this.objectReader.getObjectFieldMap(paramClass);
				}
				if(fieldMappings != null){
					FieldMapping field = fieldMappings.get(param.getName());
					if(field == null) {
						//find by table column's name
						for (FieldMapping fieldl : fieldMappings.values()) {
							if(fieldl.columnName().equalsIgnoreCase(param.getName())){
								sqltype = fieldl.columnType();
								break;
							}
						}
					} else {
						sqltype = field.columnType();
					}

					Object val = param.getValue();
					if(val != null) {
						Class<?> targetsqlcls = getObjectReader().getTargetSqlClass(sqltype);
						if(targetsqlcls != null){
							Class<?> srcCls = val.getClass();
							if(GenericConverterFactory.getInstance().needConvert(srcCls, targetsqlcls)){
								ITypeConverter converter = GenericConverterFactory.getInstance().getSqlConverter();//.getConverter(srcCls, targetsqlcls);
								if(converter != null) {
									val = converter.convert(val, targetsqlcls);
								}
							}
						}
					}
//					stmt.setObject(idx, val, sqltype);
					//fix java.sql.SQLException: Unknown Types value
					stmt.setObject(idx, val);
				}else{
					stmt.setObject(idx, param.getValue());
				}
				idx ++;
			}
			T result = action.doInStatement(stmt);
			return result;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw new SQLException(e);
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			TransactionManager.closeConnection(connection);
		}
	}
	
	protected <T> T doExecuteInTransation(StatementCallback<T> action) throws SQLException{
		Assert.notNull(action, "Callback object must not be null");
		TransactionManager.startManagedConnection(connectionFactory, null);
		Connection connection = TransactionManager.getConnection(connectionFactory);
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			T result = action.doInStatement(stmt);
			TransactionManager.commit();
			return result;
		} catch (SQLException ex) {
			TransactionManager.rollback();
			throw ex;
		} catch (RuntimeException ex) {
			TransactionManager.rollback();
			throw ex;
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			TransactionManager.closeManagedConnection();
		}
	}
	
	/**
	 * 事务、带参数的
	 * @param <T>
	 * @param action
	 * @param sql
	 * @param paramClass: 参数@param params参考的ORM class，参数@param params中定义的优先,如参数@param params中的OrmClass都已经定义，则此参数可给null
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	protected <T> T doExecuteInTransation(StatementCallback<T> action, String sql, final Class<T> paramClass, SqlParameter... params) throws SQLException{
		Assert.notNull(action, "Callback object must not be null");
		Assert.notNull(sql, "sql must not be null");
		TransactionManager.startManagedConnection(connectionFactory, null);
		Connection connection = TransactionManager.getConnection(connectionFactory);
		PreparedStatement stmt = null;
		try {
			if (logger.isDebugEnabled()) {
				if(params != null && params.length >0){
					logger.debug("Executing SQL statement [" + sql + "] values: " + Arrays.asList(params));
				}else{
					logger.debug("Executing SQL statement [" + sql + "]");
				}
			}
			stmt = connection.prepareStatement(sql);
			int idx = 1;
			for(SqlParameter param : params) {
				int sqltype = 12;
				Map<String, FieldMapping> fieldMappings = null;
				if(param.getOrmClass() != null){
					fieldMappings = this.objectReader.getObjectFieldMap(param.getOrmClass());
				}else if(paramClass != null){
					fieldMappings = this.objectReader.getObjectFieldMap(paramClass);
				}
				if(fieldMappings != null){
					FieldMapping field = fieldMappings.get(param.getName());
					if(field == null) {
						//find by table column's name
						for (FieldMapping fieldl : fieldMappings.values()) {
							if(fieldl.columnName().equalsIgnoreCase(param.getName())){
								sqltype = fieldl.columnType();
								break;
							}
						}
					} else {
						sqltype = field.columnType();
					}

					Object val = param.getValue();
					if(val != null) {
						Class<?> targetsqlcls = getObjectReader().getTargetSqlClass(sqltype);
						if(targetsqlcls != null){
							Class<?> srcCls = val.getClass();
							if(GenericConverterFactory.getInstance().needConvert(srcCls, targetsqlcls)){
								ITypeConverter converter = GenericConverterFactory.getInstance().getSqlConverter();//.getConverter(srcCls, targetsqlcls);
								if(converter != null) {
									val = converter.convert(val, targetsqlcls);
								}
							}
						}
					}
//					stmt.setObject(idx, val, sqltype);
					//fix java.sql.SQLException: Unknown Types value
					stmt.setObject(idx, val);
				}else{
					stmt.setObject(idx, param.getValue());
				}
				idx ++;
			}
			T result = action.doInStatement(stmt);
			TransactionManager.commit();
			return result;
		} catch (SQLException ex) {
			TransactionManager.rollback();
			throw ex;
		} catch (Exception ex) {
			TransactionManager.rollback();
			throw new SQLException(ex);
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			TransactionManager.closeManagedConnection();
		}
	}
	
	public boolean execute(final String sql, final SqlParameter... params) throws SQLException {
		class ExecuteStatementCallback implements StatementCallback<Boolean> {
			/* (non-Javadoc)
			 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
			 */
			@Override
			public Boolean doInStatement(Statement stmt) throws SQLException {
				if (logger.isDebugEnabled()) {
					if(params != null && params.length >0){
						logger.debug("Executing SQL statement [" + sql + "] values: " + Arrays.asList(params));
					}else{
						logger.debug("Executing SQL statement [" + sql + "]");
					}
				}
				if(stmt instanceof PreparedStatement){
					return ((PreparedStatement)stmt).execute();
				}else{
					return stmt.execute(sql);
				}
			}
		}
		if(params == null || params.length == 0){
			if ( autoManagerTransaction ) {
				return doExecuteInTransation(new ExecuteStatementCallback());
			} else {
				return doExecute(new ExecuteStatementCallback());
			}
		}else{
			if ( autoManagerTransaction ) {
				return doExecuteInTransation(new ExecuteStatementCallback(), sql, null, params);
			} else {
				return doExecute(new ExecuteStatementCallback(), sql, null, params);
			}
		}
	}
	
	public <T> T execute(ConnectionCallback<T> action) throws SQLException {
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(action);
		} else {
			return doExecute(action);
		}
	}
	
	public <T> T execute(StatementCallback<T> action) throws SQLException {
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(action);
		} else {
			return doExecute(action);
		}
	}
	
	/**
	 * query
	 * @param <T>
	 * @param sql
	 * @param rse
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T> T query(final String sql, final ResultSetExtractor<T> rse, final SqlParameter... params) throws SQLException {
		Assert.notNull(sql, "SQL must not be null");
		Assert.notNull(rse, "ResultSetExtractor must not be null");
		class QueryStatementCallback implements StatementCallback<T> {

			/* (non-Javadoc)
			 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
			 */
			@Override
			public T doInStatement(Statement stmt) throws SQLException {
				ResultSet rs = null;
				try {
					if (logger.isDebugEnabled()) {
						if(params != null && params.length >0){
							logger.debug("Executing SQL query [" + sql + "] values: " + Arrays.asList(params));
						}else{
							logger.debug("Executing SQL query [" + sql + "]");
						}
					}
					if(stmt instanceof PreparedStatement){
						rs = ((PreparedStatement)stmt).executeQuery();
					}else{
						rs = stmt.executeQuery(sql);
					}
					return rse.extractData(rs);
				} finally {
					JdbcUtils.closeResultSet(rs);
				}
			}
		}
		if(params == null || params.length == 0){
			return doExecute(new QueryStatementCallback());
		}else{
			return doExecute(new QueryStatementCallback(), sql, null, params);
		}
	}
	
	/**
	 * query
	 * @param <T>
	 * @param sql
	 * @param rse
	 * @param paramClass: 参数@param params参考的ORM class，参数@param params中定义的优先
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T> T query(final String sql, final ResultSetExtractor<T> rse, Class<?> paramClass, final SqlParameter... params) throws SQLException {
		if(params == null || params.length == 0){
			return query(sql, rse);
		}else{
			Assert.notNull(sql, "SQL must not be null");
			Assert.notNull(rse, "ResultSetExtractor must not be null");
			class QueryStatementCallback implements StatementCallback<T> {

				/* (non-Javadoc)
				 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
				 */
				@Override
				public T doInStatement(Statement stmt) throws SQLException {
					ResultSet rs = null;
					try {
						if (logger.isDebugEnabled()) {
//							if(params != null && params.length >0){
//								logger.debug("Executing SQL query [" + sql + "] values: " + Arrays.asList(params));
//							}else{
//								logger.debug("Executing SQL query [" + sql + "]");
//							}
						}
						if(stmt instanceof PreparedStatement){
							rs = ((PreparedStatement)stmt).executeQuery();
						}else{
							rs = stmt.executeQuery(sql);
						}
						return rse.extractData(rs);
					} finally {
						JdbcUtils.closeResultSet(rs);
					}
				}
			}
			return doExecute(new QueryStatementCallback(), sql, paramClass, params);
		}
	}
	
	/**
	 * 查询返回单个Map
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> queryForMap(String sql, final SqlParameter... params) throws SQLException {
		List<Map<String, Object>> results = null;
		if(params == null || params.length == 0){
			results = query(sql, new RowMapResultSetExtractor(objectReader, 1));
		}else{
			results = query(sql, new RowMapResultSetExtractor(objectReader, 1), null, params);
		}
		if ( results != null && results.size() > 0 ) {
			return results.get(0);
		}
		return null;
	}
	
	/**
	 * 查询返回单条记录
	 * @param sql
	 * @param params
	 * @return Object[]
	 * @throws SQLException
	 */
	public Object[] queryForArray(String sql, SqlParameter... params) throws SQLException {
		List<Object[]> results = null;
		if(params == null || params.length == 0){
			results = query(sql, new RowArrayResultSetExtractor(objectReader, 1));
		}else{
			results = query(sql, new RowArrayResultSetExtractor(objectReader, 1), null, params);
		}
		if ( results != null && results.size() > 0 ) {
			return results.get(0);
		}
		return null;
	}
	
	/**
	 * 查询返回单个对象
	 * @param <T>
	 * @param cls
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T> T queryForObject(Class<T> cls, String sql, final SqlParameter... params) throws SQLException {
		RowsResultSetExtractor<T> extractor = new RowsResultSetExtractor<T>(objectReader, cls);
		extractor.setMax(1);
		List<T> results = null;
		if(params == null || params.length == 0){
			results = query(sql, extractor);
		}else{
			results = query(sql, extractor, cls, params);
		}
		if(results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * 查询
	 * @param sql：sql查询语句
	 * @param params:参数
	 * @return：List<Map<String, Object>>
	 * @throws SQLException
	 */
	public List<Map<String, Object>> queryForList(String sql, final SqlParameter... params) throws SQLException {
		if(params == null || params.length == 0){
			return query(sql, new RowMapResultSetExtractor(objectReader));
		}else{
			return query(sql, new RowMapResultSetExtractor(objectReader), null, params);
		}
	}
	
	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return List<Object[]>
	 * @throws SQLException
	 */
	public List<Object[]> queryForListArray(String sql, final SqlParameter... params) throws SQLException {
		if(params == null || params.length == 0){
			return query(sql, new RowArrayResultSetExtractor(objectReader));
		}else{
			return query(sql, new RowArrayResultSetExtractor(objectReader), null, params);
		}
	}
	
	/**
	 * 查询
	 * @param <T>
	 * @param cls
	 * @param sql：sql查询语句
	 * @param params：参数
	 * @return：List<T>
	 * @throws SQLException
	 */
	public <T> List<T> queryForList(final Class<T> cls, final String sql, final SqlParameter... params) throws SQLException {
		if(params == null || params.length == 0){
			return query(sql, new RowsResultSetExtractor<T>(objectReader, cls));
		}else{
			return query(sql, new RowsResultSetExtractor<T>(objectReader, cls), cls, params);
		}
	}
	
	/**
	 * 更新
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int update(final String sql, final SqlParameter... params) throws SQLException {
		Assert.notNull(sql, "SQL must not be null");
		class UpdateStatementCallback implements StatementCallback<Integer> {

			/* (non-Javadoc)
			 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
			 */
			@Override
			public Integer doInStatement(Statement stmt) throws SQLException {
				if (logger.isDebugEnabled()) {
					if(params != null && params.length >0){
						logger.debug("Executing SQL update [" + sql + "] values: " + Arrays.asList(params));
					}else{
						logger.debug("Executing SQL update [" + sql + "]");
					}
				}
				if(stmt instanceof PreparedStatement){
					return ((PreparedStatement)stmt).executeUpdate();
				}else{
					return stmt.executeUpdate(sql);
				}
			}
			
		}
		if(params == null || params.length == 0){
			if ( autoManagerTransaction ) {
				return doExecuteInTransation(new UpdateStatementCallback());
			} else {
				return doExecute(new UpdateStatementCallback());
			}
		}else{
			if ( autoManagerTransaction ) {
				return doExecuteInTransation(new UpdateStatementCallback(), sql, null, params);
			} else {
				return doExecute(new UpdateStatementCallback(), sql, null, params);
			}
		}
	}
	
	/**
	 * 批量更新
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int[] batchUpdate(final String[] sql) throws SQLException {
		Assert.notEmpty(sql, "SQL array must not be empty");
		class BatchUpdateStatementCallback implements StatementCallback<int[]> {

			/* (non-Javadoc)
			 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
			 */
			@Override
			public int[] doInStatement(Statement stmt) throws SQLException {
				int[] rowsAffected = new int[sql.length];
				for (String sqlStmt : sql) {
					stmt.addBatch(sqlStmt);
				}
				rowsAffected = stmt.executeBatch();
				return rowsAffected;
			}
			
		}
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(new BatchUpdateStatementCallback());
		} else {
			return doExecute(new BatchUpdateStatementCallback());
		}
	}
	
	/**
	 * @return the connectionFactory
	 */
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * @param connectionFactory the connectionFactory to set
	 */
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	/**
	 * @return the objectReader
	 */
	public IObjectReader getObjectReader() {
		return objectReader;
	}

	/**
	 * @param objectReader the objectReader to set
	 */
	public void setObjectReader(IObjectReader objectReader) {
		this.objectReader = objectReader;
	}

	/**
	 * @return the identifierGeneratorFactory
	 */
	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
		return identifierGeneratorFactory;
	}

	/**
	 * @param identifierGeneratorFactory the identifierGeneratorFactory to set
	 */
	public void setIdentifierGeneratorFactory(
			IdentifierGeneratorFactory identifierGeneratorFactory) {
		this.identifierGeneratorFactory = identifierGeneratorFactory;
	}

	/**
	 * @return the dialect
	 */
	public Dialect getDialect() {
		if(dialect == null) {
			dialect = genDialect(this.connectionFactory.getConfiguration().getDatabasetype(), this.connectionFactory.getConfiguration().getDialectClass());
		}
		return dialect;
	}
	
	/**
	 * 是否是oralce
	 * @return
	 */
	public boolean isOracle() {
		return DataBaseType.ORACLE == this.connectionFactory.getConfiguration().getDatabasetype();
	}
	
	/**
	 * get native jdbc extractor
	 * @return
	 */
	public INativeJdbcExtractor getNativeJdbcExtractor() {
		if(!inited){
			inited = true;
			initNativeJdbcExtractor();
		}
		return this.nativeJdbcExtractor;
	}
	
	/**
	 * init native Jdbc Extractor
	 * 2013-1-18 下午4:27:27
	 */
	protected void initNativeJdbcExtractor() {
		String pooltype = this.connectionFactory.getConfiguration().getPoolType();
		if(pooltype != null){
			if(DefaultConnectionFactory._POOL_TYPE_C3P0.equals(pooltype)){
				this.nativeJdbcExtractor = new org.uorm.dao.common.nativejdbc.C3P0NativeJdbcExtractor();
			}
		}
	}
	
	protected Connection getOracleNatveJdbcConnection(Connection con) throws SQLException {
		if(isOracle()) {
			if(getNativeJdbcExtractor() != null) {
				return getNativeJdbcExtractor().doGetNativeConnection(con);
			}
		}
		return con;
	}
	
	/**
	 * generate sql dialect
	 * @param databasetype
	 * @param dialectClass
	 * @return
	 */
	private Dialect genDialect(DataBaseType databasetype, String dialectClass) {
		Dialect dialect = null;
		if( dialectClass == null || dialectClass.length() == 0 ) {
			switch (databasetype) {
			case DB2:
				dialect = new DB2Dialect();
				break;
			case H2:
				dialect = new H2Dialect();
				break;
			case HSQL:
				dialect = new HSQLDialect();
				break;
			case MYSQL:
				dialect = new MySQLDialect();
				break;
			case ORACLE:
				dialect = new Oracle10gDialect();
				break;
			case PSQL:
				dialect = new PostgreSQLDialect();
				break;
			case DERBY:
				dialect = new DerbyDialect();
				break;
			case SQLSERVER:
				dialect = new SQLServerDialect();
				break;
			case FIREBIRD:
				dialect = new FirebirdDialect();
				break;
			case INTERBASE:
				dialect = new InterbaseDialect();
				break;
			case INFORMIX:
				dialect = new InformixDialect();
				break;
			case INGRES:
				dialect = new IngresDialect();
				break;
			case INGRES9:
				dialect = new Ingres9Dialect();
				break;
			case INGRES10:
				dialect = new Ingres10Dialect();
				break;
			case RDMS2200:
				dialect = new RDMSOS2200Dialect();
				break;
			case TIMESTEN:
				dialect = new TimesTenDialect();
				break;
			case SQLITE:
				dialect = new SqliteDialect();
				break;
			case ACCESS:
				dialect = new AccessDialect();
				break;
			case MARIADB:
				dialect = new MariadbDialect();
				break;
			case SYBASE:
				dialect = new SybaseDialect();
				break;
			default:
				dialect = new DefaultDialect();
				break;
			}
		} else {
			dialect = constructDialect(dialectClass);
		}
		return dialect;
	}
	
	/**
	 * 初始化SQL方言类
	 * @param typeClass
	 * @return
	 */
	private Dialect constructDialect(String typeClass) {
    	Class<?> _class;
		try {
			_class = Class.forName(typeClass);
		} catch (ClassNotFoundException e) {
			logger.error("",e);
			return null;
		}
		try {
			Object insObject = _class.newInstance();
			if(insObject instanceof Dialect){
				return (Dialect)insObject;
			}
		} catch (InstantiationException e) {
			logger.error("",e);
		} catch (IllegalAccessException e) {
			logger.error("",e);
		}
		return null;
	}
	
	/**
	 * 生成insert语句
	 * @param cls
	 * @return
	 */
	protected String generateInsertSql(Class<?> cls) {
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		String tableName = null;
		if ( clsmapping != null ){
			tableName = clsmapping.tableName();
			if(clsmapping.pattern() != null && clsmapping.pattern().length() > 0) {
//				if(clsmapping.patternField() != null && clsmapping.patternField().length() > 0) {
//					tableName += "%s";
//				} else {
				tableName += Utils.dateFormat(new Date(), clsmapping.pattern());
//				}
			}
		}
		if(tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		Map<String, FieldMapping> fieldMappings = getObjectReader().getObjectFieldMap(cls);
		sql.append(tableName).append('(');
		int count = 0;
		for (FieldMapping field : fieldMappings.values()) {
			if(field.includeInWrites()){
				if( (field.primary())
						&& (clsmapping != null) 
						&& (KeyGenertator.NATIVE.equals(clsmapping.keyGenerator())) ){
					//if native 
					continue;
				}
				if(count == 0){
					sql.append(field.columnName());
				}else{
					sql.append(',').append(field.columnName());
				}
				count ++;
			}
		}
		sql.append(") VALUES(");
		for(int i = 0; i < count; i ++) {
			if(i == 0){
				sql.append('?');
			}else{
				sql.append(", ?");
			}
		}
		sql.append(')');
		return sql.toString();
	}
	
	protected UpdateSqlInfo generateUpdateSql(Class<?> cls, Object pojo) throws SQLException {
		UpdateSqlInfo updateSqlInfo = new UpdateSqlInfo();
		StringBuffer sql = new StringBuffer("UPDATE ");
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		String tableName = null;
		String keyOrder = null;
		if ( clsmapping != null ){
			tableName = clsmapping.tableName();
			keyOrder = clsmapping.keyOrder();
		}
		if(tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		sql.append(tableName);
		Map<String, FieldMapping> fieldMappings = getObjectReader().getObjectFieldMap(cls);
		Map<String, Object> datas = null;
		if(pojo != null){
			//filter null
			try {
				datas = this.objectReader.readValue2Map(pojo);
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		int count = 0;
		for (FieldMapping field : fieldMappings.values()) {
			if( (field.includeInUpdate()) && (!field.primary()) ){
				if(datas != null){
					//filter null
					if(null != datas.get(field.columnName())){
						if(count == 0){
							sql.append(" SET ").append(field.columnName()).append(" = ?");
						}else{
							sql.append(", ").append(field.columnName()).append(" = ?");
						}
						updateSqlInfo.addParameter(field);
						count ++;
					}
				}else{
					if(count == 0){
						sql.append(" SET ").append(field.columnName()).append(" = ?");
					}else{
						sql.append(", ").append(field.columnName()).append(" = ?");
					}
					updateSqlInfo.addParameter(field);
					count ++;
				}
			}
		}
		FieldMapping[] pkfields = this.objectReader.getClassPrimaryKeys(cls, keyOrder);
		if(pkfields != null && pkfields.length > 0){
			int idx = 0;
			for(FieldMapping pkfield : pkfields){
				if(idx == 0){
					sql.append(" WHERE ").append(pkfield.columnName()).append(" = ?");
				}else{
					sql.append(" AND ").append(pkfield.columnName()).append(" = ?");
				}
				updateSqlInfo.addParameter(pkfield);
				idx ++;
			}
		}
		updateSqlInfo.setSql(sql.toString());
		return updateSqlInfo;
	}
	
	protected UpdateSqlInfo generateDeleteSql(Class<?> cls) throws SQLException {
		UpdateSqlInfo updateSqlInfo = new UpdateSqlInfo();
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		StringBuffer sql = new StringBuffer("DELETE FROM ");
		String tableName = null;
		if ( clsmapping != null ){
			tableName = clsmapping.tableName();
		}
		if(tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		sql.append(tableName).append(" WHERE ");
		FieldMapping[] pkfields = getObjectReader().getClassPrimaryKeys(cls, clsmapping.keyOrder());
		if(pkfields == null) {
			throw new IllegalArgumentException("can not find the primary key infomation.");
		}
		int idx = 0;
		for ( FieldMapping field : pkfields ) {
			updateSqlInfo.addParameter(field);
			if ( idx == 0 ){
				sql.append(field.columnName()).append(" = ?");
			} else {
				sql.append(" and ").append(field.columnName()).append(" = ?");
			}
			idx ++;
		}
		updateSqlInfo.setSql(sql.toString());
		return updateSqlInfo;
	}

	public boolean isAutoManagerTransaction() {
		return autoManagerTransaction;
	}

	public void setAutoManagerTransaction(boolean autoManagerTransaction) {
		this.autoManagerTransaction = autoManagerTransaction;
	}
}
