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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;



/**
 * 通用数据库操作接口
 * 
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public interface ICommonDao {

	/**
	 * 开始一个事务
	 * @throws SQLException 
	 */
	void beginTransation() throws SQLException;

	/**
	 * 事务提交
	 * 
	 * @throws Exception
	 */
	void commitTransation() throws SQLException;

	/**
	 * 事务回滚
	 * @throws SQLException 
	 */
	void rollbackTransation() throws SQLException;
	
	/**
	 * 获取是否自动管理事务
	 * @return
	 */
	boolean isAutoManagerTransaction();
	
	/**
	 * 设置是否自动管理事务
	 * @param autoManagerTransaction
	 */
	void setAutoManagerTransaction(boolean autoManagerTransaction);
	
	/**
	 * 根据主键查询
	 * @param <T>
	 * @param cls
	 * @param pkvals 多个主键，按照 cls 中定义的keyOrder顺序给值
	 * @return
	 */
	public <T> T queryBusinessObjByPk(Class<T> cls, Serializable... pkvals) throws SQLException;
	
	/**
	 * sql 查询 （可带参数，例： select * from tab a where a.fielda = ?）
	 * @param <T>
	 * @param cls
	 * @param query 带参数的查询sql语句
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> queryBusinessObjs(Class<T> cls, String query, SqlParameter... params) throws SQLException;
	
	/**
	 * 查询表里所有数据
	 * @param <T>
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> queryAllBusinessObjs(Class<T> cls) throws SQLException;
	
	/**
	 * sql 查询
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return List Map  key colname(upper case)
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryForListMap(String sql, SqlParameter... params) throws SQLException;
	
	/**
	 * sql 查询
	 * @param sql 
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws SQLException
	 */
	public List<Object[]> queryForListArray(String sql, SqlParameter... params) throws SQLException;
	
	/**
	 * sql 查询
	 * @param <T>
	 * @param sql
	 * @param rse
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public <T> T query(final String sql, final ResultSetExtractor<T> rse, final SqlParameter... params) throws SQLException; 
	
	/**
	 * sql查询单个对象
	 * @param <T>
	 * @param cls
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public <T> T querySingleObject(Class<T> cls, String sql, SqlParameter... params) throws SQLException;
	
	/**
	 * sql查询,返回单条记录
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return Map key colname(upper case)
	 * @throws Exception
	 */
	public Map<String, Object> queryForMap(String sql, SqlParameter... params) throws SQLException;
	
	/**
	 * sql查询,返回单条记录
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return Object[]
	 * @throws SQLException
	 */
	public Object[] queryForArray(String sql, SqlParameter... params) throws SQLException;
	
	/**
	 * sql查询
	 * @param <T>
	 * @param cls
	 * @param sql
	 * @param startRecord 开始记录 start with 0
	 * @param maxRecord  取maxRecord条，取的条数 <= maxRecord
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> queryBusinessObjs(Class<T> cls, final String sql, final int startRecord, final int maxRecord, final SqlParameter... params) throws SQLException;
	
	/**
	 * 进行分页查询
	 * @param cls
	 * @param sql
	 * @param startPage  开始页数，页码从0开始
	 * @param pageSize  每页大小
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls, final String sql, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	
	/**
	 * 进行分页查询
	 * @param cls
	 * @param countsql 查询总记录sql，这个sql的条件参数必须和@param sql（查询记录sql）的条件参数一致，包括顺序
	 * @param sql 查询记录sql，这个sql的条件参数必须和@param countsql（查询总记录sql）的条件参数一致，包括顺序
	 * @param startPage
	 * @param pageSize
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls, final String countsql, final String sql, final int startPage, final int pageSize, final SqlParameter... params) throws SQLException;
	/**
	 * 删除表里数据
	 * @param cls 要删除的类，为找对应的表用
	 * @param pkvals 多个主键，按照 cls 中定义的keyOrder顺序给值
	 * @return 
	 * @throws Exception
	 */
	public int deleteBusiness(Class<?> cls, Serializable... pkvals) throws SQLException;
	
	/**
	 * 删除表里数据
	 * @param pojos 要删除的数据
	 * @return 
	 * @throws Exception
	 */
	public int deleteBusiness(Object... pojos) throws SQLException;
	
	/**
	 * 删除表里数据
	 * @param pojos 要删除的数据
	 * @return
	 * @throws Exception
	 */
	public int deleteBusinessCol(Collection<? extends Serializable> pojos) throws SQLException;
	
	/**
	 * 保存数据到数据库
	 * @param pojos
	 * @return
	 * @throws Exception
	 */
	public int saveBusinessObjs(Object... pojos) throws SQLException;
	
	/**
	 * 保存数据到数据库
	 * @param pojos
	 * @return
	 * @throws Exception
	 */
	public int saveBusinessObjsCol(Collection<? extends Serializable> pojos) throws SQLException;
	
	/**
	 * 更新数据到数据库
	 * @param isFilterNull 是否过滤空值
	 * @param pojos
	 * @return 影响记录条数
	 * @throws Exception
	 */
	public int updateBusinessObjs(boolean isFilterNull, Object... pojos) throws SQLException;
	
	/**
	 * 更新数据到数据库
	 * @param isFilterNull 是否过滤空值
	 * @param pojos
	 * @return 影响记录条数
	 * @throws Exception
	 */
	public int updateBusinessObjsCol(boolean isFilterNull, Collection<? extends Serializable> pojos) throws SQLException;
	/**
	 * update
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public int update(final String sql, final SqlParameter... params) throws SQLException;
	
	/**
	 * batch update
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int[] batchUpdate(final String[] sql) throws SQLException;
	
	/**
	 * execute sql
	 * @param sql
	 * @param params 参数，顺序很重要，和@param query中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
	 * @return
	 * @throws Exception
	 */
	public boolean execute(final String sql, final SqlParameter... params) throws SQLException;
	
	/**
	 * do with ConnectionCallback
	 * @param <T>
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public <T> T execute(ConnectionCallback<T> action) throws SQLException;
	
	/**
	 * do with StatementCallback
	 * @param <T>
	 * @param action
	 * @return
	 * @throws SQLException
	 */
	public <T> T execute(StatementCallback<T> action) throws SQLException;
}
