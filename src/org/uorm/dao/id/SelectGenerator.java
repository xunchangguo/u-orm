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
package org.uorm.dao.id;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.uorm.dao.common.ICommonDao;
import org.uorm.dao.dialect.Dialect;
import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.mapping.ObjectMappingCache;
import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-30       郭训长            创建<br/>
 */
public class SelectGenerator implements IdentifierGenerator {
	public final static String _DEFAULT_TABLE_NAME = "IDENTIFIER_TABLE";
	public final static String _DEFAULT_NAME_CLOUMN = "TABLE_NAME";
	public final static String _DEFAULT_VALUE_CLOUMN = "SERIALIZE_VALUE";
	private String tablename = null;
	private String namecolumn = null;
	private String valuecolumn = null;

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Object, org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable generate(Dialect dialect, ICommonDao dao, Object pojo,
			FieldMapping idFieldInfo, boolean writevalue) throws SQLException {
		try{
			Class<?> cls = pojo.getClass();
			Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(cls);
			PropertyDescriptor prop = props.get(idFieldInfo.columnName());
			Class<?> fieldType = prop.getPropertyType();
			if( Long.class.isAssignableFrom( fieldType ) 
					|| Integer.class.isAssignableFrom( fieldType )
					|| Short.class.isAssignableFrom( fieldType )
					|| BigInteger.class.isAssignableFrom( fieldType )
					|| BigDecimal.class.isAssignableFrom( fieldType ) ){
				ClassMapping clsmapping = ObjectMappingCache.getInstance().getClassMapping(cls);
				String tableName = null;
				if ( clsmapping != null ){
					tableName = clsmapping.tableName();
					if(clsmapping.pattern() != null && clsmapping.pattern().length() > 0) {
						tableName += Utils.dateFormat(new Date(), clsmapping.pattern());
					}
				}
				if(tableName == null) {
					tableName = cls.getSimpleName().toUpperCase();
				}
				int update = dao.update(generateUpdate(tableName, 0));
				Object idval = null;
				if(update > 0){
					idval = dao.querySingleObject(fieldType, generateQuery(tableName));
				}else{
					dao.execute(generateInsert(tableName, 0));
					if( Long.class.isAssignableFrom( fieldType ) ){
						idval = Long.valueOf(1L);
					} else if ( Integer.class.isAssignableFrom( fieldType ) ) {
						idval = Integer.valueOf(1);
					} else if ( Short.class.isAssignableFrom( fieldType ) ) {
						idval = Short.valueOf((short) 1);
					} else if ( BigInteger.class.isAssignableFrom( fieldType ) ) {
						idval = BigInteger.valueOf(1L);
					} else if ( BigDecimal.class.isAssignableFrom( fieldType ) ) {
						idval = BigDecimal.valueOf(1L);
					}
				}
				if(idval != null) {
					if ( writevalue ) {
						prop.getWriteMethod().invoke(pojo, idval);
					}
				}
				return (Serializable) idval;
			} else {
				throw new SQLException( "Unknown integral data type for ids : " + fieldType.getName() );
			}
		}catch (SQLException e) {
			throw e;
		}catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private String generateQuery(String tablename) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(getValuecolumn()).append(" FROM ").append(getTablename());
		sql.append(" WHERE ").append(getNamecolumn()).append(" = '").append(tablename).append('\'');
		return sql.toString();
	}

	private String generateUpdate(String tablename, int num) {
		StringBuffer sql = new StringBuffer("UPDATE ");
		sql.append(getTablename());
		if(num > 0) {
			sql.append(" SET ").append(getValuecolumn()).append(" = ").append(getValuecolumn()).append('+').append(num);
		}else{
			sql.append(" SET ").append(getValuecolumn()).append(" = ").append(getValuecolumn()).append("+1");
		}
		sql.append(" WHERE ").append(getNamecolumn()).append(" = '").append(tablename).append('\'');
		return sql.toString();
	}

	private String generateInsert(String tablename, int num) {
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		sql.append(getTablename());
		sql.append('(').append(getNamecolumn()).append(", ").append(getValuecolumn()).append(") VALUES('");
		if(num > 0){
			sql.append(tablename).append("', ").append(num).append(')');
		}else{
			sql.append(tablename).append("', 1)");
		}
		return sql.toString();
	}

	/**
	 * @return the tablename
	 */
	public String getTablename() {
		if(tablename == null){
			tablename = _DEFAULT_TABLE_NAME;
		}
		return tablename;
	}

	/**
	 * @param tablename the tablename to set
	 */
	public void setTableName(String tablename) {
		this.tablename = tablename;
	}

	/**
	 * @return the namecolumn
	 */
	public String getNamecolumn() {
		if(namecolumn == null){
			namecolumn = _DEFAULT_NAME_CLOUMN;
		}
		return namecolumn;
	}

	/**
	 * @param namecolumn the namecolumn to set
	 */
	public void setNameColumn(String namecolumn) {
		this.namecolumn = namecolumn;
	}

	/**
	 * @return the valuecolumn
	 */
	public String getValuecolumn() {
		if(valuecolumn == null) {
			valuecolumn = _DEFAULT_VALUE_CLOUMN;
		}
		return valuecolumn;
	}

	/**
	 * @param valuecolumn the valuecolumn to set
	 */
	public void setValueColumn(String valuecolumn) {
		this.valuecolumn = valuecolumn;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Object[], org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable[] generate(Dialect dialect, ICommonDao dao,
			Object[] pojos, FieldMapping idFieldInfo, boolean writevalue)
					throws SQLException {
		Serializable[] sids = new Serializable[pojos.length];
		try{
			Map<Class<?>, Integer> clsSet = new LinkedHashMap<Class<?>, Integer>();
			for(Object pojo : pojos){
				Class<?> cls = pojo.getClass();
				Integer v = clsSet.get(cls);
				if(v == null) {
					clsSet.put(cls, 1);
				}else{
					clsSet.put(cls, (v.intValue() + 1));
				}
			}
			int objidx = 0;
			for(Class<?> cls : clsSet.keySet()) {
				Integer numids = clsSet.get(cls);
				Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(cls);
				PropertyDescriptor prop = props.get(idFieldInfo.columnName());
				Class<?> fieldType = prop.getPropertyType();
				if( Long.class.isAssignableFrom( fieldType ) 
						|| Integer.class.isAssignableFrom( fieldType )
						|| Short.class.isAssignableFrom( fieldType )
						|| BigInteger.class.isAssignableFrom( fieldType )
						|| BigDecimal.class.isAssignableFrom( fieldType ) ){
					ClassMapping clsmapping = ObjectMappingCache.getInstance().getClassMapping(cls);
					String tableName = null;
					if ( clsmapping != null ){
						tableName = clsmapping.tableName();
						if(clsmapping.pattern() != null && clsmapping.pattern().length() > 0) {
							tableName += Utils.dateFormat(new Date(), clsmapping.pattern());
						}
					}
					if(tableName == null) {
						tableName = cls.getSimpleName().toUpperCase();
					}
					int update = dao.update(generateUpdate(tableName, numids));
					if(update > 0){
						Object maxidval = dao.querySingleObject(fieldType, generateQuery(tableName));
						if( Long.class.isAssignableFrom( fieldType ) ){
							Long maxlongval = (Long)maxidval;
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = maxlongval - numids + 1;
								}else{
									sids[objidx] = (Long)sids[objidx-1] + 1;
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( Integer.class.isAssignableFrom( fieldType ) ) {
							Integer maxIntval = (Integer)maxidval;
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = maxIntval - numids + 1;
								}else{
									sids[objidx] = (Integer)sids[objidx-1] + 1;
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( Short.class.isAssignableFrom( fieldType ) ) {
							Short maxshortval = (Short)maxidval;
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = maxshortval - numids + 1;
								}else{
									sids[objidx] = (Short)sids[objidx-1] + 1;
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( BigInteger.class.isAssignableFrom( fieldType ) ) {
							BigInteger maxbigval = (BigInteger)maxidval;
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = maxbigval.subtract(BigInteger.valueOf(numids + 1l));
								}else{
									sids[objidx] = ((BigInteger)sids[objidx-1]).add(BigInteger.ONE);
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( BigDecimal.class.isAssignableFrom( fieldType ) ) {
							BigDecimal maxbdval = (BigDecimal)maxidval;
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = maxbdval.subtract(BigDecimal.valueOf(numids + 1l));
								}else{
									sids[objidx] = ((BigDecimal)sids[objidx-1]).add(BigDecimal.ONE);
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						}
					}else{
						dao.execute(generateInsert(tableName, numids));
						if( Long.class.isAssignableFrom( fieldType ) ){
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = 1l;
								}else{
									sids[objidx] = (Long)sids[objidx-1] + 1;
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( Integer.class.isAssignableFrom( fieldType ) ) {
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = Integer.valueOf(1);
								}else{
									sids[objidx] = (Integer)sids[objidx-1] + 1;
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( Short.class.isAssignableFrom( fieldType ) ) {
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = Short.valueOf((short) 1);
								}else{
									sids[objidx] = (Short)sids[objidx-1] + 1;
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( BigInteger.class.isAssignableFrom( fieldType ) ) {
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = BigInteger.ONE;
								}else{
									sids[objidx] = ((BigInteger)sids[objidx-1]).add(BigInteger.ONE);
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						} else if ( BigDecimal.class.isAssignableFrom( fieldType ) ) {
							for(int i = 0; i < numids; i ++){
								if(i == 0) {
									sids[objidx] = BigDecimal.ONE;
								}else{
									sids[objidx] = ((BigDecimal)sids[objidx-1]).add(BigDecimal.ONE);
								}
								if ( writevalue ) {
									prop.getWriteMethod().invoke(pojos[objidx], sids[objidx]);
								}
								objidx ++;
							}
						}
					}
				} else {
					throw new SQLException( "Unknown internal data type for ids : " + fieldType.getName() + ", " + fieldType );
				}
			}
			return sids;
		}catch (SQLException e) {
			throw e;
		}catch (Exception e) {
			throw new SQLException(e);
		}
		//		throw new UnsupportedOperationException("unsupport until now.");
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Class, java.util.Map, org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable generate(Dialect dialect, ICommonDao dao, Class<?> cls,
			Map<String, Object> valmap, FieldMapping idFieldInfo,
			boolean writevalue) throws SQLException {
		try{
			Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(cls);
			PropertyDescriptor prop = props.get(idFieldInfo.columnName());
			Class<?> fieldType = prop.getPropertyType();
			if( Long.class.isAssignableFrom( fieldType ) 
					|| Integer.class.isAssignableFrom( fieldType )
					|| Short.class.isAssignableFrom( fieldType )
					|| BigInteger.class.isAssignableFrom( fieldType )
					|| BigDecimal.class.isAssignableFrom( fieldType ) ){
				ClassMapping clsmapping = ObjectMappingCache.getInstance().getClassMapping(cls);
				String tableName = null;
				if ( clsmapping != null ){
					tableName = clsmapping.tableName();
					if(clsmapping.pattern() != null && clsmapping.pattern().length() > 0) {
						tableName += Utils.dateFormat(new Date(), clsmapping.pattern());
					}
				}
				if(tableName == null) {
					tableName = cls.getSimpleName().toUpperCase();
				}
				int update = dao.update(generateUpdate(tableName, 0));
				Object idval = null;
				if(update > 0){
					idval = dao.querySingleObject(fieldType, generateQuery(tableName));
				}else{
					dao.execute(generateInsert(tableName, 0));
					if( Long.class.isAssignableFrom( fieldType ) ){
						idval = Long.valueOf(1L);
					} else if ( Integer.class.isAssignableFrom( fieldType ) ) {
						idval = Integer.valueOf(1);
					} else if ( Short.class.isAssignableFrom( fieldType ) ) {
						idval = Short.valueOf((short) 1);
					} else if ( BigInteger.class.isAssignableFrom( fieldType ) ) {
						idval = BigInteger.valueOf(1L);
					} else if ( BigDecimal.class.isAssignableFrom( fieldType ) ) {
						idval = BigDecimal.valueOf(1L);
					}
				}
				if(idval != null) {
					if ( writevalue ) {
						valmap.put(idFieldInfo.columnName(), idval);
					}
				}
				return (Serializable) idval;
			} else {
				throw new SQLException( "Unknown integral data type for ids : " + fieldType.getName() );
			}
		}catch (SQLException e) {
			throw e;
		}catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Class, java.util.Map<java.lang.String,java.lang.Object>[], org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable[] generate(Dialect dialect, ICommonDao dao,
			Class<?> cls, Map<String, Object>[] valmaps,
			FieldMapping idFieldInfo, boolean writevalue) throws SQLException {
		Serializable[] sids = new Serializable[valmaps.length];
		try{
			int objidx = 0;
			Integer numids = sids.length;
			Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(cls);
			PropertyDescriptor prop = props.get(idFieldInfo.columnName());
			Class<?> fieldType = prop.getPropertyType();
			if( Long.class.isAssignableFrom( fieldType ) 
					|| Integer.class.isAssignableFrom( fieldType )
					|| Short.class.isAssignableFrom( fieldType )
					|| BigInteger.class.isAssignableFrom( fieldType )
					|| BigDecimal.class.isAssignableFrom( fieldType ) ){
				ClassMapping clsmapping = ObjectMappingCache.getInstance().getClassMapping(cls);
				String tableName = null;
				if ( clsmapping != null ){
					tableName = clsmapping.tableName();
					if(clsmapping.pattern() != null && clsmapping.pattern().length() > 0) {
						tableName += Utils.dateFormat(new Date(), clsmapping.pattern());
					}
				}
				if(tableName == null) {
					tableName = cls.getSimpleName().toUpperCase();
				}
				int update = dao.update(generateUpdate(tableName, numids));
				if(update > 0){
					Object maxidval = dao.querySingleObject(fieldType, generateQuery(tableName));
					if( Long.class.isAssignableFrom( fieldType ) ){
						Long maxlongval = (Long)maxidval;
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = maxlongval - numids + 1;
							}else{
								sids[objidx] = (Long)sids[objidx-1] + 1;
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( Integer.class.isAssignableFrom( fieldType ) ) {
						Integer maxIntval = (Integer)maxidval;
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = maxIntval - numids + 1;
							}else{
								sids[objidx] = (Integer)sids[objidx-1] + 1;
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( Short.class.isAssignableFrom( fieldType ) ) {
						Short maxshortval = (Short)maxidval;
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = maxshortval - numids + 1;
							}else{
								sids[objidx] = (Short)sids[objidx-1] + 1;
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( BigInteger.class.isAssignableFrom( fieldType ) ) {
						BigInteger maxbigval = (BigInteger)maxidval;
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = maxbigval.subtract( BigInteger.valueOf(numids + 1l) );
							}else{
								sids[objidx] = ((BigInteger)sids[objidx-1]).add(BigInteger.ONE);
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( BigDecimal.class.isAssignableFrom( fieldType ) ) {
						BigDecimal maxbdval = (BigDecimal)maxidval;
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = maxbdval.subtract( BigDecimal.valueOf(numids + 1l) );
							}else{
								sids[objidx] = ((BigDecimal)sids[objidx-1]).add(BigDecimal.ONE);
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					}
				}else{
					dao.execute(generateInsert(tableName, numids));
					if( Long.class.isAssignableFrom( fieldType ) ){
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = 1l;
							}else{
								sids[objidx] = (Long)sids[objidx-1] + 1;
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( Integer.class.isAssignableFrom( fieldType ) ) {
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = Integer.valueOf(1);
							}else{
								sids[objidx] = (Integer)sids[objidx-1] + 1;
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( Short.class.isAssignableFrom( fieldType ) ) {
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = Short.valueOf((short) 1);
							}else{
								sids[objidx] = (Short)sids[objidx-1] + 1;
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( BigInteger.class.isAssignableFrom( fieldType ) ) {
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = BigInteger.ONE;
							}else{
								sids[objidx] = ((BigInteger)sids[objidx-1]).add(BigInteger.ONE);
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					} else if ( BigDecimal.class.isAssignableFrom( fieldType ) ) {
						for(int i = 0; i < numids; i ++){
							if(i == 0) {
								sids[objidx] = BigDecimal.ONE;
							}else{
								sids[objidx] = ((BigDecimal)sids[objidx-1]).add(BigDecimal.ONE);
							}
							if ( writevalue ) {
								valmaps[i].put(idFieldInfo.columnName(), sids[objidx]);
							}
							objidx ++;
						}
					}
				}
			} else {
				throw new SQLException( "Unknown integral data type for ids : " + fieldType.getName() );
			}
			return sids;
		}catch (SQLException e) {
			throw e;
		}catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/**
	 * update
	 * @param dao
	 * @param generateUpdate
	 * @return
	 * @throws SQLException 
	 */
	//	private int update(ICommonDao dao, final String updatesql) throws SQLException {
	//		return dao.execute(new ConnectionCallback<Integer>() {
	//
	//			@Override
	//			public Integer doInConnection(Connection connection)
	//					throws SQLException {
	//				Statement stmt = connection.createStatement();
	//				return stmt.executeUpdate(updatesql);
	//			}
	//		});
	//	}
}
