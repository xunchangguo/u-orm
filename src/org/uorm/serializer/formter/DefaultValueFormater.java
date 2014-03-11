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
package org.uorm.serializer.formter;

import java.math.BigDecimal;

import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-6-25       郭训长            创建<br/>
 */
public class DefaultValueFormater implements IValueFormater {
	private static final String EMPTY_STRING = "";
	private String dateParten = "yyyy-MM-dd HH:mm:ss";
	private String numberParten = null;

	/* (non-Javadoc)
	 * @see org.uorm.serializer.formter.IValueFormater#format(java.lang.Object)
	 */
	@Override
	public String format(Object obj) throws Exception {
		if(obj != null) {
			if (obj instanceof String) {
				return (String) obj;
			}
			if (obj instanceof byte[]) {
				return new String((byte[]) obj);
			}
			if (obj instanceof java.sql.Date || obj instanceof java.util.Date) {
				return Utils.dateFormat(obj, dateParten);
			}
			if(numberParten != null) {
				if (obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal) {
					return Utils.bigDicimalFormat(obj, numberParten);
				}
			}
			return obj.toString();
		}
		return EMPTY_STRING;
	}

	/**
	 * @param dateParten the dateParten to set
	 */
	public void setDateParten(String dateParten) {
		this.dateParten = dateParten;
	}

	/**
	 * @param numberParten the numberParten to set
	 */
	public void setNumberParten(String numberParten) {
		this.numberParten = numberParten;
	}

}
