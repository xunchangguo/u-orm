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
package org.uorm.dao.dialect;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-29       郭训常            创建<br/>
 */
public class Ingres9Dialect extends IngresDialect {

	/* (non-Javadoc)
	 * @see org.uorm.dao.dialect.IngresDialect#supportsOffset()
	 */
	@Override
	public boolean supportsOffset() {
		return true;
	}

	@Override
	public String getLimitString(String querySelect, int offset, int limit) {
		StringBuffer soff = new StringBuffer(" offset " + offset);
		StringBuffer slim = new StringBuffer(" fetch first " + limit + " rows only");
		StringBuffer sb = new StringBuffer(querySelect.length() +
				soff.length() + slim.length()).append(querySelect);
		if (offset > 0) {
			sb.append(soff);
		}
		if (limit > 0) {
			sb.append(slim);
		}
		return sb.toString();
	}
}
