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
package test;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uorm.dao.common.CommonDaoImpl;
import org.uorm.dao.common.ConnectionFactory;
import org.uorm.dao.common.DatasourceConfig;
import org.uorm.dao.common.DefaultConnectionFactory;
import org.uorm.dao.common.ICommonDao;
import org.uorm.orm.bytecode.javassist.JavassistObjectReader;
import org.uorm.orm.mapping.IObjectReader;
import org.uorm.orm.mapping.ObjectReader;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-9-12       郭训长            创建<br/>
 */
public class JavassistTest {
	
	public static void a(IObjectReader reader, User uus) {
		try {
			reader.readValue2Map(uus);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void query(ICommonDao dao) {
		List<User> us;
		try {
			us = dao.queryBusinessObjs(User.class, "select * from UUM_USER", 0, 20);
//			System.out.println(us.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		//for test read to map
//		int i = 10;
//		User uus = new User();//new User(i);
//		uus.setLoginName("o-loginname-"+Utils.genRandomNum(6));
//		uus.setUserName("o-着急啊看到username-"+ Utils.genRandomNum(6));
//		uus.setPassword("o-着急啊看到password-"+i);
//		uus.setDescription("o-着急啊看到desc-"+i);
//		uus.setState(Double.valueOf(i));
//		uus.setCreateTime(new Timestamp(System.currentTimeMillis()));
//		uus.setUserCard("usercard-"+i);
//		uus.setUserType(i);
//		uus.setEmail("email-"+i);
//		uus.setSex(i);
//		uus.setAddress("address-"+i);
//		uus.setBirthday(new java.sql.Date(System.currentTimeMillis()));
//		JavassistObjectReader reader = new JavassistObjectReader();
//
//		int count = 30;
//		long time = System.currentTimeMillis();
//		for (int j = 0; j < count; j++) {
//			a(reader, uus);
//		}
//		time = System.currentTimeMillis() - time;
//		System.out.println("JavassistObjectReader" + time + " ms, " + new DecimalFormat().format(count * 1000 / time) + " t/s");
//		System.out.println("-------------");
//		time = System.currentTimeMillis();
//		for (int j = 0; j < count; j++) {
//			a(new ObjectReader(), uus);
//		}
//		time = System.currentTimeMillis() - time;
//		System.out.println("ObjectReader" + time + " ms, " + new DecimalFormat().format(count * 1000 / time) + " t/s");
		
		//
		String driver = "com.mysql.jdbc.Driver";
		/**连接字串 */
		String url = "jdbc:mysql://127.0.0.1/simdb";
		/** 数据库用户名 */
		String username = "root";
		/** 数据库密码 */
		String password = "root";
		DatasourceConfig config = new DatasourceConfig();
//		config.setDatabasetype(DataBaseType.MYSQL);
		config.setDriverClass(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		Map<String, String> poolPerperties = new HashMap<String, String>();
		//DBCP
		poolPerperties.put("___POOL_TYPE_", "DBCP");
		poolPerperties.put("initialSize", "2");
		poolPerperties.put("maxActive", "10");
		poolPerperties.put("maxIdle", "5");
		poolPerperties.put("minIdle", "2");
		poolPerperties.put("maxWait", "60000");
		poolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
		config.setPoolPerperties(poolPerperties);
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		CommonDaoImpl dao = new CommonDaoImpl(connectionFactory );
		dao.setObjectReader(new ObjectReader());
		CommonDaoImpl javassistDao = new CommonDaoImpl(connectionFactory );
		javassistDao.setObjectReader(new JavassistObjectReader());
		
		System.out.println(dao.queryBusinessObjByPk(User.class, 388));
		System.out.println(javassistDao.queryBusinessObjByPk(User.class, 388));

		for(int i = 0; i < 3; i++) {
			int count = 500;
			long time = System.currentTimeMillis();
			for (int j = 0; j < count; j++) {
				query(dao);
			}
			time = System.currentTimeMillis() - time;
			System.out.println("ObjectReader: " + time + " ms, " + new DecimalFormat().format(count * 1000 / time) + " t/s");

			System.out.println("-------------");
			time = System.currentTimeMillis();
			for (int j = 0; j < count; j++) {
				query(javassistDao);
			}
			time = System.currentTimeMillis() - time;
			System.out.println("JavassistObjectReader: " + time + " ms, " + new DecimalFormat().format(count * 1000 / time) + " t/s");
			System.out.println("===================");
		}
	}

}
