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
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.dao.id.IdentifierGenerator;
import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.annotation.KeyGenertator;
import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.mapping.AsciiStream;
import org.uorm.orm.mapping.IJsonReader;
import org.uorm.orm.mapping.IObjectReader;
import org.uorm.orm.mapping.IXmlReader;
import org.uorm.orm.mapping.JsonReader;
import org.uorm.orm.mapping.XmlReader;
import org.uorm.utils.Assert;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-2-1       郭训长            创建<br/>
 */
public class OneThreadMultiConnectionCommonDaoXmlExtImpl extends
		OneThreadMultiConnectionCommonDaoImpl implements ICommonDaoXmlExt {
	private static final Logger logger = LoggerFactory.getLogger(OneThreadMultiConnectionCommonDaoXmlExtImpl.class);
	
	private IXmlReader xmlReader = null;
	private IJsonReader jsonReader = null;
	
	private String rootName = CommonDaoXmlExtImpl._DEFAULT_ROOT_NAME;
	private String itemName = CommonDaoXmlExtImpl._DEFAULT_ITEM_NAME;
	/**
	 * 
	 */
	public OneThreadMultiConnectionCommonDaoXmlExtImpl() {
		super();
		this.xmlReader = new XmlReader();
		this.jsonReader = new JsonReader();
	}

	/**
	 * @param connectionFactory
	 * @param objectReader
	 */
	public OneThreadMultiConnectionCommonDaoXmlExtImpl(
			ConnectionFactory connectionFactory, IObjectReader objectReader) {
		super(connectionFactory, objectReader);
		this.xmlReader = new XmlReader();
		this.jsonReader = new JsonReader();
	}

	/**
	 * @param connectionFactory
	 */
	public OneThreadMultiConnectionCommonDaoXmlExtImpl(
			ConnectionFactory connectionFactory) {
		super(connectionFactory);
		this.xmlReader = new XmlReader();
		this.jsonReader = new JsonReader();
	}

	/**
	 * @param connectionFactory
	 * @param xmlReader
	 */
	public OneThreadMultiConnectionCommonDaoXmlExtImpl(ConnectionFactory connectionFactory, IXmlReader xmlReader) {
		super(connectionFactory);
		this.xmlReader = xmlReader;
		this.jsonReader = new JsonReader();
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fill(java.lang.String, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fill(String sql, SqlParameter... params)
			throws SQLException {
		return query(sql, new RowsResultSetXmlExtractor(xmlReader, rootName, itemName), params);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fill(java.lang.String, java.lang.String, java.lang.String, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fill(String sql, String rootName, String itemName,
			SqlParameter... params) throws SQLException {
		return query(sql, new RowsResultSetXmlExtractor(xmlReader, rootName, itemName), params);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fill(java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fill(String sql, int startRecord, int maxRecord,
			SqlParameter... params) throws SQLException {
		if(getDialect() == null){
			throw new SQLException("can not find SQL Dialect.");
		}
		Document items = null;
		String sqlforLimit = null;
		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(xmlReader, rootName, itemName);
		if(getDialect().supportsOffset()){
			sqlforLimit = getDialect().getLimitString(sql, startRecord, maxRecord);
    		items = query(sqlforLimit, rse, params);
		}else{
			sqlforLimit = getDialect().getLimitString(sql, 0, startRecord + maxRecord);
			rse.setOffset(startRecord);
    		items = query(sqlforLimit, rse, params);
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fill(java.lang.String, java.lang.String, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fill(String sql, String rootName, String itemName,
			int startRecord, int maxRecord, SqlParameter... params)
			throws SQLException {
		if(getDialect() == null){
			throw new SQLException("can not find SQL Dialect.");
		}
		Document items = null;
		String sqlforLimit = null;
		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(xmlReader, rootName, itemName);
		if(getDialect().supportsOffset()){
			sqlforLimit = getDialect().getLimitString(sql, startRecord, maxRecord);
    		items = query(sqlforLimit, rse, params);
		}else{
			sqlforLimit = getDialect().getLimitString(sql, 0, startRecord + maxRecord);
			rse.setOffset(startRecord);
    		items = query(sqlforLimit, rse, params);
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillByPagedQuery(java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fillByPagedQuery(String sql, int startPage, int pageSize,
			SqlParameter... params) throws SQLException {
//		String coutsql = "SELECT COUNT(0) FROM (" + sql +") A";
		String coutsql = getDialect().getCountSql(sql);//"SELECT COUNT(0) " + removeSelect(sql);
	    Long totalCount = queryForObject(Long.class, coutsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			Document items = null;
	    	if ( 0 == totalCount.longValue()){
				items = DocumentHelper.createDocument();
				Element rootElement = items.addElement(rootName);
				rootElement.addAttribute("TotalCount", "0");
//				Element totalitem = rootElement.addElement(itemName + "1");
//	    		totalitem.addElement("TotalCount").addText("0");
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(xmlReader, rootName, itemName);
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
	    			items = query(sqlforLimit, rse, params);
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
	    			rse.setOffset(startRecord);
	        		items = query(sqlforLimit, rse, params);
	    		}
				items.getRootElement().addAttribute("TotalCount", String.valueOf(totalCount));
//	    		Element totalitem = items.getRootElement().addElement(itemName + "1");
//	    		totalitem.addElement("TotalCount").addText(String.valueOf(totalCount));
	    	}
	    	return items;
	    }
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillByPagedQuery(java.lang.String, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fillByPagedQuery(String countsql, String sql,
			int startPage, int pageSize, SqlParameter... params)
			throws SQLException {
	    Long totalCount = queryForObject(Long.class, countsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			Document items = null;
	    	if ( 0 == totalCount.longValue()){
				items = DocumentHelper.createDocument();
				Element rootElement = items.addElement(rootName);
				rootElement.addAttribute("TotalCount", "0");
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(xmlReader, rootName, itemName);
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
	    			items = query(sqlforLimit, rse, params);
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
	    			rse.setOffset(startRecord);
	        		items = query(sqlforLimit, rse, params);
	    		}
				items.getRootElement().addAttribute("TotalCount", String.valueOf(totalCount));
	    	}
	    	return items;
	    }
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillByPagedQuery(java.lang.String, java.lang.String, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fillByPagedQuery(String sql, String rootName,
			String itemName, int startPage, int pageSize,
			SqlParameter... params) throws SQLException {
		String coutsql = getDialect().getCountSql(sql);//"SELECT COUNT(0) " + removeSelect(sql);
	    Long totalCount = queryForObject(Long.class, coutsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			Document items = null;
	    	if ( 0 == totalCount.longValue()){
				items = DocumentHelper.createDocument();
				Element rootElement = items.addElement(rootName);
				rootElement.addAttribute("TotalCount", "0");
//				Element totalitem = rootElement.addElement(itemName + "1");
//	    		totalitem.addElement("TotalCount").addText("0");
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(xmlReader, rootName, itemName);
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
	    			items = query(sqlforLimit, rse, params);
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
	    			rse.setOffset(startRecord);
	        		items = query(sqlforLimit, rse, params);
	    		}
				items.getRootElement().addAttribute("TotalCount", String.valueOf(totalCount));
//	    		Element totalitem = items.getRootElement().addElement(itemName + "1");
//	    		totalitem.addElement("TotalCount").addText(String.valueOf(totalCount));
	    	}
	    	return items;
	    }
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillByPagedQuery(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public Document fillByPagedQuery(String countsql, String sql,
			String rootName, String itemName, int startPage, int pageSize,
			SqlParameter... params) throws SQLException {
	    Long totalCount = queryForObject(Long.class, countsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			Document items = null;
	    	if ( 0 == totalCount.longValue()){
				items = DocumentHelper.createDocument();
				Element rootElement = items.addElement(rootName);
				rootElement.addAttribute("TotalCount", "0");
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(xmlReader, rootName, itemName);
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
	    			items = query(sqlforLimit, rse, params);
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
	    			rse.setOffset(startRecord);
	        		items = query(sqlforLimit, rse, params);
	    		}
				items.getRootElement().addAttribute("TotalCount", String.valueOf(totalCount));
	    	}
	    	return items;
	    }
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelData(java.lang.Class, java.util.Map<java.lang.String,java.lang.Object>[])
	 */
	@Override
	public int saveModelData(Class<?> cls, Map<String, Object>[] models)
			throws SQLException {
		Assert.notEmpty(models, "models can not be null.");
		if ( autoManagerTransaction ) {
			beginTransation();
		}
		try {
			final String sql = generateInsertSql(cls);
			return batchSaveBusinessObjs(sql, cls, models);
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

	private Integer batchSaveBusinessObjs(final String insertsql, final Class<?> cls, final Map<String, Object>... objValMaps) throws Exception {
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
					Map<String, Object> objValMap = objValMaps[0];
					Object val = objValMap.get(field.columnName());
					if(val == null) {
						idgenerator.generate(getDialect(), dao, cls, objValMaps, field, true);
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
					int batchsize = 0;
					stmt = connection.prepareStatement(insertsql);
					Serializable[] pkvals = null;
					int ik = 0;
					for(Map<String, Object> objValMap : objValMaps){
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
										pkvals = idgenerator.generate(getDialect(), dao, cls, objValMaps, field, true);
										if(pkvals == null){
											pkvals = new Serializable[objValMaps.length];
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
						if(batchsize >= getBatchSize()){
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
	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelDataCol(java.lang.Class, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int saveModelDataCol(Class<?> cls,
			Collection<Map<String, Object>> models) throws SQLException {
		if(models != null && !models.isEmpty()){
			saveModelData(cls, models.toArray(new Map[models.size()]));
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelData(java.lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int saveModelData(Class<?> cls, Map<String, Object> model)
			throws SQLException {
		Assert.notNull(model, "model can not be null.");
		return saveModelData(cls, new Map[]{model});
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillJson(java.lang.String, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public String fillJson(String sql, SqlParameter... params)
			throws SQLException {
		return query(sql, new RowsResultSetJsonExtractor(this.jsonReader), params);
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillJson(java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public String fillJson(String sql, int startRecord, int maxRecord,
			SqlParameter... params) throws SQLException {
		if(getDialect() == null){
			throw new SQLException("can not find SQL Dialect.");
		}
		String items = null;
		String sqlforLimit = null;
		RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor(this.jsonReader);
		if(getDialect().supportsOffset()){
			sqlforLimit = getDialect().getLimitString(sql, startRecord, maxRecord);
    		items = query(sqlforLimit, rse, params);
		}else{
			sqlforLimit = getDialect().getLimitString(sql, 0, startRecord + maxRecord);
			rse.setOffset(startRecord);
    		items = query(sqlforLimit, rse, params);
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillJsonByPagedQuery(java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public String fillJsonByPagedQuery(String sql, int startPage, int pageSize,
			SqlParameter... params) throws SQLException {
		String coutsql = getDialect().getCountSql(sql);//"SELECT COUNT(0) " + removeSelect(sql);
	    Long totalCount = queryForObject(Long.class, coutsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			StringBuilder items = new StringBuilder();
	    	if ( 0 == totalCount.longValue()){
	    		items.append("{\"total\":0,\"rows\":[]}");
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		items.append("{\"total\":").append(totalCount).append(",\"rows\":");
	    		RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor(this.jsonReader);
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
	    			items.append( query(sqlforLimit, rse, params) );
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
	    			rse.setOffset(startRecord);
	        		items.append( query(sqlforLimit, rse, params) );
	    		}
	    		items.append('}');
	    	}
	    	return items.toString();
	    }
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ICommonDaoXmlExt#fillJsonByPagedQuery(java.lang.String, java.lang.String, int, int, org.uorm.dao.common.SqlParameter[])
	 */
	@Override
	public String fillJsonByPagedQuery(String countsql, String sql,
			int startPage, int pageSize, SqlParameter... params)
			throws SQLException {
	    Long totalCount = queryForObject(Long.class, countsql, params);
	    if(totalCount != null){
			int pagecount = (int)Math.ceil((double)totalCount / (double)pageSize);
			int curpage = startPage;
			if(curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			StringBuilder items = new StringBuilder();
	    	if ( 0 == totalCount.longValue()){
	    		items.append("{\"total\":0,\"rows\":[]}");
	    	} else {
	    		if(getDialect() == null){
	    			throw new SQLException("can not find SQL Dialect.");
	    		}
	    		items.append("{\"total\":").append(totalCount).append(",\"rows\":");
	    		RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor(this.jsonReader);
	    		String sqlforLimit = null;
	    		if(getDialect().supportsOffset()){
	    			sqlforLimit = getDialect().getLimitString(sql, startRecord, pageSize);
	    			items.append( query(sqlforLimit, rse, params) );
	    		}else{
		    		int endRecord = (curpage + 1) * pageSize;
		    		if(endRecord > totalCount.intValue()){
		    			endRecord = totalCount.intValue();
		    		}
	    			sqlforLimit = getDialect().getLimitString(sql, 0, endRecord);
	    			rse.setOffset(startRecord);
	        		items.append( query(sqlforLimit, rse, params) );
	    		}
	    		items.append('}');
	    	}
	    	return items.toString();
	    }
		return null;
	}

	/**
	 * @return the xmlReader
	 */
	public IXmlReader getXmlReader() {
		return xmlReader;
	}

	/**
	 * @param xmlReader the xmlReader to set
	 */
	public void setXmlReader(IXmlReader xmlReader) {
		this.xmlReader = xmlReader;
	}

	/**
	 * @param jsonReader the jsonReader to set
	 */
	public void setJsonReader(IJsonReader jsonReader) {
		this.jsonReader = jsonReader;
	}

	/**
	 * @param rootName the rootName to set
	 */
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

}
