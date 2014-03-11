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
import java.util.UUID;

import org.uorm.dao.common.ICommonDao;
import org.uorm.dao.dialect.Dialect;
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
public class UUIDGenerator implements IdentifierGenerator {
	private boolean simplerandom = true;

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGenerator#generate(org.uorm.dao.dialect.Dialect, org.uorm.dao.common.ICommonDao, java.lang.Object, org.uorm.orm.annotation.FieldMapping, boolean)
	 */
	@Override
	public Serializable generate(Dialect dialect, ICommonDao dao, Object pojo, FieldMapping idFieldInfo, boolean writevalue)
			throws SQLException {
		try{
			Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(pojo.getClass());
			PropertyDescriptor prop = props.get(idFieldInfo.columnName());
			Class<?> fieldType = prop.getPropertyType();
			UUID uuid = UUIDHelper.generateUUID(simplerandom);
			Serializable idval = null;
			if ( UUID.class.isAssignableFrom( fieldType ) ) {
				idval = uuid;
			} else if ( String.class.isAssignableFrom( fieldType ) ) {
				idval = uuid.toString();
			} else if ( byte[].class.isAssignableFrom( fieldType ) ) {
				byte[] bytes = new byte[16];
				System.arraycopy( Utils.long2bytes( uuid.getMostSignificantBits() ), 0, bytes, 0, 8 );
				System.arraycopy( Utils.long2bytes( uuid.getLeastSignificantBits() ), 0, bytes, 8, 8 );
				idval = bytes;
			} else {
				throw new SQLException( "Unanticipated return type [" + fieldType.getName() + "] for UUID conversion" );
			}
			if ( writevalue ) {
				prop.getWriteMethod().invoke(pojo, idval);
			}
			return idval;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/**
	 * @return the simplerandom
	 */
	public boolean isSimplerandom() {
		return simplerandom;
	}

	/**
	 * @param simplerandom the simplerandom to set
	 */
	public void setSimplerandom(boolean simplerandom) {
		this.simplerandom = simplerandom;
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
			Map<String, PropertyDescriptor> props = ObjectMappingCache.getInstance().getObjectPropertyMap(cls);
			PropertyDescriptor prop = props.get(idFieldInfo.columnName());
			Class<?> fieldType = prop.getPropertyType();
			UUID uuid = UUIDHelper.generateUUID(simplerandom);
			Serializable idval = null;
			if ( UUID.class.isAssignableFrom( fieldType ) ) {
				idval = uuid;
			} else if ( String.class.isAssignableFrom( fieldType ) ) {
				idval = uuid.toString();
			} else if ( byte[].class.isAssignableFrom( fieldType ) ) {
				byte[] bytes = new byte[16];
				System.arraycopy( Utils.long2bytes( uuid.getMostSignificantBits() ), 0, bytes, 0, 8 );
				System.arraycopy( Utils.long2bytes( uuid.getLeastSignificantBits() ), 0, bytes, 8, 8 );
				idval = bytes;
			} else {
				throw new SQLException( "Unanticipated return type [" + fieldType.getName() + "] for UUID conversion" );
			}
			if ( writevalue ) {
				valmap.put(idFieldInfo.columnName(), idval);
			}
			return idval;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
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
