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

import java.io.Serializable;

/**
 * sql参数
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-26       郭训常            创建<br/>
 */
public class SqlParameter {

	/** 参数名，为ORM对应class变量名或表字段名 */
	private String name;
	
	/** 参数值 */
	private Serializable value;

	/** 此参数名对应的ORM class ,为null、则使用输入参数的ORM class*/
	private Class<?> ormClass = null;

	/**
	 * 
	 */
	public SqlParameter() {
		super();
	}

	/**
	 * @param name
	 * @param value
	 */
	public SqlParameter(String name, Serializable value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * @param name
	 * @param value
	 * @param ormClass
	 */
	public SqlParameter(String name, Serializable value, Class<?> ormClass) {
		super();
		this.name = name;
		this.value = value;
		this.ormClass = ormClass;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Serializable getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Serializable value) {
		this.value = value;
	}

	/**
	 * @return the ormClass
	 */
	public Class<?> getOrmClass() {
		return ormClass;
	}

	/**
	 * @param ormClass the ormClass to set
	 */
	public void setOrmClass(Class<?> ormClass) {
		this.ormClass = ormClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s=%s", name, value);
	}
}
