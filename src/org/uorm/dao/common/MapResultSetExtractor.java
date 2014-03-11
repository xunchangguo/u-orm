package org.uorm.dao.common;

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
import java.util.HashMap;
import java.util.Map;

import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.convert.TypeConvertException;
import org.uorm.orm.mapping.AsciiStream;
import org.uorm.orm.mapping.ObjectMappingCache;

public class MapResultSetExtractor<K, V> implements ResultSetExtractor<Map<K, V>> {
	private int offset = 0;
	private int max = Integer.MAX_VALUE;
	private String keyFiled = null;
	private Class<V> vcls = null;
	private Class<K> kcls = null;
	
	/**
	 * @param keyFiled
	 * @param vcls
	 * @param kcls
	 */
	public MapResultSetExtractor(String keyFiled, Class<K> kcls, Class<V> vcls) {
		super();
		this.keyFiled = keyFiled;
		this.kcls = kcls;
		this.vcls = vcls;
	}


	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<K, V> extractData(ResultSet rs) throws SQLException {
		Map<K, V> results = new HashMap<K, V>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		while (rs.next()) {
			if(pos >= offset){
				try {
					int count = rsmd.getColumnCount();
					if(count <= 1) {
						throw new SQLException("number of columns in this ResultSet object <= 1, can not extrate to Map.");
					}
					K key = null;
					V v = null;
					Map<String, Method> setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(vcls);
					if( (setMethods == null || setMethods.isEmpty()) ||
							(ObjectMappingCache.getInstance().getClassMapping(vcls) == null) ) {
						if(count == 2) {
							//simple
							for(int i = 1; i <= count; i++){
								String columnName = rsmd.getColumnLabel(i);
								if (null == columnName || 0 == columnName.length()) {
									columnName = rsmd.getColumnName(i);
								}
								if(keyFiled.equalsIgnoreCase(columnName)) {
									key = readKey(rs, i);
								} else {
									v = readValue(rs, i);
								}
							}
						} else {
							//value is Map
							if(vcls.isAssignableFrom(Map.class)) {
								Map<String, Object> val = new HashMap<String, Object>();
								for(int i = 1; i <= count; i++){
									String columnName = rsmd.getColumnLabel(i);
									if (null == columnName || 0 == columnName.length()) {
										columnName = rsmd.getColumnName(i);
									}
									if(keyFiled.equalsIgnoreCase(columnName)) {
										key = readKey(rs, i);
										val.put(columnName.toUpperCase(), key);
									}else{
										val.put(columnName.toUpperCase(), rs.getObject(i));
									}
								}
								v = (V) val;
							} else if(vcls.isArray() && Object.class.isAssignableFrom(vcls.getComponentType())) {
								Object[] val = new Object[count];
								for(int i = 1; i <= count; i++){
									String columnName = rsmd.getColumnLabel(i);
									if (null == columnName || 0 == columnName.length()) {
										columnName = rsmd.getColumnName(i);
									}
									if(keyFiled.equalsIgnoreCase(columnName)) {
										key = readKey(rs, i);
										val[i-1] = key;
									}else{
										val[i-1] = rs.getObject(i);
									}
								}
								v = (V) val;
							} else {
								throw new SQLException("columns count > 2 and value class is not pojo and Map and Object Array.");
							}
						}
					} else {
						//value is pojo
						v = vcls.newInstance();
						for(int i = 1; i <= count; i++){
							String columnName = rsmd.getColumnLabel(i);//rsmd.getColumnName(i);
							if (null == columnName || 0 == columnName.length()) {
								columnName = rsmd.getColumnName(i);
							}
							Method setterMethod = setMethods.get(columnName.toUpperCase());
							if(setterMethod != null) {
								Class<?> memberType = setterMethod.getParameterTypes()[0];
								Object val = rs.getObject(i);
								if(val != null ){
									if(val instanceof Clob){
										if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType))) {
											byte[] lobvar = clob2bytes((Clob)val);
											setterMethod.invoke(v, lobvar);
										}else if(String       .class.equals(memberType)) {
											setterMethod.invoke(v, clob2String((Clob)val));
										}else{
											val = getValue(rs, i, memberType);
											setterMethod.invoke(v, val);
										}
									}else if(val instanceof Blob) {
										if((Byte[]       .class.equals(memberType)) || (byte[].class.equals(memberType))) {
											byte[] lobvar = blob2bytes((Blob)val);
											setterMethod.invoke(v, lobvar);
										}else{
											val = getValue(rs, i, memberType);
											setterMethod.invoke(v, val);
										}
									}else{
										if(GenericConverterFactory.getInstance().needConvert(val.getClass(), vcls)) {//convert
											try{
												val = getValue(rs, i, memberType);
											} catch (SQLException e) {
												ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), vcls);
												if(converter != null) {
													val = converter.convert(val, vcls);
												}
												throw e;
											}
										}
										setterMethod.invoke(v, val);
									}
								}
							}
							if(keyFiled.equalsIgnoreCase(columnName)) {
								key = readKey(rs, i);
							}
						}
					}
					
					results.put(key, v);
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if(results.size() >= max){
				break;
			}
			pos ++;
		}
		return results;
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
	@SuppressWarnings("unchecked")
	private K readKey(ResultSet rs, int columnIndex) throws SQLException {
		K key = null;
		Object val = rs.getObject(columnIndex);
		if(val != null){
			if(GenericConverterFactory.getInstance().needConvert(val.getClass(), kcls)) {
				try{
					key = (K)getValue(rs, columnIndex, kcls);
				} catch (SQLException e) {
					ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), kcls);
					if(converter != null) {
						try {
							key = (K)converter.convert(val, kcls);
						} catch (TypeConvertException e1) {
							throw e;
						}
					}
				}
			} else {
				key = (K) val;
			}
		}
		return key;
	}
	
	@SuppressWarnings("unchecked")
	private V readValue(ResultSet rs, int columnIndex) throws SQLException {
		V v = null;
		Object val = rs.getObject(columnIndex);
		if(val != null){
			if(GenericConverterFactory.getInstance().needConvert(val.getClass(), vcls)) {
				try{
					v = (V)getValue(rs, columnIndex, vcls);
				} catch (SQLException e) {
					ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(val.getClass(), vcls);
					if(converter != null) {
						try {
							v = (V)converter.convert(val, vcls);
						} catch (TypeConvertException e1) {
							throw e;
						}
					}
				}
			} else {
				v = (V) val;
			}
		}
		return v;
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
	/**
	 * @return the keyFiled
	 */
	public String getKeyFiled() {
		return keyFiled;
	}

	/**
	 * @param keyFiled the keyFiled to set
	 */
	public void setKeyFiled(String keyFiled) {
		this.keyFiled = keyFiled;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}


}
