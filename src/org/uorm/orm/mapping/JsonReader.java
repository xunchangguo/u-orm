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

import org.uorm.utils.Utils;

/**
 * json reader
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-7-5       郭训长            创建<br/>
 */
public class JsonReader implements IJsonReader {
	/**
	 * From RFC 4627, "All Unicode characters may be placed within the
	 * quotation marks except for the characters that must be escaped:
	 * quotation mark, reverse solidus, and the control characters
	 * (U+0000 through U+001F)."
	 *
	 * We also escape '\u2028' and '\u2029', which JavaScript interprets as
	 * newline characters. This prevents eval() from failing with a syntax
	 * error. 
	 */
	private static final String[] REPLACEMENT_CHARS;
	private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
	static {
		REPLACEMENT_CHARS = new String[128];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
		}
		REPLACEMENT_CHARS['"'] = "\\\"";
		REPLACEMENT_CHARS['\\'] = "\\\\";
		REPLACEMENT_CHARS['\t'] = "\\t";
		REPLACEMENT_CHARS['\b'] = "\\b";
		REPLACEMENT_CHARS['\n'] = "\\n";
		REPLACEMENT_CHARS['\r'] = "\\r";
		REPLACEMENT_CHARS['\f'] = "\\f";
		HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
		HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
		HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
		HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
		HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
		HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
	}
	
	private boolean htmlSafe = true;

	private String dataFormatParten = "yyyy-MM-dd HH:mm:ss";

	/* (non-Javadoc)
	 * @see org.uorm.orm.mapping.IJsonReader#read(java.sql.ResultSet, java.sql.ResultSetMetaData)
	 */
	@Override
	public String read(ResultSet result, ResultSetMetaData rsmd)
			throws Exception {
		int count = rsmd.getColumnCount();
		if(count == 1) {
			//only one
			Object val = result.getObject(1);
			if(val == null){
				return "null";
			}else{
				int columnType = rsmd.getColumnType(1);
				String strval = getValue(result, 1, columnType);
				if(isNumberOrBoolean(columnType)){
					return strval == null ? "null" : strval;
				} else {
					return string(strval);
				}
			}
		}else{
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			for (int i = 1; i <= count; i++) {
				String columnName = rsmd.getColumnLabel(i);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				if(i > 1) {
					sb.append(',');
				}
				sb.append('\"').append(columnName).append("\":");
				int columnType = rsmd.getColumnType(i);
				String strval = getValue(result, i, columnType);
				if(isNumberOrBoolean(columnType)){
					if(strval == null) {
						sb.append("null");
					}else{
						sb.append(strval);
					}
				} else {
					sb.append( string(strval) );
				}
			}
			sb.append('}');
			return sb.toString();
		}
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
	
	private boolean isNumberOrBoolean(int sqlType) {
		boolean number = false;
		switch (sqlType) {
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.BIT://Boolean.class
		case Types.BOOLEAN:
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.REAL:
			number = true;
			break;
		default:
			break;
		}
		return number;
	}
	
	private String string(String value) {
		if(value == null) {
			return "null";
		}
		StringBuilder out = new StringBuilder();
		String[] replacements = htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
		out.append('\"');
		int length = value.length();
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			String replacement = null;
			if (c < 128) {
				replacement = replacements[c];
			} else if (c == '\u2028') {
				replacement = "\\u2028";
			} else if (c == '\u2029') {
				replacement = "\\u2029";
			}
			if (replacement == null) {
				out.append(c);
			} else {
				out.append(replacement);
			}
		}
		out.append('\"');
		return out.toString();
	}

	/**
	 * @param dataFormatParten the dataFormatParten to set
	 */
	public void setDataFormatParten(String dataFormatParten) {
		this.dataFormatParten = dataFormatParten;
	}


	/**
	 * @param htmlSafe the htmlSafe to set
	 */
	public void setHtmlSafe(boolean htmlSafe) {
		this.htmlSafe = htmlSafe;
	}
	
}
