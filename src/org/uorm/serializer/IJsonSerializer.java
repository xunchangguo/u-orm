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
package org.uorm.serializer;

import java.util.Map;

/**
 * json
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-7-1       郭训长            创建<br/>
 */
public interface IJsonSerializer {

	/**
	 * 序列化
	 * @param pojo
	 * @return json串
	 * @throws Exception
	 */
	public String serialize(Object src) throws Exception;
	
	/**
	 * 反序列化
	 * @param cls
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public <T> T deserialize(Class<T> cls, String json) throws Exception;
	
	/**
	 * 反序列化Map, 为了方便使用 @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelData(java.lang.Class, java.util.Map<java.lang.String,java.lang.Object>[])
	 * @param cls 参考对应的类，这个类里有字段对应的类型及相关信息（注解）
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> deserialize2(Class<?> cls, String json) throws Exception;
}
