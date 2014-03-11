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
package org.uorm.dao.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.uorm.orm.mapping.IXmlReader;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-1-31       郭训长            创建<br/>
 */
public class RowsResultSetXmlExtractor implements ResultSetExtractor<Document> {

	private int offset = 0;
	private int max = Integer.MAX_VALUE;
	
	private String rootName = null;
	private String itemName = null;
	
	private IXmlReader xmlReader = null;
	
	public RowsResultSetXmlExtractor(IXmlReader xmlReader, String rootName, String itemName) {
		super();
		this.xmlReader = xmlReader;
		this.rootName = rootName;
		this.itemName = itemName;
	}


	/* (non-Javadoc)
	 * @see org.uorm.dao.common.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
	@Override
	public Document extractData(ResultSet rs) throws SQLException {
		Document results = DocumentHelper.createDocument();
		Element rootElement = results.addElement(rootName);
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		int len = 0;
		while (rs.next()) {
			if(pos >= offset){
				try {
					rootElement.add(this.xmlReader.read(itemName, rs, rsmd));
					len ++;
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if(len >= max){
				break;
			}
			pos ++;
		}
		return results;
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


	/**
	 * @param rootName the rootName to set
	 */
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}


	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	/**
	 * @return the rootName
	 */
	public String getRootName() {
		return rootName;
	}


	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
}
