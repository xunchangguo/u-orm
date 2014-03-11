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
package org.uorm.orm.mapping;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class ObjectReader implements IObjectReader {

	/* (non-Javadoc)
	 * @see jetsennet.sqlclient.orm.mapping.IObjectReader#read(java.lang.Class, java.sql.ResultSet, java.sql.ResultSetMetaData)
	 */
	@SuppressWarnings("unchecked")
	public <T> T read(Class<T> cls, ResultSet result,
			ResultSetMetaData rsmd) throws Exception {
		int count = rsmd.getColumnCount();
		Map<String, Method> setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(cls);
		
		if( ((count == 1) && (setMethods == null || setMethods.isEmpty())) ||
				((count == 1) && (ObjectMappingCache.getInstance().getClassMapping(cls) == null)) ) {
			//for simple obj class
			Object val = result.getObject(1);
			if(val == null){
				return null;
			}else{
				if(GenericConverterFactory.getInstance().needConvert(val.getClass(), cls)) {
					try{
						return (T)getValue(result, 1, cls);
					} catch (SQLException e) {
						ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), cls);
						if(converter != null) {
							return (T)converter.convert(val, cls);
						}
						throw e;
					}
				}else{
					return (T)val;
				}
			}
		} else {
			T instance = cls.newInstance();
			for(int i = 1; i <= count; i++){
				String columnName = rsmd.getColumnLabel(i);//rsmd.getColumnName(i);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				Method setterMethod = setMethods.get(columnName.toUpperCase());
				if(setterMethod != null) {
					Class<?> memberType = setterMethod.getParameterTypes()[0];
					Object val = result.getObject(i);
					if(val != null ){
						if(val instanceof Clob){
							if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType))) {
								byte[] lobvar = clob2bytes((Clob)val);
								setterMethod.invoke(instance, lobvar);
							}else if(String       .class.equals(memberType)) {
								setterMethod.invoke(instance, clob2String((Clob)val));
							}else{
								val = getValue(result, i, memberType);
								setterMethod.invoke(instance, val);
							}
						}else if(val instanceof Blob) {
							if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType))) {
								byte[] lobvar = blob2bytes((Blob)val);
								setterMethod.invoke(instance, lobvar);
							}else{
								val = getValue(result, i, memberType);
								setterMethod.invoke(instance, val);
							}
						}else{
							if(GenericConverterFactory.getInstance().needConvert(val.getClass(), cls)) {//convert
								try{
									val = getValue(result, i, memberType);
								} catch (SQLException e) {
									ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), cls);
									if(converter != null) {
										val = converter.convert(val, cls);
									}
									throw e;
								}
							}
							setterMethod.invoke(instance, val);
						}
					}
				}
			}
			return instance;
		}
	}
	
	protected static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	protected byte[] clob2bytes(Clob clob) throws SQLException {
		if(clob != null){
			String date = clob.getSubString(1, (int) clob.length());
			return date.getBytes();
		}
		return null;
	}
	
	protected String clob2String(Clob clob) throws SQLException {
		if(clob != null){
			String date = clob.getSubString(1, (int) clob.length());
			return date;
		}
		return null;
	}
	
	protected byte[] blob2bytes(Blob blob) throws SQLException {
		if(blob != null){
			byte[] data = blob.getBytes(1, (int) blob.length());
			return data;
		}
		return null;
	}
	
	protected Object getValue(ResultSet result, int columnIndex, Class<?> memberType) throws SQLException {
		if(Array.class.equals(memberType))         return result.getArray(columnIndex);
        if(AsciiStream  .class.equals(memberType)) return result.getAsciiStream(columnIndex);
        
        if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType)))   return result.getBytes(columnIndex);
        if((Boolean      .class.equals(memberType)) || (boolean.class.equals(memberType)))  return result.getBoolean(columnIndex);
        if((Byte         .class.equals(memberType)) || (byte.class.equals(memberType)))     return result.getByte(columnIndex);
        if((Double       .class.equals(memberType)) || (double.class.equals(memberType)))   return result.getDouble(columnIndex);
        if((Float        .class.equals(memberType)) || (float.class.equals(memberType)))    return result.getFloat(columnIndex);
        if((Integer      .class.equals(memberType)) || (int.class.equals(memberType)))      return result.getInt(columnIndex);
        if((Long         .class.equals(memberType)) || (long.class.equals(memberType)))     return result.getLong(columnIndex);
        if((Short        .class.equals(memberType)) || (short.class.equals(memberType)))    return result.getShort(columnIndex);

        if(BigInteger   .class.equals(memberType)) {
        	BigDecimal val = result.getBigDecimal(columnIndex);
        	if(val != null){
        		return val.toBigInteger();
        	}else{
        		return null;
        	}
        }
        if(BigDecimal   .class.equals(memberType)) return result.getBigDecimal(columnIndex);
        if(InputStream  .class.equals(memberType)) return result.getBinaryStream(columnIndex);
        if(Blob         .class.equals(memberType)) return result.getBlob(columnIndex);
        if(Reader       .class.equals(memberType)) return result.getCharacterStream(columnIndex);
        if(Clob         .class.equals(memberType)) return result.getClob(columnIndex);
        if(java.sql.Date.class.equals(memberType)) return result.getDate(columnIndex);
        if(java.util.Date.class.equals(memberType)) return result.getTimestamp(columnIndex);
        if(Ref          .class.equals(memberType)) return result.getRef(columnIndex);
        if(String       .class.equals(memberType)) return result.getString(columnIndex);
        if(Time         .class.equals(memberType)) return result.getTime(columnIndex);
        if(Timestamp    .class.equals(memberType)) return result.getTimestamp(columnIndex);
        if(URL          .class.equals(memberType)) return result.getURL(columnIndex);
        if(Object       .class.equals(memberType)) return result.getObject(columnIndex);
        if(SQLXML       .class.equals(memberType)) return result.getSQLXML(columnIndex);
        return result.getObject(columnIndex);
	}


	/* (non-Javadoc)
	 * @see jetsennet.sqlclient.orm.mapping.IObjectReader#readValue2Map(java.lang.Object)
	 */
	public Map<String, Object> readValue2Map(Object pojo)
			throws Exception {
		Map<String, Method> getMethodMap = ObjectMappingCache.getInstance().getPojoGetMethod(pojo.getClass());
		Map<String, Object> model = new HashMap<String, Object>();
		for( String colName : getMethodMap.keySet() ) {
			Method getMethod = getMethodMap.get(colName);
			Object val = getMethod.invoke(pojo);
			model.put(colName, val);
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see jetsennet.sqlclient.orm.mapping.IObjectReader#writePkVal2Pojo(java.lang.Object, java.lang.Object, java.lang.String)
	 */
	public boolean writePkVal2Pojo(Object pojo, Object pkval,
			String pkcolumnName) throws Exception {
		Map<String, Method> setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(pojo.getClass());
		Method setMethod = setMethods.get(pkcolumnName);
		if(setMethod != null){
			setMethod.invoke(pojo, pkval);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IObjectReader#readToMap(java.sql.ResultSet, java.sql.ResultSetMetaData)
	 */
	@Override
	public Map<String, Object> readToMap(ResultSet rs, ResultSetMetaData rsmd) throws Exception {
		Map<String, Object> valMap = new HashMap<String, Object>();
		int count = rsmd.getColumnCount();
		for(int i = 1; i <= count; i++){
			String columnName = rsmd.getColumnLabel(i);//rsmd.getColumnName(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}
			Object val = rs.getObject(i);
			valMap.put(columnName.toUpperCase(), val);
		}
		return valMap;
	}
	
	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IObjectReader#readToArray(java.sql.ResultSet, java.sql.ResultSetMetaData)
	 */
	@Override
	public Object[] readToArray(ResultSet rs, ResultSetMetaData rsmd)
			throws Exception {
		int count = rsmd.getColumnCount();
		if(count > 0) {
			Object[] values = new Object[count];
			for(int i = 1; i <= count; i++){
				values[i - 1] = rs.getObject(i);
			}
			return values;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IObjectReader#getClassMapping(java.lang.Class)
	 */
	@Override
	public <T> ClassMapping getClassMapping(Class<T> cls) {
		return ObjectMappingCache.getInstance().getClassMapping(cls);
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IObjectReader#getClassPrimaryKeys(java.lang.Class, java.lang.String)
	 */
	@Override
	public FieldMapping[] getClassPrimaryKeys(Class<?> cls, String keyOrder) {
		Map<String, FieldMapping> fieldMapping = ObjectMappingCache.getInstance().getObjectFieldMap(cls);
		if(fieldMapping != null) {
			if(keyOrder == null || keyOrder.length() == 0){//only one key
				for (FieldMapping field : fieldMapping.values()) {
					if(field.primary()){
						return new FieldMapping[]{field};
					}
				}
			} else {
				String[] keyOArray = keyOrder.split(",");
				FieldMapping[] fields = new FieldMapping[keyOArray.length];
				int idx = 0;
				for ( String key : keyOArray ) {
					fields[idx] = fieldMapping.get(key);
					idx ++;
				}
				return fields;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IObjectReader#getObjectFieldMap(java.lang.Class)
	 */
	@Override
	public Map<String, FieldMapping> getObjectFieldMap(Class<?> cls) {
		return ObjectMappingCache.getInstance().getObjectFieldMap(cls);
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IObjectReader#getTargetSqlClass(int)
	 */
	@Override
	public Class<?> getTargetSqlClass(int sqlType) {
		Class<?> cls = null;
		switch (sqlType) {
		case Types.DATE:
			cls = java.sql.Date.class;
			break;
		case Types.TIME:
			cls = java.sql.Time.class;
			break;
		case Types.TIMESTAMP:
			cls = java.sql.Timestamp.class;
			break;
//		case Types.DOUBLE:
//			cls = Double.class;
//			break;
//		case Types.FLOAT:
//			cls = Float.class;
//			break;
//		case Types.INTEGER:
//			cls = Integer.class;
//			break;
//		case Types.ARRAY:
//			cls = java.sql.Array.class;
//			break;
//		case Types.BLOB:
//			cls = java.sql.Blob.class;
//			break;
//		case Types.CLOB:
//			cls = java.sql.Clob.class;
//			break;
//		case Types.BIGINT:
//			break;
//		case Types.BINARY:
//			break;
//		case Types.BIT:
//			break;
//		case Types.BOOLEAN:
//			break;
//		case Types.CHAR:
//			break;
//		case Types.DATALINK:
//			break;
//		case Types.DECIMAL:
//			break;
//		case Types.DISTINCT:
//			break;
//		case Types.JAVA_OBJECT:
//			break;
//		case Types.LONGNVARCHAR:
//			break;
//		case Types.LONGVARBINARY:
//			break;
//		case Types.LONGVARCHAR:
//			break;
//		case Types.NCHAR:
//			break;
//		case Types.NCLOB:
//			break;
//		case Types.NUMERIC:
//			break;
//		case Types.NVARCHAR:
//			break;
//		case Types.OTHER:
//			break;
//		case Types.REAL:
//			break;
//		case Types.REF:
//			break;
//		case Types.ROWID:
//			break;
//		case Types.SMALLINT:
//			break;
//		case Types.SQLXML:
//			break;
//		case Types.STRUCT:
//			break;
//		case Types.TINYINT:
//			break;
//		case Types.VARBINARY:
//			break;
//		case Types.VARCHAR:
//			break;
		default:
			break;
		}
		return cls;
	}
}
