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

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 可能的数据库表名，字段名 Guesser
 * 
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public interface IDbNameGuesser {

	/**
	 * 返回可能的数据库表的字段名列表
	 * @param member 类的方法 for guess the column names for
	 * @return
	 */
	 public Collection<String> getPossibleColumnNames(Method member);
	 
	 /**
	  * 返回可能名字（表名或属性名）
	  * @param fieldname 类的属性名或类名  for guess the column names for
	  * @return
	  */
	 public Collection<String> getPossibleNames(String fieldname);
	 
	 /**
	  * 返回可能的数据库表名列表
	  * @param object 类 for guess the table names for
	  * @return
	  */
	 public Collection<String> getPossibleTableNames(Class<?> object);
}
