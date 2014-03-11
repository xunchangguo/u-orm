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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.dom4j.Document;

/**
 * 增加查询返回xml方法
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-1-31       郭训长            创建<br/>
 */
public interface ICommonDaoXmlExt extends ICommonDao {

	/**
	 * 查询返回xml
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public Document fill(final String sql, final SqlParameter... params) throws SQLException; 
	
	/**
	 * 查询返回xml
	 * @param sql
	 * @param rootName
	 * @param itemName
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public Document fill(final String sql, final String rootName, final String itemName, final SqlParameter... params) throws SQLException; 
	
	/**
	 * 查询返回xml
	 * @param sql
	 * @param startRecord  开始记录 start with 0
	 * @param maxRecord  取maxRecord条，取的条数 <= maxRecord
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public Document fill(final String sql, final int startRecord, final int maxRecord, final SqlParameter... params) throws SQLException;
	
	/**
	 * 查询返回xml
	 * @param sql
	 * @param rootName
	 * @param itemName
	 * @param startRecord 开始记录 start with 0
	 * @param maxRecord 取maxRecord条，取的条数 <= maxRecord
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public Document fill(final String sql, final String rootName, final String itemName, final int startRecord, final int maxRecord, final SqlParameter... params) throws SQLException;
	
	/**
	 * 分页查询返回xml
	 * @param sql
	 * @param startPage 开始页数，页码从0开始
	 * @param pageSize 每页大小
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public Document fillByPagedQuery(final String sql, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	
	/**
	 * 分页查询返回xml
	 * @param countsql 查询总记录sql，这个sql的条件参数必须和@param sql（查询记录sql）的条件参数一致，包括顺序
	 * @param sql 查询记录sql，这个sql的条件参数必须和@param countsql（查询总记录sql）的条件参数一致，包括顺序
	 * @param startPage
	 * @param pageSize
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Document fillByPagedQuery(final String countsql, final String sql, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	
	/**
	 * 分页查询返回xml
	 * @param sql
	 * @param rootName
	 * @param itemName
	 * @param startPage 开始页数，页码从0开始
	 * @param pageSize 每页大小
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public Document fillByPagedQuery(final String sql, final String rootName, final String itemName, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	
	/**
	 * 分页查询返回xml
	 * @param countsql 查询总记录sql，这个sql的条件参数必须和@param sql（查询记录sql）的条件参数一致，包括顺序
	 * @param sql 查询记录sql，这个sql的条件参数必须和@param countsql（查询总记录sql）的条件参数一致，包括顺序
	 * @param rootName
	 * @param itemName
	 * @param startPage
	 * @param pageSize
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Document fillByPagedQuery(final String countsql, final String sql, final String rootName, final String itemName, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	
	/**
	 * 保存数据到数据库
	 * @param cls 目标表对应的class，为了获取表名，主键生成方式等等
	 * @param models 模型数据 key: uppercase
	 * @return
	 */
	public int saveModelData(Class<?> cls, Map<String, Object>[] models) throws SQLException;
	
	/**
	 * 保存数据到数据库
	 * @param cls 目标表对应的class，为了获取表名，主键生成方式等等
	 * @param model 模型数据 key: uppercase
	 * @return
	 */
	public int saveModelData(Class<?> cls, Map<String, Object> model) throws SQLException;
	
	/**
	 * 保存数据到数据库
	 * @param cls 目标表对应的class，为了获取表名，主键生成方式等等
	 * @param models 模型数据 key: uppercase
	 * @return
	 */
	public int saveModelDataCol(Class<?> cls, Collection<Map<String, Object>> models) throws SQLException;
	
	//------------- json --------
	/**
	 * 查询返回json串
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public String fillJson(final String sql, final SqlParameter... params) throws SQLException; 
	
	/**
	 * 查询返回json串
	 * @param sql
	 * @param startRecord  开始记录 start with 0
	 * @param maxRecord  取maxRecord条，取的条数 <= maxRecord
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public String fillJson(final String sql, final int startRecord, final int maxRecord, final SqlParameter... params) throws SQLException;
	
	/**
	 * 分页查询返回json串
	 * @param sql
	 * @param startPage 开始页数，页码从0开始
	 * @param pageSize 每页大小
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public String fillJsonByPagedQuery(final String sql, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	
	/** 
	 * 分页查询返回json串
	 * @param countsql 查询总记录sql，这个sql的条件参数必须和@param sql（查询记录sql）的条件参数一致，包括顺序
	 * @param sql 查询记录sql，这个sql的条件参数必须和@param countsql（查询总记录sql）的条件参数一致，包括顺序
	 * @param startPage
	 * @param pageSize
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public String fillJsonByPagedQuery(final String countsql, final String sql, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
}
