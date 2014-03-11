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

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.uorm.dao.common.ValueEscaper;
import org.uorm.orm.convert.GenericConverterFactory;
import org.uorm.orm.convert.ITypeConverter;
import org.uorm.orm.mapping.ObjectMappingCache;
import org.uorm.serializer.DefaultJsonSerializer;
import org.uorm.serializer.DefaultPojoSerializer;
import org.uorm.utils.Utils;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-19       郭训常            创建<br/>
 */
public class Test {
	
	public static String removeOrders(String hql) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S|\\)]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
//		System.out.println(m.matches());
//		System.out.println(m.toMatchResult().groupCount());
//		System.out.println(m.toMatchResult().end());
		StringBuffer sb = new StringBuffer();
		for(; m.find(); m.appendReplacement(sb, ""));
		m.appendTail(sb);
		return sb.toString();
	}
	
	public static String replaceFormatSqlOrderBy(String sql) {
		sql = sql.replaceAll("(\\s)+", " ");
		int index = sql.toLowerCase().lastIndexOf("order by");
		if (index > sql.toLowerCase().lastIndexOf(")")) {
			String sql1 = sql.substring(0, index);
			String sql2 = sql.substring(index);
//			sql2 = sql2.replaceAll("[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.]+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.]+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*", "");
//			return sql1 + sql2;
			return sql1 + removeOrders(sql2);
		}
		return sql;
	}

	public static String removeOrder(Number b) {
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		Map<String, Method> getters = ObjectMappingCache.getInstance().getPojoGetMethod(User.class);
		for (String field : getters.keySet()) {
			System.out.println( field + ": " + getters.get(field) );
		}
		System.out.println("------------------------");
		Map<String, Method> setters = ObjectMappingCache.getInstance().getPojoSetMethod(User.class);
		for (String field : setters.keySet()) {
			System.out.println( field + ": " + setters.get(field) );
		}
		Class<?> cls = ObjectMappingCache.getInstance().getObjectPropertyMap(User.class).get("ID").getPropertyType();
		System.out.println(cls);
		System.out.println(Integer.class.isAssignableFrom(cls));
		System.out.println(User.class.getSimpleName());
		System.out.println("耗时：" + (System.currentTimeMillis() - start) + " ms");
		System.out.println(ValueEscaper.escapeSqlParameter("adminor' or 1=1;--"));
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(removeOrders("select * from (select * from UUM_USER order by id desc) A order by (state)"));
		System.out.println(replaceFormatSqlOrderBy("select * from (select * from UUM_USER order by id desc) A order by state"));
		ITypeConverter converter = GenericConverterFactory.getInstance().getConverter(java.sql.Date.class, Timestamp.class);
		System.out.println(converter);
		System.out.println(GenericConverterFactory.getInstance().needConvert(Date.class, Timestamp.class));
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("Integer", 12);
		m.put("String", "String");
		m.put("Float", 1.3F);
		m.put("Object", new Date());
		System.out.println(m);
//		System.out.println(Arrays.asList(null));
		DefaultPojoSerializer serializer = new DefaultPojoSerializer();
		User user = new User(10l);
		user.setUserName("user&name");
		user.setBirthday(new java.sql.Date(System.currentTimeMillis()));
		user.setState(10.2);
		user.setEmail("xunchangguo@gmail.com");
		Document doc = serializer.serialize(user, "UUM_USER");
		System.out.println(doc.asXML());
		String xml = serializer.serialize2(user, "UUM_USER");
		System.out.println(xml);
		User duser = serializer.deserialize(User.class, xml);
		System.out.println(duser.getUserName());
		System.out.println(duser.getState());
		System.out.println(Utils.dateFormat(duser.getBirthday(), "yyyy-MM-dd HH:mm:ss"));
		start = System.currentTimeMillis();
		Map<String, Object> map = serializer.deserialize2(User.class, xml);
		System.out.println("耗时：" + (System.currentTimeMillis() - start) + " ms");
		System.out.println(map);
		for(String key : map.keySet()) {
			Object val = map.get(key);
			System.out.println(key +" = " + val + ", type = " + val.getClass());
		}
		
		System.out.println("##############");
		DefaultJsonSerializer jsonSerializer = new DefaultJsonSerializer();
		jsonSerializer.setSerializeNulls(false);
		String json = jsonSerializer.serialize(duser);
		System.out.println(json);
		User u = jsonSerializer.deserialize(User.class, json);
		System.out.println(u.getUserName());
		System.out.println(u.getState());
		System.out.println(Utils.dateFormat(u.getBirthday(), "yyyy-MM-dd HH:mm:ss"));
		System.out.println("===============");
		String j = "{\"ID\":1,\"LOGIN_NAME\":\"取\u0026而\",\"USER_NAME\":\"user\u0026name\",\"PASSWORD\":\"o-着急啊看到password-0\",\"DESCRIPTION\":\"o-着急啊看到desc-0\",\"STATE\":1,\"CREATE_TIME\":\"2013-06-26 15:14:46\",\"USER_CARD\":\"usercard-0\",\"USER_TYPE\":0,\"EMAIL\":\"email-0\",\"SEX\":0,\"ADDRESS\":\"address-0\",\"BIRTHDAY\":\"2013-06-26 15:14:46\"}";
		System.out.println(new JsonStreamParser(j).next().getAsJsonObject().get("LOGIN_NAME").getAsString());
		Map<String, Object> jmap = jsonSerializer.deserialize2(User.class, j);
		for(String key : jmap.keySet()) {
			Object val = jmap.get(key);
			System.out.println(key +" = " + val + ", type = " + val.getClass());
		}
	}

}
