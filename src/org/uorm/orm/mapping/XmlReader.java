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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-1-31       郭训长            创建<br/>
 */
public class XmlReader implements IXmlReader {
	private String dataFormatParten = "yyyy-MM-dd HH:mm:ss";

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IXmlReader#read(java.lang.String, java.sql.ResultSet, java.sql.ResultSetMetaData)
	 */
	@Override
	public Element read(String itemname, ResultSet result,
			ResultSetMetaData rsmd) throws Exception {
		Element dataElement = DocumentHelper.createElement(itemname);
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}
			int columnType = rsmd.getColumnType(i);
			Element curElement = dataElement.addElement(columnName);
			String val = getValue(result, i, columnType);
			if (val != null) {
				curElement.addText(val);
//				if (count<=2) {
//					curElement.addAttribute("type", rsmd.getColumnTypeName(i));
//				}
			}
		}
		return dataElement;
	}

	/**
	 * @param result
	 * @param columnIndex
	 * @param columnType
	 * @return
	 * @throws SQLException 
	 */
	protected String getValue(ResultSet result, int columnIndex, int columnType) throws SQLException {
		String val = null;
		switch (columnType) {
		case Types.TIMESTAMP:
			java.sql.Timestamp timestamp = result.getTimestamp(columnIndex);
			if(timestamp != null) {
				val = Utils.dateFormat(timestamp, dataFormatParten);
			}
			break;
		case Types.DATE:
			java.sql.Date date = result.getDate(columnIndex);
			if(date != null) {
				val = Utils.dateFormat(date, dataFormatParten);
			}
			break;
		case Types.TIME:
			java.sql.Time time = result.getTime(columnIndex);
			if(time != null) {
				val = Utils.dateFormat(time, "HH:mm:ss");
			}
			break;
		case Types.CLOB:
			val = result.getString(columnIndex);
			break;
		case Types.BLOB:
			java.sql.Blob blob = result.getBlob(columnIndex);
			if(blob != null) {
				val = new String(blob.getBytes(1, (int) blob.length()));
			}
			break;

		default:
			val = result.getString(columnIndex);
			break;
		}
		return val;
	}

	/**
	 * @param dataFormatParten the dataFormatParten to set
	 */
	public void setDataFormatParten(String dataFormatParten) {
		this.dataFormatParten = dataFormatParten;
	}

}
