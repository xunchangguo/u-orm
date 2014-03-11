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
package org.uorm.dao.common.pool;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.uorm.dao.common.DatasourceConfig;
import org.uorm.utils.Utils;

/**
 * Druid连接池
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-5-24       郭训长            创建<br/>
 */
public class DruidDataSourceCreator implements IDataSourceCreator {
	
//	<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close"> 
//    <!-- 基本属性 url、user、password -->
//    <property name="url" value="${jdbc_url}" />
//    <property name="username" value="${jdbc_user}" />
//    <property name="password" value="${jdbc_password}" />
//
//    <!-- 配置初始化大小、最小、最大 -->
//    <property name="initialSize" value="1" />
//    <property name="minIdle" value="1" /> 
//    <property name="maxActive" value="20" />
//
//    <!-- 配置获取连接等待超时的时间 -->
//    <property name="maxWait" value="60000" />
//
//    <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
//    <property name="timeBetweenEvictionRunsMillis" value="60000" />
//
//    <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
//    <property name="minEvictableIdleTimeMillis" value="300000" />
//
//    <property name="validationQuery" value="SELECT 'x'" />
//    <property name="testWhileIdle" value="true" />
//    <property name="testOnBorrow" value="false" />
//    <property name="testOnReturn" value="false" />
//
//    <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
//    <property name="poolPreparedStatements" value="true" />
//    <property name="maxPoolPreparedStatementPerConnectionSize" value="20" />
//
//    <!-- 配置监控统计拦截的filters -->
//    <property name="filters" value="stat" /> 
//</bean>
//通常来说，只需要修改initialSize、minIdle、maxActive。
//
//如果用Oracle，则把poolPreparedStatements配置为true，mysql可以配置为false。

	/* (non-Javadoc)
	 * @see org.uorm.dao.common.pool.IDataSourceCreator#createDatasource(org.uorm.dao.common.DatasourceConfig)
	 */
	@Override
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
		dataSource.setDriverClassName(datasourcecfg.getDriverClass());
		dataSource.setUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map<String, String> props = datasourcecfg.getPoolPerperties();
		int initialSize = Utils.strToInt(props.get("initialSize"));
		if(initialSize >0){
			dataSource.setInitialSize(initialSize);
		}else{
			dataSource.setInitialSize(1);
		}
		int minIdle = Utils.strToInt(props.get("minIdle"));
		if(minIdle >0){
			dataSource.setMinIdle(minIdle);
		}else{
			dataSource.setMinIdle(1);
		}
		int maxActive = Utils.strToInt(props.get("maxActive"));
		if(maxActive >0){
			dataSource.setMaxActive(maxActive);
		}else{
			dataSource.setMaxActive(20);
		}
//		<!-- 配置获取连接等待超时的时间 -->
		int maxWait = Utils.strToInt(props.get("maxWait"));
		if(maxWait >0){
			dataSource.setMaxWait(maxWait);
		}else{
			dataSource.setMaxWait(60000);
		}
//		<!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
//	      <property name="timeBetweenEvictionRunsMillis" value="60000" />
		long timeBetweenEvictionRunsMillis = Utils.str2long(props.get("timeBetweenEvictionRunsMillis"));
		if(timeBetweenEvictionRunsMillis > 0) {
			dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		}else{
			dataSource.setTimeBetweenEvictionRunsMillis(60000);
		}
//		<!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
//	      <property name="minEvictableIdleTimeMillis" value="300000" />
		long minEvictableIdleTimeMillis = Utils.str2long(props.get("minEvictableIdleTimeMillis"));
		if(minEvictableIdleTimeMillis > 0){
			dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}
		String validationQuery = props.get("validationQuery");
		if(validationQuery != null && validationQuery.trim().length() > 0){
			dataSource.setValidationQuery(validationQuery);
		}else{
			dataSource.setValidationQuery("SELECT 'x'");
		}
		Boolean testWhileIdle = Utils.str2Boolean(props.get("testWhileIdle"));
		if(testWhileIdle != null){
			dataSource.setTestWhileIdle(testWhileIdle);
		}
		Boolean testOnBorrow = Utils.str2Boolean(props.get("testOnBorrow"));
		if(testOnBorrow != null){
			dataSource.setTestOnBorrow(testOnBorrow);
		}
		Boolean testOnReturn = Utils.str2Boolean(props.get("testOnReturn"));
		if(testOnReturn != null){
			dataSource.setTestOnReturn(testOnReturn);
		}
//		 <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
//	      <property name="poolPreparedStatements" value="true" />
		Boolean poolPreparedStatements = Utils.str2Boolean(props.get("poolPreparedStatements"));
		if(poolPreparedStatements != null){
			dataSource.setPoolPreparedStatements(poolPreparedStatements);
		}
		int maxPoolPreparedStatementPerConnectionSize = Utils.strToInt(props.get("maxPoolPreparedStatementPerConnectionSize"));
		if(maxPoolPreparedStatementPerConnectionSize > 0){
			dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		}
//		<!-- 配置监控统计拦截的filters -->
//	      <property name="filters" value="stat" /> 
		String filters = props.get("filters");
		if(filters != null && filters.trim().length() > 0){
			dataSource.setFilters(filters);
		}
		return dataSource;
	}

}
