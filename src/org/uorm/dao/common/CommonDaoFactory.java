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


/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-3-2       郭训长            创建<br/>
 */
public class CommonDaoFactory {
	
	/**
	 * 生成通用的ICommonDao
	 * @param config 配置信息
	 * @return
	 */
	public static ICommonDao createCommonDao(DatasourceConfig config){
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory );
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 增加xml返回结果类型
	 * @param config 配置信息
	 * @return
	 */
	public static ICommonDaoXmlExt createCommonDaoXmlExt(DatasourceConfig config){
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory );
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 同一时间同一个线程不同库可允许多个事务
	 * @param config
	 * @return
	 */
	public static ICommonDao createOneThreadMultiCommonDao(DatasourceConfig config){
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(connectionFactory );
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao
	 * @param config 配置信息
	 * @param autoManagerTransaction 是否自动管理事务
	 * @return
	 */
	public static ICommonDao createCommonDao(DatasourceConfig config, boolean autoManagerTransaction){
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory );
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 增加xml返回结果类型
	 * @param config 配置信息
	 * @param autoManagerTransaction 是否自动管理事务
	 * @return
	 */
	public static ICommonDaoXmlExt createCommonDaoXmlExt(DatasourceConfig config, boolean autoManagerTransaction){
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory );
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 同一时间同一个线程不同库可允许多个事务
	 * @param config  配置信息
	 * @param autoManagerTransaction 是否自动管理事务
	 * @return
	 */
	public static ICommonDao createOneThreadMultiCommonDao(DatasourceConfig config, boolean autoManagerTransaction){
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(connectionFactory );
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao
	 * @param connectionFactory
	 * @return
	 */
	public static ICommonDao createCommonDao(ConnectionFactory connectionFactory){
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory );
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 增加xml返回结果类型
	 * @param connectionFactory
	 * @return
	 */
	public static ICommonDaoXmlExt createCommonXmlExtDao(ConnectionFactory connectionFactory){
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory );
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 同一时间同一个线程不同库可允许多个事务
	 * @param connectionFactory
	 * @return
	 */
	public static ICommonDao createOneThreadMultiCommonDao(ConnectionFactory connectionFactory){
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(connectionFactory );
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao
	 * @param connectionFactory
	 * @param autoManagerTransaction 是否自动管理事务
	 * @return
	 */
	public static ICommonDao createCommonDao(ConnectionFactory connectionFactory, boolean autoManagerTransaction){
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory );
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 增加xml返回结果类型
	 * @param connectionFactory
	 * @param autoManagerTransaction 是否自动管理事务
	 * @return
	 */
	public static ICommonDaoXmlExt createCommonDaoXmlExt(ConnectionFactory connectionFactory, boolean autoManagerTransaction){
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory );
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}
	
	/**
	 * 生成通用的ICommonDao, 同一时间同一个线程不同库可允许多个事务
	 * @param connectionFactory
	 * @param autoManagerTransaction 是否自动管理事务
	 * @return
	 */
	public static ICommonDao createOneThreadMultiCommonDao(ConnectionFactory connectionFactory, boolean autoManagerTransaction){
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(connectionFactory );
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}

}
