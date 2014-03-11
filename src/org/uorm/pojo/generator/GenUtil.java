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
package org.uorm.pojo.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * POJO代码生成工具类
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class GenUtil {
	public static final String LINE_END = System.getProperty("line.separator");

	public static final  int PASCAL_CASE = 0;
	public static final  int CAMEL_CASE = 1;
	
	private GenUtil() {
		super();
	}


	public static String capitalize(String value, int cap) {
		String ret = "";
		String tmpValue = value;
		if (value.toUpperCase().equals(value)) {
			tmpValue = value.toLowerCase();
		}
		String[] split = splitWords(tmpValue).split("_");
		boolean firstPart = true;
		for (String part : split) {
			if (part.length() > 0) {
				if ((firstPart) && (cap == CAMEL_CASE)) {
					ret = ret + part.toLowerCase();
					firstPart = false;
				} else {
					ret = ret + part.substring(0, 1).toUpperCase() + 
					part.substring(1).toLowerCase();
				}
			}
		}
		return ret;
	}

	private static String splitWords(String value) {
		Pattern pattern = Pattern.compile("([a-z0-9]+?)([A-Z]+?)");
		Matcher matcher = pattern.matcher(value);
		return matcher.replaceAll("$1_$2");
	}
}
