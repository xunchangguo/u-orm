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
 * 文件：test.NmpPerfdata.java
 * 日 期：Thu Feb 20 14:45:10 CST 2014
 */
package test;

import java.io.Serializable;
import java.util.Date;

import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;

/**
 *
 * 存储任务采集指标历史数据
 * this file is generated by the uorm pojo tools.
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 */
@ClassMapping(tableName = "NMP_PERFDATA", keyGenerator = "native")
public class NmpPerfdata implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String PROP_COLL_TIME = "COLL_TIME";
	public static String PROP_DEV_ID = "DEV_ID";
	public static String PROP_ID = "ID";
	public static String PROP_KPI_ID = "KPI_ID";
	public static String PROP_KPI_VALUE = "KPI_VALUE";
	public static String PROP_OBJECT_ID = "OBJECT_ID";
	public static String PROP_TASK_ID = "TASK_ID";
	
	/**
	 * primary key field of id
	 * 
	 */
	@FieldMapping(columnName = "ID", columnType = 4, primary = true)
	private Long id;
	/** 采集时间 */
	@FieldMapping(columnName = "COLL_TIME", columnType = 93)
	private Date collTime;
	/** 物理设备编号 */
	@FieldMapping(columnName = "DEV_ID", columnType = 4)
	private Integer devId;
	/**  */
	@FieldMapping(columnName = "KPI_ID", columnType = 4)
	private Integer kpiId;
	/**  */
	@FieldMapping(columnName = "KPI_VALUE", columnType = 12)
	private String kpiValue;
	/**  */
	@FieldMapping(columnName = "OBJECT_ID", columnType = 4)
	private Integer objectId;
	/** 任务ID */
	@FieldMapping(columnName = "TASK_ID", columnType = 4)
	private Long taskId;
	
	public NmpPerfdata() {
		super();
	}

	public NmpPerfdata(Long id) {
		this.id = id;
	}

	/**
	 * @return the collTime 采集时间
	 */
	public Date getCollTime() {
		return this.collTime;
	}
	
	/**
	 * 采集时间
	 * @param collTime the collTime to set
	 */
	public void setCollTime(Date collTime) {
		this.collTime = collTime;
	}

	/**
	 * @return the devId 物理设备编号
	 */
	public Integer getDevId() {
		return this.devId;
	}
	
	/**
	 * 物理设备编号
	 * @param devId the devId to set
	 */
	public void setDevId(Integer devId) {
		this.devId = devId;
	}

	/**
	 * @return the id 
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the kpiId 
	 */
	public Integer getKpiId() {
		return this.kpiId;
	}
	
	/**
	 * 
	 * @param kpiId the kpiId to set
	 */
	public void setKpiId(Integer kpiId) {
		this.kpiId = kpiId;
	}

	/**
	 * @return the kpiValue 
	 */
	public String getKpiValue() {
		return this.kpiValue;
	}
	
	/**
	 * 
	 * @param kpiValue the kpiValue to set
	 */
	public void setKpiValue(String kpiValue) {
		this.kpiValue = kpiValue;
	}

	/**
	 * @return the objectId 
	 */
	public Integer getObjectId() {
		return this.objectId;
	}
	
	/**
	 * 
	 * @param objectId the objectId to set
	 */
	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the taskId 任务ID
	 */
	public Long getTaskId() {
		return this.taskId;
	}
	
	/**
	 * 任务ID
	 * @param taskId the taskId to set
	 */
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof NmpPerfdata)) {
			return false;
		}
		NmpPerfdata other = (NmpPerfdata)o;
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