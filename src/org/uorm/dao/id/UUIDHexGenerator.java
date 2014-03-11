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
import java.sql.SQLException;
import java.util.Map;

import org.uorm.dao.common.ICommonDao;
import org.uorm.dao.dialect.Dialect;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.mapping.ObjectMappingCache;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-30       郭训长            创建<br/>
 */
public class UUIDHexGenerator implements IdentifierGenerator {
	
	private String separator = "";

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Object, org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable generate(Dialect dialect, ICommonDao dao, Object pojo,
			FieldMapping idFieldInfo, boolean writevalue) throws SQLException {
		try{
			String idval = new StringBuffer( 36 )
			.append( UUIDHelper.format( UUIDHelper.getAddressInt() ) ).append( separator )
			.append( UUIDHelper.format( UUIDHelper.getJvmIdentifierInt() ) ).append( separator )
			.append( UUIDHelper.format( getHiTime() ) ).append( separator )
			.append( UUIDHelper.format( getLoTime() ) ).append( separator )
			.append( UUIDHelper.format( UUIDHelper.getCountShort() ) )
			.toString();

			Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(pojo.getClass());
			PropertyDescriptor prop = props.get(idFieldInfo.columnName());
			if ( writevalue ) {
				prop.getWriteMethod().invoke(pojo, idval);
			}
			return idval;
		}catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	protected short getHiTime() {
		return (short) ( System.currentTimeMillis() >>> 32 );
	}
	protected int getLoTime() {
		return (int) System.currentTimeMillis();
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Object[], org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable[] generate(Dialect dialect, ICommonDao dao,
			Object[] pojos, FieldMapping idFieldInfo, boolean writevalue)
			throws SQLException {
		Serializable[] vals = new Serializable[pojos.length];
		for(int i = 0; i < vals.length; i ++){
			vals[i] = generate(dialect, dao, pojos[i], idFieldInfo, writevalue);
		}
		return vals;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Class, java.util.Map, org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable generate(Dialect dialect, ICommonDao dao, Class<?> cls,
			Map<String, Object> valmap, FieldMapping idFieldInfo,
			boolean writevalue) throws SQLException {
		try{
			String idval = new StringBuffer( 36 )
			.append( UUIDHelper.format( UUIDHelper.getAddressInt() ) ).append( separator )
			.append( UUIDHelper.format( UUIDHelper.getJvmIdentifierInt() ) ).append( separator )
			.append( UUIDHelper.format( getHiTime() ) ).append( separator )
			.append( UUIDHelper.format( getLoTime() ) ).append( separator )
			.append( UUIDHelper.format( UUIDHelper.getCountShort() ) )
			.toString();
			if ( writevalue ) {
				valmap.put(idFieldInfo.columnName(), idval);
			}
			return idval;
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
		Serializable[] vals = new Serializable[valmaps.length];
		for(int i = 0; i < vals.length; i ++){
			vals[i] = generate(dialect, dao, cls, valmaps[i], idFieldInfo, writevalue);
		}
		return vals;
	}
}
