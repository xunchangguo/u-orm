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
package org.uorm.serializer.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.convert.TypeConvertException;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-6-25       郭训长            创建<br/>
 */
public class StringConverter implements ITypeConverter {
	private String dateParten = "yyyy-MM-dd HH:mm:ss";

	/* (non-Javadoc)
	 * @see org.uorm.orm.convert.ITypeConverter#convert(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object convert(Object source, Class<?> targetType)
			throws TypeConvertException {
		if(source != null) {
			String srcval = (String)source;
			if(targetType == Integer.class || targetType == Integer.TYPE)
				return Integer.valueOf(srcval);
			if(targetType == Byte.class || targetType == Byte.TYPE)
				return Byte.valueOf(srcval);
			if(targetType == Double.class || targetType == Double.TYPE)
				return Double.valueOf(srcval);
			if(targetType == Float.class || targetType == Float.TYPE)
				return Float.valueOf(srcval);
			if(targetType == Long.class || targetType == Long.TYPE)
				return Long.valueOf(srcval);
			if(targetType == Short.class || targetType == Short.TYPE)
				return Short.valueOf(srcval);
			if(targetType == Boolean.class || targetType == Boolean.TYPE)
				return Boolean.valueOf(srcval);
			if(targetType == BigDecimal.class)
				return new BigDecimal(srcval);
			if(targetType == BigInteger.class)
				return new BigInteger(srcval);
			if(targetType == byte[].class)
				return srcval.getBytes();
			if(targetType == java.sql.Timestamp.class){
				Long time = parserDate(srcval);
				if(time == null){
					return null;
				}else{
					return new java.sql.Timestamp(time);
				}
			}
			if(targetType == java.sql.Date.class){
				Long time = parserDate(srcval);
				if(time == null){
					return null;
				}else{
					return new java.sql.Date(time);
				}
			}
			if(targetType == java.util.Date.class){
				Long time = parserDate(srcval);
				if(time == null){
					return null;
				}else{
					return new java.util.Date(time);
				}
			}
			throw new TypeConvertException(source, targetType);
		}
		return null;
	}
	
	private Long parserDate(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateParten);
		Date dt = null;
		try{
			dt = sdf.parse(strDate);
		}catch(Exception e){
			return null;
		}
		return dt.getTime();
	}

	/**
	 * @return the dateParten
	 */
	public String getDateParten() {
		return dateParten;
	}

	/**
	 * @param dateParten the dateParten to set
	 */
	public void setDateParten(String dateParten) {
		this.dateParten = dateParten;
	}

}
