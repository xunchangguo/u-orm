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
package org.uorm;

import org.uorm.orm.annotation.KeyGenertator;
import org.uorm.pojo.generator.PojoGenerator;

/**
 * POJO 生成工具 Mapping对应的类生成工具
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-31       郭训长            创建<br/>
 */
public class Generator {
	//TODO edit below value
	String driver = "com.mysql.jdbc.Driver";//"com.ibm.db2.jcc.DB2Driver";//"com.microsoft.sqlserver.jdbc.SQLServerDriver";//"oracle.jdbc.driver.OracleDriver";//"org.sqlite.JDBC";//
	/**连接字串 */
	String url = "jdbc:mysql://127.0.0.1/jnmp_bj";//"jdbc:db2://198.9.1.130:50000/jummii";//"jdbc:sqlserver://198.9.1.220:1433;DatabaseName=CastMain";//"jdbc:oracle:thin:@198.9.1.122:1521:orcl";//"jdbc:sqlite:C:/Users/guoxunchang/Desktop/Avid Projects/test/SearchData/_SearchDB_";//"jdbc:oracle:thin:@192.168.1.91:1521:jdvncctv7";//"jdbc:oracle:thin:@127.0.0.1:1521:orcl";//
	/** 数据库用户名 */
	String username = "root";//"db2inst1";//"sa";//"jnmp_cz";//"GBMadmin";//"jmtc1";//"cctv7";//"cctv";//"root";
	/** 数据库密码 */
	String password = "root";//"password";//"password";//"jnmp_cz";//"GBMadmin1";//"password";//"password";//"1";//"root";
	/** 生成的POJO类包名 */
	private String packageName = "test";
    /** 生成表对应的POJO类名需要去除的表名前缀，如 “T_”, “DVN_”等 */
	private String prefix = "";//"UUM_";
	/** 生成类的目标地，默认为当前路径的 /src下，即 "./src" */
	private String destination = "./src";
	/** 是否生成json注解，方便指定系列化的字段名为数据库字段 */
	private boolean genJsonAnnotations = false;
	
	/**
	 * 生成指定表对应的 pojo类
	 * @param tableName 表名
	 * @param idgenerator 主键生成方式 {@link KeyGenertator}
	 */
	public void pojoGen (String tableName, String idgenerator) {
		PojoGenerator generator = new PojoGenerator(driver, url, username, password, packageName, destination);
		if(prefix != null){
			generator.setPrefix(prefix);
		}
		generator.setGenJsonAnnotations(genJsonAnnotations);
		generator.createDatabaseEntities(tableName, idgenerator);
	}
	
	public static void main(String[] args) {
		Generator generator = new Generator();
		generator.pojoGen("NMP_PERFDATA", KeyGenertator.NATIVE);//TODO edit this
	}
}
