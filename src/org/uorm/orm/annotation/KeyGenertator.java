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
package org.uorm.orm.annotation;

/**
 * 主键生成器
 * 插入多条数据时，INCREMENT 比 SELECT 性能高很多，建议用 INCREMENT，
 * 如果能自己生成主键的话，建议直接生成用 ASSIGNED 方式
 * 
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public interface KeyGenertator {
	/**ASSIGNED*/
	public static final String ASSIGNED = "assigend";
	public static final String IDENTITY = "identity";
	public static final String UUID = "uuid";//org.uorm.dao.id.UUIDGenerator
	public static final String UUIDHEX = "uuid.hex";//org.uorm.dao.id.UUIDHexGenerator
	public static final String GUID = "guid";//org.uorm.dao.id.GUIDGenerator
	public static final String INCREMENT = "increment";//org.uorm.dao.id.IncrementGenerator
	/** Sequence Name is SEQ_TABLENAME ,also can be set by method setSequenceName(String)*/
	public static final String SEQUENCE = "sequence";//org.uorm.dao.id.SequenceGenerator
	/** 
	 * select from table, table name default is IDENTIFIER_TABLE
	 * also can be set by method setTableName(String);
	 * has 2 columns(default is TABLE_NAME(String), SERIALIZE_VALUE(number)))
	 * also can be set by method setNameColumn(String) and setValueColumn(String)
	 */
	public static final String SELECT = "select";//org.uorm.dao.id.SelectGenerator
	public static final String NATIVE = "native";//org.uorm.dao.id.NativeGenerator
}
