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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.uorm.orm.mapping.ObjectMappingCache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

/**
 * default json serializer use gson
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2013-7-1       郭训长            创建<br/>
 */
public class DefaultJsonSerializer implements IJsonSerializer {
	private Gson gson = null;
	private boolean serializeNulls = true;
	private String datePattern = "yyyy-MM-dd HH:mm:ss";
	private boolean prettyPrinting = false;
	private boolean excludeFieldsWithoutExposeAnnotation = false;
	private boolean enableComplexMapKey = false;
	private Double version = null;//ignoreVersionsAfter
	
	

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IJsonSerializer#serialize(java.lang.Object)
	 */
	@Override
	public String serialize(Object src) throws Exception {
		return getGson().toJson(src);
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IJsonSerializer#deserialize(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T deserialize(Class<T> cls, String json) throws Exception {
		return getGson().fromJson(json, cls);
	}

	/* (non-Javadoc)
	 * @see org.uorm.serializer.IJsonSerializer#deserialize2(java.lang.Class, java.lang.String)
	 */
	@Override
	public Map<String, Object> deserialize2(Class<?> cls, String json)
			throws Exception {
		JsonStreamParser parser = new JsonStreamParser(json);
		if(parser.hasNext()){
			JsonObject jsonobj = parser.next().getAsJsonObject();
			Set<Entry<String, JsonElement>> jset = jsonobj.entrySet();
			if(!jset.isEmpty()) {
				Map<String, PropertyDescriptor> propMap = ObjectMappingCache.getInstance().getObjectPropertyMap(cls);
				Map<String, Object> instance = new HashMap<String, Object>();
				for (Entry<String, JsonElement> entry : jset) {
					String name = entry.getKey();
					JsonElement val = entry.getValue();
					if(!val.isJsonNull()) {
						PropertyDescriptor descriptor = propMap.get(name);
						if(descriptor != null) {
							Class<?> memberType = descriptor.getPropertyType();
							if(memberType == String.class) {
								instance.put(name, val.getAsString());
							}else if(memberType == Integer.class || memberType == Integer.TYPE) {
								instance.put(name, val.getAsInt());
							}else if(memberType == Byte.class || memberType == Byte.TYPE) {
								instance.put(name, val.getAsByte());
							}else if(memberType == Double.class || memberType == Double.TYPE) {
								instance.put(name, val.getAsDouble());
							}else if(memberType == Float.class || memberType == Float.TYPE) {
								instance.put(name, val.getAsFloat());
							}else if(memberType == Long.class || memberType == Long.TYPE) {
								instance.put(name, val.getAsLong());
							}else if(memberType == Short.class || memberType == Short.TYPE) {
								instance.put(name, val.getAsShort());
							}else if(memberType == Boolean.class || memberType == Boolean.TYPE) {
								instance.put(name, val.getAsBoolean());
							}else if(memberType == BigDecimal.class) {
								instance.put(name, val.getAsBigDecimal());
							}else if(memberType == BigInteger.class) {
								instance.put(name, val.getAsBigInteger());
							}else if(memberType == byte[].class) {
								instance.put(name, val.getAsString().getBytes());
							}else if(memberType == java.sql.Timestamp.class) {
								Long time = parserDate(val.getAsString());
								if(time == null) {
									instance.put(name, null);
								}else{
									instance.put(name, new java.sql.Timestamp(time));
								}
							}else if(memberType == java.sql.Date.class) {
								Long time = parserDate(val.getAsString());
								if(time == null) {
									instance.put(name, null);
								}else{
									instance.put(name, new java.sql.Date(time));
								}
							}else if(memberType == java.util.Date.class) {
								Long time = parserDate(val.getAsString());
								if(time == null) {
									instance.put(name, null);
								}else{
									instance.put(name, new java.util.Date(time));
								}
							}else{//default String
								instance.put(name, val.getAsString());
							}
						}else{//未定义类型，返回String
							instance.put(name, val.getAsString());
						}
					}
				}
				return instance;
			}
		}
		return null;
	}
	
	private Long parserDate(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		Date dt = null;
		try{
			dt = sdf.parse(strDate);
		}catch(Exception e){
			return null;
		}
		return dt.getTime();
	}

	/**
	 * @return the gson
	 */
	public Gson getGson() {
		if(gson == null) {
			GsonBuilder gbuilder = new GsonBuilder();
			if(serializeNulls){
				gbuilder.serializeNulls();
			}
			gbuilder.setDateFormat(datePattern);
			if(prettyPrinting) {
				gbuilder.setPrettyPrinting();
			}
			if(excludeFieldsWithoutExposeAnnotation) {
				gbuilder.excludeFieldsWithoutExposeAnnotation();
			}
			if(enableComplexMapKey) {
				gbuilder.enableComplexMapKeySerialization();
			}
			if(version != null) {
				gbuilder.setVersion(version);
			}
			gson = gbuilder.create();
		}
		return gson;
	}

	/**
	 * @param gson the gson to set
	 */
	public void setGson(Gson gson) {
		this.gson = gson;
	}

	/**
	 * @return the serializeNulls
	 */
	public boolean isSerializeNulls() {
		return serializeNulls;
	}

	/**
	 * @param serializeNulls the serializeNulls to set
	 */
	public void setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}

	/**
	 * @return the datePattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * @param datePattern the datePattern to set
	 */
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	/**
	 * @return the prettyPrinting
	 */
	public boolean isPrettyPrinting() {
		return prettyPrinting;
	}

	/**
	 * @param prettyPrinting the prettyPrinting to set
	 */
	public void setPrettyPrinting(boolean prettyPrinting) {
		this.prettyPrinting = prettyPrinting;
	}

	/**
	 * @return the excludeFieldsWithoutExposeAnnotation
	 */
	public boolean isExcludeFieldsWithoutExposeAnnotation() {
		return excludeFieldsWithoutExposeAnnotation;
	}

	/**
	 * @param excludeFieldsWithoutExposeAnnotation the excludeFieldsWithoutExposeAnnotation to set
	 */
	public void setExcludeFieldsWithoutExposeAnnotation(
			boolean excludeFieldsWithoutExposeAnnotation) {
		this.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
	}

	/**
	 * @return the enableComplexMapKey
	 */
	public boolean isEnableComplexMapKey() {
		return enableComplexMapKey;
	}

	/**
	 * @param enableComplexMapKey the enableComplexMapKey to set
	 */
	public void setEnableComplexMapKey(boolean enableComplexMapKey) {
		this.enableComplexMapKey = enableComplexMapKey;
	}

	/**
	 * @return the version
	 */
	public Double getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(Double version) {
		this.version = version;
	}

}
