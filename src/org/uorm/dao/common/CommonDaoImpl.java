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

import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.dao.id.IdentifierGenerator;
import org.uorm.dao.transation.TransactionManager;
import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.annotation.KeyGenertator;
import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.mapping.AsciiStream;
import org.uorm.orm.mapping.IObjectReader;
import org.uorm.utils.Assert;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class CommonDaoImpl extends JdbcTemplate implements ICommonDao {
	private static final Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);
	public static final int _BATCH_SIZE = 50;
	private int batchSize = _BATCH_SIZE;
	
	public CommonDaoImpl() {
		super();
	}

	/**
	 * @param connectionFactory
	 */
	public CommonDaoImpl(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	/**
	 * @param connectionFactory
	 * @param objectReader
	 */
	public CommonDaoImpl(ConnectionFactory connectionFactory,
			IObjectReader objectReader) {
		super(connectionFactory, objectReader);
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#beginTransation()
	 */
	@Override
	public void beginTransation() throws SQLException {
		TransactionManager.startManagedConnection(getConnectionFactory(), null);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#commitTransation()
	 */
	@Override
	public void commitTransation() throws SQLException {
		try{
			TransactionManager.commit();
		} finally {
			TransactionManager.closeManagedConnection();
		}
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#rollbackTransation()
	 */
	@Override
	public void rollbackTransation() throws SQLException {
		try{
			TransactionManager.rollback();
		} finally {
			TransactionManager.closeManagedConnection();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryBusinessObjByPk(java.lang.Class, java.io.Serializable[])
	 */
	@Override
	public <T> T queryBusinessObjByPk(Class<T> cls, Serializable... pkvals)
			throws SQLException {
		Assert.notEmpty(pkvals);
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		if(pkvals.length > 1 && clsmapping == null){
			throw new IllegalArgumentException("too many primary key values, can not find the key order.");
		}
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
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
		SqlParameter[] params = new SqlParameter[pkfields.length];
		for ( FieldMapping field : pkfields ) {
			params[idx] = new SqlParameter(field.columnName(), pkvals[idx]);
			if ( idx == 0 ){
				sql.append(field.columnName()).append(" = ?");
			} else {
				sql.append(" and ").append(field.columnName()).append(" = ?");
			}
			idx ++;
		}
		return queryForObject(cls, sql.toString(), params);
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryBusinessObjs(java.lang.Class, java.lang.String, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public <T> List<T> queryBusinessObjs(Class<T> cls, String query,
			SqlParameter... params) throws SQLException {
		return queryForList(cls, query, params);
	}

	private <T> List<T> queryBusinessObjs(Class<T> cls, String query, int offset, SqlParameter... params)
	throws SQLException {
		if(offset > 0){
			RowsResultSetExtractor<T> rse = new RowsResultSetExtractor<T>(getObjectReader(), cls);
			rse.setOffset(offset);
			return query(query, rse, params);
		}else{
			return queryForList(cls, query, params);
		}
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryAllBusinessObjs(java.lang.Class)
	 */
	@Override
	public <T> List<T> queryAllBusinessObjs(Class<T> cls) throws SQLException {
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		String tableName = null;
		if ( clsmapping != null ){
			tableName = clsmapping.tableName();
		}
		if(tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		sql.append(tableName);
		return queryForList(cls, sql.toString());
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryForListMap(java.lang.String, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public List<Map<String, Object>> queryForListMap(String sql, SqlParameter... params)
			throws SQLException {
		return queryForList(sql, params);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#querySingleObject(java.lang.Class, java.lang.String, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public <T> T querySingleObject(Class<T> cls, String sql, SqlParameter... params) throws SQLException {
		return queryForObject(cls, sql, params);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryBusinessObjs(java.lang.Class, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public <T> List<T> queryBusinessObjs(Class<T> cls, final String sql,
			final int startRecord, final int maxRecord, final SqlParameter... params) throws SQLException {
		Assert.notNull(sql, "sql can not be null");
		if(getDialect() == null){
			throw new SQLException("can not find SQL Dialect.");
		}
		List<T> items = null;
		String sqlforLimit = null;
		if(getDialect().supportsOffset()){
			sqlforLimit = getDialect().getLimitString(sql, startRecord, maxRecord);
    		items = queryBusinessObjs(cls, sqlforLimit, params);
		}else{
			sqlforLimit = getDialect().getLimitString(sql, 0, startRecord + maxRecord);
    		items = queryBusinessObjs(cls, sqlforLimit, startRecord, params);
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryByPagedQuery(java.lang.Class, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls, final String sql,
			final int startPage, final int pageSize, final SqlParameter... params) throws SQLException {
		Assert.notNull(sql, "sql can not be null");
		String coutsql = getDialect().getCountSql(sql);//"SELECT COUNT(0) " + removeSelect(sql);
	    Long totalCount = queryForObject(Long.class, coutsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			List<T> items = null;
	    	if ( 0 == totalCount.longValue()){
				items = new ArrayList<T>(0);
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
		    		items = queryBusinessObjs(cls, sqlforLimit, params);
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
		    		items = queryBusinessObjs(cls, sqlforLimit, startRecord, params);
	    		}
	    	}
	    	PaginationSupport<T> ps = new PaginationSupport<T>(pageSize, totalCount, curpage, items);
	    	return ps;
	    }
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#queryByPagedQuery(java.lang.Class, java.lang.String, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls,
			String countsql, String sql, int startPage, int pageSize,
			SqlParameter... params) throws SQLException {
		Assert.notNull(countsql, "countsql can not be null");
		Assert.notNull(sql, "sql can not be null");
		Long totalCount = queryForObject(Long.class, countsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			List<T> items = null;
	    	if ( 0 == totalCount.longValue()){
				items = new ArrayList<T>(0);
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
		    		items = queryBusinessObjs(cls, sqlforLimit, params);
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
		    		items = queryBusinessObjs(cls, sqlforLimit, startRecord, params);
	    		}
	    	}
	    	PaginationSupport<T> ps = new PaginationSupport<T>(pageSize, totalCount, curpage, items);
	    	return ps;
	    }
		return null;
	}

	protected static String removeSelect(String sql) {
		int beginPos = sql.toLowerCase().indexOf("from");
		return sql.substring(beginPos);
	}
	
//	类似这样的无法获取正确结果select * from (select * from UUM_USER order by id desc) A order by state
//	private static String removeOrders(String hql) {
//		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
//		Matcher m = p.matcher(hql);
//		StringBuffer sb = new StringBuffer();
//		for(; m.find(); m.appendReplacement(sb, ""));
//		m.appendTail(sb);
//		return sb.toString();
//	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#deleteBusiness(java.lang.Class, java.io.Serializable[])
	 */
	@Override
	public int deleteBusiness(Class<?> cls, Serializable... pkvals)
			throws SQLException {
		Assert.notEmpty(pkvals, "pkvals must not null or empty!");
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		if(pkvals.length > 1 && clsmapping == null){
			throw new IllegalArgumentException("too many primary key values, can not find the key order.");
		}
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
		SqlParameter[] params = new SqlParameter[pkfields.length];
		for ( FieldMapping field : pkfields ) {
			params[idx] = new SqlParameter(field.columnName(), pkvals[idx]);
			if ( idx == 0 ){
				sql.append(field.columnName()).append(" = ?");
			} else {
				sql.append(" and ").append(field.columnName()).append(" = ?");
			}
			idx ++;
		}
		return update(sql.toString(), params);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#deleteBusiness(java.lang.Object[])
	 */
	@Override
	public int deleteBusiness(Object... pojos) throws SQLException {
		Assert.notEmpty(pojos, "pojos can not be null.");
		if(pojos.length == 1){
			Assert.notNull(pojos[0]);
			UpdateSqlInfo deleteSqlinfo = generateDeleteSql(pojos[0].getClass());
			List<FieldMapping> parameterMappings = deleteSqlinfo.getParameterMappings();
			SqlParameter[] params = new SqlParameter[parameterMappings.size()];
			Map<String, Object> objvalMap = null;
			try {
				objvalMap = this.getObjectReader().readValue2Map(pojos[0]);
			} catch (Exception e) {
				throw new SQLException(e);
			}
			for ( int i = 0 ; i < parameterMappings.size(); i ++) {
				FieldMapping field = parameterMappings.get(i);
				params[i] = new SqlParameter(field.columnName(), (Serializable) objvalMap.get(field.columnName()));
			}
			return update(deleteSqlinfo.getSql(), params);
		}else{
			Set<Class<?>> clsSet = new HashSet<Class<?>>();
			for(Object pojo : pojos){
				clsSet.add(pojo.getClass());
			}
			Map<Class<?>, UpdateSqlInfo> sqlMap = new HashMap<Class<?>, UpdateSqlInfo>();
			for(Class<?> cls : clsSet){
				sqlMap.put(cls, generateDeleteSql(cls));
			}
			int rtnval = 0;
			for(Object pojo : pojos){
				UpdateSqlInfo deleteSqlinfo = sqlMap.get(pojo.getClass());
				List<FieldMapping> parameterMappings = deleteSqlinfo.getParameterMappings();
				SqlParameter[] params = new SqlParameter[parameterMappings.size()];
				Map<String, Object> objvalMap = null;
				try {
					objvalMap = this.getObjectReader().readValue2Map(pojo);
				} catch (Exception e) {
					throw new SQLException(e);
				}
				for ( int i = 0 ; i < parameterMappings.size(); i ++) {
					FieldMapping field = parameterMappings.get(i);
					params[i] = new SqlParameter(field.columnName(), (Serializable) objvalMap.get(field.columnName()));
				}
				rtnval += update(deleteSqlinfo.getSql(), params);
			}
			return rtnval;
		}
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#deleteBusinessCol(java.util.Collection)
	 */
	@Override
	public int deleteBusinessCol(Collection<? extends Serializable> pojos)
			throws SQLException {
		if( (pojos != null) && (!pojos.isEmpty()) ){
			return deleteBusiness(pojos.toArray());
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#saveBusinessObjs(java.lang.Object[])
	 */
	@Override
	public int saveBusinessObjs(Object... pojos) throws SQLException {
		Assert.notEmpty(pojos, "pojos can not be null.");
		if(pojos.length == 1){
			final String sql = generateInsertSql(pojos[0].getClass());
			if ( autoManagerTransaction ) {
				beginTransation();
			}
			try {
				int rtn = saveBusinessObjs(pojos[0], sql);
				if ( autoManagerTransaction ) {
					commitTransation();
				}
				return rtn;
			 } catch ( SQLException e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw e;
			} catch ( Exception e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw new SQLException(e);
			}
		}else{
			int rtnval = 0;
//			Set<Class<?>> clsSet = new HashSet<Class<?>>();
//			for(Object pojo : pojos){
//				clsSet.add(pojo.getClass());
//			}
//			Map<Class<?>, String> sqlMap = new HashMap<Class<?>, String>();
//			for(Class<?> cls : clsSet){
//				sqlMap.put(cls, generateInsertSql(cls));
//			}
			if ( autoManagerTransaction ) {
				beginTransation();
			}
			try {
//				for(Object pojo : pojos){
//					final String sql = sqlMap.get(pojo.getClass());
//					rtnval += saveBusinessObjs(pojo, sql);
//				}
//				final String sql = sqlMap.get(pojos[0].getClass());
				final String sql = generateInsertSql(pojos[0].getClass());
				rtnval = batchSaveBusinessObjs(sql, pojos);
				if ( autoManagerTransaction ) {
					commitTransation();
				}
				return rtnval;
			 } catch ( SQLException e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw e;
			} catch ( Exception e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw new SQLException(e);
			}
		}
	}
	
	private Integer batchSaveBusinessObjs(final String insertsql, final Object... pojos) throws Exception {
		Class<?> cls = pojos[0].getClass();
		final Map<String, FieldMapping> fieldMappings = getObjectReader().getObjectFieldMap(cls);
		final ClassMapping classMapping = getObjectReader().getClassMapping(cls);
		final ICommonDao dao = this;
		String keyGenerator = null;
		if(classMapping != null){
			keyGenerator = classMapping.keyGenerator();
		}else{
			keyGenerator = KeyGenertator.ASSIGNED;
		}
		final IdentifierGenerator idgenerator = getIdentifierGeneratorFactory().createIdentifierGenerator(keyGenerator);
		if(keyGenerator.equals(KeyGenertator.SELECT)) {
			//if select deal primary key val first, becuase need execute update sql
			for (FieldMapping field : fieldMappings.values()) {
				if(field.primary()) {
					Map<String, Object> objValMap = getObjectReader().readValue2Map(pojos[0]);
					Object val = objValMap.get(field.columnName());
					if(val == null) {
						idgenerator.generate(getDialect(), dao, pojos, field, true);
					}
					break;
				}
			}
		}
		Integer vals = doExecute(new ConnectionCallback<Integer>() {

			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				int rtnval = 0;
				try{
//					if(logger.isDebugEnabled()){
//						logger.debug("Batch Executing SQL [" + insertsql + "]");
//					}
					int batchsize = 0;
					stmt = connection.prepareStatement(insertsql);
					Serializable[] pkvals = null;
					int ik = 0;
					for(Object pojo : pojos){
						Map<String, Object> objValMap = getObjectReader().readValue2Map(pojo);
						int idx = 1;
						for (FieldMapping field : fieldMappings.values()) {
							if(field.includeInWrites()){
								if( (field.primary())
										&& (classMapping != null) 
										&& (KeyGenertator.NATIVE.equals(classMapping.keyGenerator())) ){
									//if native 
									continue;
								}
								Object val = objValMap.get(field.columnName());
								if(val == null && field.primary()){
									//if KeyGenertator not native
									if(pkvals == null){
										pkvals = idgenerator.generate(getDialect(), dao, pojos, field, true);
										if(pkvals == null){
											pkvals = new Serializable[pojos.length];
										}
									}
									val = pkvals[ik];
								}
								int columntype = field.columnType();
								if( columntype == Types.BLOB
										|| columntype == Types.CLOB
										|| columntype == Types.NCLOB) {
									//deal CLOB, BLOB etc.
									if( val != null ) {
										if(columntype == Types.CLOB || columntype == Types.NCLOB){
											if(val instanceof byte[]){
												Clob lob = getOracleNatveJdbcConnection(connection).createClob();
												lob.setString(1, new String((byte[])val));
												stmt.setClob(idx, lob);
												clobs.add(lob);
											}else if(val instanceof String){
												Clob lob = getOracleNatveJdbcConnection(connection).createClob();
												lob.setString(1, (String)val);
												stmt.setClob(idx, lob);
												clobs.add(lob);
											}else if(val instanceof AsciiStream){
												stmt.setClob(idx, new InputStreamReader(((AsciiStream)val).getInputStream()), ((AsciiStream)val).getLength());
											}
										}else{
											Blob lob = getOracleNatveJdbcConnection(connection).createBlob();
											lob.setBytes(1, (byte[])val);
											stmt.setBlob(idx, lob);
											blobs.add(lob);
										}
									} else {
										stmt.setNull(idx, columntype);
									}
								} else {
									if(val != null) {
										Class<?> targetsqlcls = getObjectReader().getTargetSqlClass(columntype);
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
//									stmt.setObject(idx, val, columntype);
									//fix java.sql.SQLException: Unknown Types value
									stmt.setObject(idx, val);
								}
								idx ++;
							}
						}
						stmt.addBatch();
						batchsize ++;
						if(batchsize >= batchSize){
							int[] bvals = stmt.executeBatch();
							for(int val : bvals){
								rtnval += val;
							}
							stmt.clearBatch();
							if(logger.isDebugEnabled()){
								logger.debug("Batch Executing SQL [size:"+batchsize+"] [" + insertsql + "]");
							}
							batchsize = 0;
						}
						ik ++;
					}
					if(batchsize > 0){
						int[] bvals = stmt.executeBatch();
						for(int val : bvals){
							rtnval += val;
						}
						if(logger.isDebugEnabled()){
							logger.debug("Batch Executing SQL [size:"+batchsize+"] [" + insertsql + "]");
						}
					}
					return rtnval;
				}catch(SQLException e){
					throw e;
				}catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if(!clobs.isEmpty()){
						for(Clob clob : clobs){
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if(!blobs.isEmpty()){
						for(Blob blob : blobs){
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}

		});
		return vals;
	}
	
	private int saveBusinessObjs(Object pojo, final String insertsql) throws Exception {
		Assert.notNull(pojo);
		Class<?> cls = pojo.getClass();
		final Map<String, FieldMapping> fieldMappings = getObjectReader().getObjectFieldMap(cls);
		final Map<String, Object> objValMap = getObjectReader().readValue2Map(pojo);
		final ClassMapping classMapping = getObjectReader().getClassMapping(cls);
		String keyGenerator = null;
		if(classMapping != null){
			keyGenerator = classMapping.keyGenerator();
		}else{
			keyGenerator = KeyGenertator.ASSIGNED;
		}
		//deal primary key
		for (FieldMapping field : fieldMappings.values()) {
			if(field.primary()) {
				Object val = objValMap.get(field.columnName());
				if(val == null) {
					IdentifierGenerator idgenerator = getIdentifierGeneratorFactory().createIdentifierGenerator(keyGenerator);
					if(idgenerator != null){
						Serializable id = idgenerator.generate(getDialect(), this, pojo, field, true);
						objValMap.put(field.columnName(), id);
					}else{
						throw new SQLException("column " + field.columnName() + " is null and can not find the proper IdentifierGenerator");
					}
				}
			}
		}
		
		Integer val = doExecute(new ConnectionCallback<Integer>() {
			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				try{
					if(logger.isDebugEnabled()){
						logger.debug("Executing SQL [" + insertsql + "] values:" + objValMap);
					}
					stmt = connection.prepareStatement(insertsql);
					int idx = 1;
					for (FieldMapping field : fieldMappings.values()) {
						if(field.includeInWrites()){
							if( (field.primary())
									&& (classMapping != null) 
									&& (KeyGenertator.NATIVE.equals(classMapping.keyGenerator())) ){
								//if native 
								continue;
							}
							Object val = objValMap.get(field.columnName());
							int columntype = field.columnType();
							if( columntype == Types.BLOB
									|| columntype == Types.CLOB
									|| columntype == Types.NCLOB) {
								//deal CLOB, BLOB etc.
								if( val != null ) {
									if(columntype == Types.CLOB || columntype == Types.NCLOB){
										if(val instanceof byte[]){
											Clob lob = getOracleNatveJdbcConnection(connection).createClob();
											lob.setString(1, new String((byte[])val));
										    stmt.setClob(idx, lob);
										    clobs.add(lob);
										}else if(val instanceof String){
											Clob lob = getOracleNatveJdbcConnection(connection).createClob();
											lob.setString(1, (String)val);
										    stmt.setClob(idx, lob);
										    clobs.add(lob);
										}else if(val instanceof AsciiStream){
											stmt.setClob(idx, new InputStreamReader(((AsciiStream)val).getInputStream()), ((AsciiStream)val).getLength());
										}
									}else{
										Blob lob = getOracleNatveJdbcConnection(connection).createBlob();
										lob.setBytes(1, (byte[])val);
										stmt.setBlob(idx, lob);
									    blobs.add(lob);
									}
								} else {
									stmt.setNull(idx, columntype);
//									if(columntype == Types.CLOB){
//										stmt.setClob(idx, (Clob)null);
//									}else{
//										stmt.setBlob(idx, (Blob)null);
//									}
								}
							} else {
								if(val != null) {
									Class<?> targetsqlcls = getObjectReader().getTargetSqlClass(columntype);
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
//								stmt.setObject(idx, val, columntype);
								//fix java.sql.SQLException: Unknown Types value
								stmt.setObject(idx, val);
							}
							idx ++;
						}
					}
					return stmt.executeUpdate();
				}catch (SQLException e) {
					throw e;
				}catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if(!clobs.isEmpty()){
						for(Clob clob : clobs){
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if(!blobs.isEmpty()){
						for(Blob blob : blobs){
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}
		});
		return val == null ? 0 : val.intValue();
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#saveBusinessObjsCol(java.util.Collection)
	 */
	@Override
	public int saveBusinessObjsCol(Collection<? extends Serializable> pojos)
			throws SQLException {
		if( (pojos != null) && (!pojos.isEmpty()) ){
			return saveBusinessObjs(pojos.toArray());
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#updateBusinessObjs(boolean, java.lang.Object[])
	 */
	@Override
	public int updateBusinessObjs(boolean isFilterNull, Object... pojos)
			throws SQLException {
		Assert.notEmpty(pojos, "pojos can not be null.");
		if(pojos.length == 1){
			UpdateSqlInfo updateSqlInfo = null;
			if(isFilterNull){//filter null
				updateSqlInfo = generateUpdateSql(pojos[0].getClass(), pojos[0]);
			}else{
				updateSqlInfo = generateUpdateSql(pojos[0].getClass(), null);
			}
			if ( autoManagerTransaction ) {
				beginTransation();
			}
			try {
				int rtn = updateBusinessObjs(pojos[0], updateSqlInfo);
				if ( autoManagerTransaction ) {
					commitTransation();
				}
				return rtn;
			 } catch ( SQLException e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw e;
			} catch ( Exception e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw new SQLException(e);
			}
		}else{
			int rtnval = 0;
			if ( autoManagerTransaction ) {
				beginTransation();
			}
			try {
				if(isFilterNull){//filter null
					//one by one, sql can be not same
					for(Object pojo : pojos){
						Class<?> cls = pojo.getClass();
						final UpdateSqlInfo updateSqlInfo = generateUpdateSql(cls, pojo);
						rtnval += updateBusinessObjs(pojo, updateSqlInfo);
					}
				}else{//not filter null, same class ,sql is same
					Set<Class<?>> clsSet = new HashSet<Class<?>>();
					for(Object pojo : pojos){
						clsSet.add(pojo.getClass());
					}
					if(clsSet.size() == 1){
						UpdateSqlInfo updateSqlInfo = null;
						for(Class<?> cls : clsSet){
							updateSqlInfo = generateUpdateSql(cls, null);
							break;
						}
						rtnval = batchUpdateBusinessObjs(updateSqlInfo, pojos);
					}else{
						Map<Class<?>, UpdateSqlInfo> sqlMap = new HashMap<Class<?>, UpdateSqlInfo>();
						for(Class<?> cls : clsSet){
							sqlMap.put(cls, generateUpdateSql(cls, null));
						}
						for(Object pojo : pojos){
							final UpdateSqlInfo updateSqlInfo = sqlMap.get(pojo.getClass());
							rtnval += updateBusinessObjs(pojo, updateSqlInfo);
						}
					}
				}
				if ( autoManagerTransaction ) {
					commitTransation();
				}
				return rtnval;
			} catch ( SQLException e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw e;
			} catch ( Exception e ) {
				if ( autoManagerTransaction ) {
					rollbackTransation();
				}
				throw new SQLException(e);
			}
		}
	}

	private int batchUpdateBusinessObjs(final UpdateSqlInfo updateSqlInfo, final Object... pojos) throws SQLException {
		Integer val = doExecute(new ConnectionCallback<Integer>() {

			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				int rtnval = 0;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				try{
//					if(logger.isDebugEnabled()){
//						logger.debug("Executing SQL [" + updateSqlInfo.getSql() + "]");
//					}
					int batchsize = 0;
					stmt = connection.prepareStatement(updateSqlInfo.getSql());
					List<FieldMapping> parameterMappings = updateSqlInfo.getParameterMappings();
					for(Object pojo : pojos) {
						int idx = 1;
						Map<String, Object> objValMap = getObjectReader().readValue2Map(pojo);
						for (FieldMapping field : parameterMappings) {
							if(field.includeInUpdate()){
								int columntype = field.columnType();
								if( columntype == Types.BLOB
										|| columntype == Types.CLOB
										|| columntype == Types.NCLOB) {
									//deal CLOB, BLOB.
									Object val = objValMap.get(field.columnName());
									if( val != null ) {
										if(columntype == Types.CLOB || columntype == Types.NCLOB){
											if(val instanceof byte[]){
												Clob lob = getOracleNatveJdbcConnection(connection).createClob();
												lob.setString(1, new String((byte[])val));
												stmt.setClob(idx, lob);
												clobs.add(lob);
											}else if(val instanceof String){
												Clob lob = getOracleNatveJdbcConnection(connection).createClob();
												lob.setString(1, (String)val);
												stmt.setClob(idx, lob);
												clobs.add(lob);
											}else if(val instanceof AsciiStream){
												stmt.setClob(idx, new InputStreamReader(((AsciiStream)val).getInputStream()), ((AsciiStream)val).getLength());
											}
										}else{
											Blob lob = getOracleNatveJdbcConnection(connection).createBlob();
											lob.setBytes(1, (byte[])val);
											stmt.setBlob(idx, lob);
											blobs.add(lob);
										}
									} else {
										stmt.setNull(idx, columntype);
									}
								} else {
									Object val = objValMap.get(field.columnName());
									if(val != null) {
										Class<?> targetsqlcls = getObjectReader().getTargetSqlClass(columntype);
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
//									stmt.setObject(idx, val, field.columnType());
									//fix java.sql.SQLException: Unknown Types value
									stmt.setObject(idx, val);
								}
								idx ++;
							}
						}//end for
						stmt.addBatch();
						batchsize ++;
						if(batchsize >= batchSize){
							int[] bvals = stmt.executeBatch();
							for(int val : bvals){
								rtnval += val;
							}
							stmt.clearBatch();
							if(logger.isDebugEnabled()){
								logger.debug("Batch Executing SQL [size:"+batchsize+"] [" + updateSqlInfo.getSql() + "]");
							}
							batchsize = 0;
						}
					}//end for pojos
					if(batchsize > 0){
						int[] bvals = stmt.executeBatch();
						for(int val : bvals){
							rtnval += val;
						}
						if(logger.isDebugEnabled()){
							logger.debug("Batch Executing SQL [size:"+batchsize+"] [" + updateSqlInfo.getSql() + "]");
						}
					}
					return rtnval;
				}catch (SQLException e) {
					throw e;
				}catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if(!clobs.isEmpty()){
						for(Clob clob : clobs){
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if(!blobs.isEmpty()){
						for(Blob blob : blobs){
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}
			
		});
		return val == null ? 0 : val.intValue();
	}
	
	/**
	 * update
	 * @param object
	 * @param updateSqlInfo
	 * @return
	 * @throws Exception 
	 */
	private int updateBusinessObjs(final Object pojo, final UpdateSqlInfo updateSqlInfo) throws Exception {
		final Map<String, Object> objValMap = getObjectReader().readValue2Map(pojo);
		Integer val = doExecute(new ConnectionCallback<Integer>() {
			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				try{
					if(logger.isDebugEnabled()){
						logger.debug("Executing SQL [" + updateSqlInfo.getSql() + "] values:" + objValMap);
					}
					stmt = connection.prepareStatement(updateSqlInfo.getSql());
					List<FieldMapping> parameterMappings = updateSqlInfo.getParameterMappings();
					int idx = 1;
					for (FieldMapping field : parameterMappings) {
						if(field.includeInUpdate()){
							int columntype = field.columnType();
							if( columntype == Types.BLOB
									|| columntype == Types.CLOB
									|| columntype == Types.NCLOB) {
								//deal CLOB, BLOB.
								Object val = objValMap.get(field.columnName());
								if( val != null ) {
									if(columntype == Types.CLOB || columntype == Types.NCLOB){
										if(val instanceof byte[]){
											Clob lob = getOracleNatveJdbcConnection(connection).createClob();
											lob.setString(1, new String((byte[])val));
										    stmt.setClob(idx, lob);
										    clobs.add(lob);
										}else if(val instanceof String){
											Clob lob = getOracleNatveJdbcConnection(connection).createClob();
											lob.setString(1, (String)val);
										    stmt.setClob(idx, lob);
										    clobs.add(lob);
										}else if(val instanceof AsciiStream){
											stmt.setClob(idx, new InputStreamReader(((AsciiStream)val).getInputStream()), ((AsciiStream)val).getLength());
										}
									}else{
										Blob lob = getOracleNatveJdbcConnection(connection).createBlob();
										lob.setBytes(1, (byte[])val);
										stmt.setBlob(idx, lob);
									    blobs.add(lob);
									}
								} else {
									stmt.setNull(idx, columntype);
//									if(columntype == Types.CLOB){
//										stmt.setClob(idx, (Clob)null);
//									}else{
//										stmt.setBlob(idx, (Blob)null);
//									}
								}
							} else {
								Object val = objValMap.get(field.columnName());
								if(val != null) {
									Class<?> targetsqlcls = getObjectReader().getTargetSqlClass(columntype);
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
//								stmt.setObject(idx, val, field.columnType());
								//fix java.sql.SQLException: Unknown Types value
								stmt.setObject(idx, val);
							}
							idx ++;
						}
					}
					return stmt.executeUpdate();
				}catch (SQLException e) {
					throw e;
				}catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if(!clobs.isEmpty()){
						for(Clob clob : clobs){
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if(!blobs.isEmpty()){
						for(Blob blob : blobs){
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}
		});
		return val == null ? 0 : val.intValue();
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDao#updateBusinessObjsCol(boolean, java.util.Collection)
	 */
	@Override
	public int updateBusinessObjsCol(boolean isFilterNull,
			Collection<? extends Serializable> pojos) throws SQLException {
		if( (pojos != null) && (!pojos.isEmpty()) ){
			return updateBusinessObjs(isFilterNull, pojos.toArray());
		}
		return 0;
	}
	/**
	 * 判断是否是Number类型
	 * @param sqlType
	 * @return
	 */
//	private boolean sqlHasSegment(int sqlType) {
//		boolean has = false;
//		switch (sqlType) {
//		case Types.BIGINT:
//		case Types.BIT:
//		case Types.BOOLEAN:
//		case Types.DECIMAL:
//		case Types.DOUBLE:
//		case Types.FLOAT:
//		case Types.INTEGER:
//		case Types.NUMERIC:
//		case Types.REAL:
//		case Types.SMALLINT:
//		case Types.TINYINT:
//			has = false;
//			break;
//		case Types.DATE:
//		case Types.CHAR:
//		case Types.BLOB:
//		case Types.CLOB:
//		case Types.LONGNVARCHAR:
//		case Types.LONGVARBINARY:
//		case Types.LONGVARCHAR:
//		case Types.NCHAR:
//		case Types.NCLOB:
//		case Types.NVARCHAR:
//		case Types.SQLXML:
//		case Types.TIME:
//		case Types.TIMESTAMP:
//		case Types.VARBINARY:
//		case Types.VARCHAR:
//			has = true;
//			break;
//		default:
//			break;
//		}
//		return has;
//	}

	
	/**
	 * 判断是否是Number类型
	 * @param keyType
	 * @return
	 */
//	private boolean isNumber(String keyType) {
//		if (keyType.equals("java.math.BigDecimal")
//				|| keyType.equals("java.math.BigInteger")
//				|| keyType.equals("java.lang.Integer")
//				|| keyType.equals("java.lang.Long")
//				|| keyType.equals("java.lang.Short")
//				|| keyType.equals("java.lang.Double")
//				|| keyType.equals("java.lang.Float")
//				|| keyType.equals("java.lang.Byte")
//				|| keyType.equals("java.util.concurrent.atomic.AtomicLong")
//				|| keyType.equals("java.util.concurrent.atomic.AtomicInteger") ) {
//			return true;
//		}
//		return false;
//	}

	/**
	 * @return the batchSize
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
}
