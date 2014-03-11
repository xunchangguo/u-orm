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
package org.uorm.orm.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-12-5       郭训长            创建<br/>
 */
public class BaseConverter implements IGenericConverter {

	/* (non-Javadoc)
	 * @see org.uorm.orm.convert.ITypeConverter#convert(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object convert(Object source, Class<?> targetType) throws TypeConvertException {
		try{
			if(source instanceof Calendar) {
				Calendar cal = (Calendar) source;
				return cal.getTime();
			}
			if(source instanceof Date) {
				Date date = (Date) source;
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				return cal;
			}
			// Source must be a Number
			Number num = (Number) source;
			// identity-equality is used since these are all final classes:
			if(targetType == Integer.class || targetType == Integer.TYPE)
				return Integer.valueOf(num.intValue());
			if(targetType == Byte.class || targetType == Byte.TYPE)
				return Byte.valueOf(num.byteValue());
			if(targetType == Double.class || targetType == Double.TYPE)
				return Double.valueOf(num.doubleValue());
			if(targetType == Float.class || targetType == Float.TYPE)
				return Float.valueOf(num.floatValue());
			if(targetType == Long.class || targetType == Long.TYPE)
				return Long.valueOf(num.longValue());
			if(targetType == Short.class || targetType == Short.TYPE)
				return Short.valueOf(num.shortValue());
			if(targetType == BigDecimal.class)
				return new BigDecimal(num.doubleValue());
			if(targetType == BigInteger.class)
				return BigInteger.valueOf(num.longValue());
		}catch(Exception e){
			throw new TypeConvertException(source, targetType, e);
		}
    	throw new TypeConvertException(source, targetType);
	}

	/* (non-Javadoc)
	 * @see org.uorm.orm.convert.IGenericConverter#getTargetTypes(java.lang.Class)
	 */
	@Override
	public List<Class<?>> getTargetTypes(Class<?> sourceType) {
		List<Class<?>> list = null;
        if(Date.class.isAssignableFrom(sourceType)) {
        	list = new ArrayList<Class<?>>(1);
            list.add(Calendar.class);
        } else if(Calendar.class.isAssignableFrom(sourceType)) {
        	list = new ArrayList<Class<?>>(1);
            list.add(Date.class);
        } else if(Number.class.isAssignableFrom(sourceType)) {
        	list = new ArrayList<Class<?>>(13);
            list.add(Byte.class);
            list.add(Double.class);
            list.add(Float.class);
            list.add(Integer.class);
            list.add(Long.class);
            list.add(Short.class);
            list.add(BigDecimal.class);
            list.add(Byte.TYPE);
            list.add(Double.TYPE);
            list.add(Float.TYPE);
            list.add(Integer.TYPE);
            list.add(Long.TYPE);
            list.add(Short.TYPE);
        }else{
        	list = Collections.emptyList();
        }
        return list;
	}

}
