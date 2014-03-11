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
 * 2012-1-31       郭训长            创建<br/>
 */
public class ValueEscaper {
	private static final String[] escape = new String[]{
		",",
		"and ",
		"exec ",
		"insert ",
		"select ",
		"delete ",
		"update ",
		"union ",
		"count",
		"*",
		"%",
		"chr",
		"mid",
		"master",
		"truncate",
		"char",
		"declare",
		";",
		"or ",
		"+",
		"--",
		"'"
	};
	
	/**
	 * 过滤直接用于拼接sql输入的参数，防止SQL注入攻击,<br/>
	 * 不建议用此函数，而是使用带参数(?)的sql,使用 PreparedStatement
	 * @param parameter
	 * @return
	 */
	public static String escapeSqlParameter(String parameter){
		for(int i=0; i < escape.length; i ++) {
			parameter = parameter.replace(escape[i], ""); 
		}
		return parameter; 
	}
}
