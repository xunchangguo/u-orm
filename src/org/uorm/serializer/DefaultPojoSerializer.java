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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.mapping.ObjectMappingCache;
import org.uorm.serializer.converter.StringConverter;
import org.uorm.serializer.formter.DefaultValueFormater;
import org.uorm.serializer.formter.IValueFormater;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-6-25       郭训长            创建<br/>
 */
public class DefaultPojoSerializer implements IPojoSerializer {
	private IValueFormater formater = null;
	private ITypeConverter converter = null;

	public DefaultPojoSerializer() {
		super();
		this.formater = new DefaultValueFormater();
		this.converter = new StringConverter();
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.ISerializer#serialize(java.lang.Object, java.lang.String)
	 */
	@Override
	public Document serialize(Object pojo, String rootName) throws Exception {
		Class<?> cls = pojo.getClass();
		Map<String, Method> getMethodMap = ObjectMappingCache.getInstance().getPojoGetMethod(cls);
		Document doc = DocumentHelper.createDocument();
		if(rootName == null || rootName.trim().length() == 0) {
			rootName = cls.getSimpleName().toUpperCase();
		}
		Element root = doc.addElement(rootName);
		for( String colName : getMethodMap.keySet() ) {
			Method getMethod = getMethodMap.get(colName);
			Object val = getMethod.invoke(pojo);
			Element ele = root.addElement(colName);
			if(val != null) {
				if(formater == null) {
					ele.setText(val.toString());
				}else{
					ele.setText(formater.format(val));
				}
			}
		}
		return doc;
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.ISerializer#serialize2(java.lang.Object, java.lang.String)
	 */
	@Override
	public String serialize2(Object pojo, String rootName) throws Exception {
		Class<?> cls = pojo.getClass();
		Map<String, Method> getMethodMap = ObjectMappingCache.getInstance().getPojoGetMethod(cls);
		StringBuilder sb = new StringBuilder();
		if(rootName == null || rootName.trim().length() == 0) {
			rootName = cls.getSimpleName().toUpperCase();
		}
		sb.append('<').append(rootName).append('>');
		for( String colName : getMethodMap.keySet() ) {
			Method getMethod = getMethodMap.get(colName);
			Object val = getMethod.invoke(pojo);
			if(val != null) {
				sb.append('<').append(colName).append('>');
				if(formater == null) {
					sb.append(escape(val.toString()));
				}else{
					sb.append(escape(formater.format(val)));
				}
				sb.append("</").append(colName).append('>');
			}else{
				sb.append('<').append(colName).append("/>");
			}
		}
		sb.append("</").append(rootName).append('>');
		
		return sb.toString();
	}
	
	/**
	 * escape xml spacial character
	 * @param str
	 * @return
	 */
	public static String escape(String str){
		if(str != null){
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll("'", "&apos;");
			str = str.replaceAll("\"", "&quot;");
		}
		return str;
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.ISerializer#deserialize(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T deserialize(Class<T> cls, String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		return deserialize(cls, doc);
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.ISerializer#deserialize(java.lang.Class, org.dom4j.Document)
	 */
	@Override
	public <T> T deserialize(Class<T> cls, Document xml) throws Exception {
		return deserialize(cls, xml.getRootElement());
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IPojoSerializer#deserialize(java.lang.Class, org.dom4j.Element)
	 */
	@Override
	public <T> T deserialize(Class<T> cls, Element element) throws Exception {
		@SuppressWarnings("unchecked")
		List<Node> nodes = element.elements();
		Map<String, Method> setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(cls);
		T instance = cls.newInstance();
		if(setMethods != null && !setMethods.isEmpty()) {
			for (Node node : nodes) {
				String strVal = node.getText();
				if(strVal != null) {
					Method setterMethod = setMethods.get(node.getName().toUpperCase());
					if(setterMethod != null) {
						Class<?> memberType = setterMethod.getParameterTypes()[0];
						if(memberType == String.class) {
							setterMethod.invoke(instance, strVal);
						}else{
							if(strVal.length() > 0) {
								Object val = converter.convert(strVal, memberType);
								setterMethod.invoke(instance, val);
							}
						}
					}
				}
			}
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IPojoSerializer#deserialize2(java.lang.Class, java.lang.String)
	 */
	@Override
	public Map<String, Object> deserialize2(Class<?> cls, String xml)
			throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		return deserialize2(cls, doc);
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IPojoSerializer#deserialize2(java.lang.Class, org.dom4j.Document)
	 */
	@Override
	public Map<String, Object> deserialize2(Class<?> cls, Document xml)
			throws Exception {
		return deserialize2(cls, xml.getRootElement());
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IPojoSerializer#deserialize2(java.lang.Class, org.dom4j.Element)
	 */
	@Override
	public Map<String, Object> deserialize2(Class<?> refcls, Element element)
			throws Exception {
		@SuppressWarnings("unchecked")
		List<Node> nodes = element.elements();
		Map<String, PropertyDescriptor> propMap = ObjectMappingCache.getInstance().getObjectPropertyMap(refcls);
		Map<String, Object> instance = new HashMap<String, Object>();
		if(propMap != null && !propMap.isEmpty()) {
			for (Node node : nodes) {
				String strVal = node.getText();
				if(strVal != null) {
					String name = node.getName().toUpperCase();
					PropertyDescriptor descriptor = propMap.get(name);
					if(descriptor != null) {
						Class<?> memberType = descriptor.getPropertyType();
						if(memberType == String.class) {
							instance.put(name, strVal);
						}else{
							if(strVal.length() > 0) {
								Object val = converter.convert(strVal, memberType);
								instance.put(name, val);
							}
						}
					}
				}
			}
		}
		return instance;
	}

	/**
	 * @return the formater
	 */
	public IValueFormater getFormater() {
		return formater;
	}

	/**
	 * @param formater the formater to set
	 */
	public void setFormater(IValueFormater formater) {
		this.formater = formater;
	}

	/**
	 * @return the converter
	 */
	public ITypeConverter getConverter() {
		return converter;
	}

	/**
	 * @param converter the converter to set
	 */
	public void setConverter(ITypeConverter converter) {
		this.converter = converter;
	}

}
