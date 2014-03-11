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

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * interface of pojo serializer
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-6-25       郭训长            创建<br/>
 * @param <T>
 */
public interface IPojoSerializer {
	
	/**
	 * 序列化
	 * @param pojo
	 * @param rootName if null use className.upper
	 * @return xml,所有节点都大写
	 */
	public Document serialize(Object pojo, String rootName) throws Exception;
	
	/**
	 * 序列化
	 * @param pojo
	 * @param rootName if null use className.upper
	 * @return String xml, 所有节点都大写
	 */
	public String serialize2(Object pojo, String rootName) throws Exception;
	
	/**
	 * 反序列化成对象
	 * @param cls
	 * @param xml String xml, 所有节点都大写
	 * @return
	 */
	public <T> T deserialize(Class<T> cls, String xml) throws Exception;
	
	/**
	 * 反序列化成对象
	 * @param cls
	 * @param xml xml, 所有节点都大写
	 * @return
	 */
	public <T> T deserialize(Class<T> cls, Document xml) throws Exception;
	
	/**
	 * 反序列化成对象
	 * @param cls
	 * @param element xml, 所有节点都大写
	 * @return
	 */
	public <T> T deserialize(Class<T> cls, Element element) throws Exception;
	
	/**
	 * 反序列化成Map, 为了方便使用 @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelData(java.lang.Class, java.util.Map<java.lang.String,java.lang.Object>[])
	 * @param refcls 参考对应的类，这个类里有字段对应的类型及相关信息（注解）
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> deserialize2(Class<?> refcls, String xml) throws Exception;
	
	/**
	 * 反序列化成Map, 为了方便使用 @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelData(java.lang.Class, java.util.Map<java.lang.String,java.lang.Object>[])
	 * @param refcls 参考对应的类，这个类里有字段对应的类型及相关信息（注解）
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> deserialize2(Class<?> refcls, Document xml) throws Exception;
	
	/**
	 * 反序列化成Map, 为了方便使用 @see org.uorm.dao.common.ICommonDaoXmlExt#saveModelData(java.lang.Class, java.util.Map<java.lang.String,java.lang.Object>[])
	 * @param refcls 参考对应的类，这个类里有字段对应的类型及相关信息（注解）
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> deserialize2(Class<?> refcls, Element element) throws Exception;

}
