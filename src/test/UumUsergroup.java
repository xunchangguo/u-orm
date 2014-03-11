/*
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
 * 文件：test.UumUsergroup.java
 * 日 期：Mon Dec 30 13:25:25 CST 2013
 */
package test;

import java.io.Serializable;
import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;

/**
 *
 * this file is generated by the uorm pojo tools.
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 */
@ClassMapping(tableName = "UUM_USERGROUP", keyGenerator = "increment")
public class UumUsergroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String PROP_APP_PARAM = "APP_PARAM";
	public static String PROP_DESCRIPTION = "DESCRIPTION";
	public static String PROP_FIELD_1 = "FIELD_1";
	public static String PROP_FIELD_2 = "FIELD_2";
	public static String PROP_FOLDER_PATH = "FOLDER_PATH";
	public static String PROP_GROUP_CODE = "GROUP_CODE";
	public static String PROP_ID = "ID";
	public static String PROP_NAME = "NAME";
	public static String PROP_PARENT_ID = "PARENT_ID";
	public static String PROP_QUOTA_SIZE = "QUOTA_SIZE";
	public static String PROP_QUOTA_USED = "QUOTA_USED";
	public static String PROP_TYPE = "TYPE";
	
	/** primary key field of id */
	@FieldMapping(columnName = "ID", columnType = 4, primary = true)
	private Integer id;
	@FieldMapping(columnName = "APP_PARAM", columnType = 12)
	private String appParam;
	@FieldMapping(columnName = "DESCRIPTION", columnType = 12)
	private String description;
	@FieldMapping(columnName = "FIELD_1", columnType = 12)
	private String field1;
	@FieldMapping(columnName = "FIELD_2", columnType = 12)
	private String field2;
	@FieldMapping(columnName = "FOLDER_PATH", columnType = 12)
	private String folderPath;
	@FieldMapping(columnName = "GROUP_CODE", columnType = 12)
	private String groupCode;
	@FieldMapping(columnName = "NAME", columnType = 12)
	private String name;
	/** foreign key field of UUM_USERGROUP.ID */
	@FieldMapping(columnName = "PARENT_ID", columnType = 4)
	private Integer parentId;
	@FieldMapping(columnName = "QUOTA_SIZE", columnType = 4)
	private Integer quotaSize;
	@FieldMapping(columnName = "QUOTA_USED", columnType = 4)
	private Integer quotaUsed;
	@FieldMapping(columnName = "TYPE", columnType = 4)
	private Integer type;
	
	public UumUsergroup() {
		super();
	}

	public UumUsergroup(Integer id) {
		this.id = id;
	}

	public UumUsergroup(String name, Integer parentId, Integer type) {
		this.name = name;
		this.parentId = parentId;
		this.type = type;
	}

	/**
	 * @return the appParam
	 */
	public String getAppParam() {
		return this.appParam;
	}
	
	/**
	 * @param appParam the appParam to set
	 */
	public void setAppParam(String value) {
		this.appParam = value;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String value) {
		this.description = value;
	}

	/**
	 * @return the field1
	 */
	public String getField1() {
		return this.field1;
	}
	
	/**
	 * @param field1 the field1 to set
	 */
	public void setField1(String value) {
		this.field1 = value;
	}

	/**
	 * @return the field2
	 */
	public String getField2() {
		return this.field2;
	}
	
	/**
	 * @param field2 the field2 to set
	 */
	public void setField2(String value) {
		this.field2 = value;
	}

	/**
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return this.folderPath;
	}
	
	/**
	 * @param folderPath the folderPath to set
	 */
	public void setFolderPath(String value) {
		this.folderPath = value;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return this.groupCode;
	}
	
	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String value) {
		this.groupCode = value;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer value) {
		this.id = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return this.parentId;
	}
	
	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Integer value) {
		this.parentId = value;
	}

	/**
	 * @return the quotaSize
	 */
	public Integer getQuotaSize() {
		return this.quotaSize;
	}
	
	/**
	 * @param quotaSize the quotaSize to set
	 */
	public void setQuotaSize(Integer value) {
		this.quotaSize = value;
	}

	/**
	 * @return the quotaUsed
	 */
	public Integer getQuotaUsed() {
		return this.quotaUsed;
	}
	
	/**
	 * @param quotaUsed the quotaUsed to set
	 */
	public void setQuotaUsed(Integer value) {
		this.quotaUsed = value;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return this.type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(Integer value) {
		this.type = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof UumUsergroup)) {
			return false;
		}
		UumUsergroup other = (UumUsergroup)o;
		if (null == this.id) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
}