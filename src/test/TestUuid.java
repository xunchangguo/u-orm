/*
 * Copyright (c) 北京捷成世纪科技股份有限公司. All Rights Reserved.
 * 文件：test.TestUuid.java
 * 日 期：Mon Jan 30 17:20:18 CST 2012
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
@ClassMapping(tableName = "TEST_UUID", keyGenerator = "org.uorm.dao.id.UUIDGenerator")
public class TestUuid implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String PROP_ID = "id";
	public static String PROP_SEQ_NUM = "seqNum";
	
	//primary key field of id
	@FieldMapping(columnName = "ID", columnType = 12, primary = true)
	private String id;
	@FieldMapping(columnName = "SEQ_NUM", columnType = 4)
	private Integer seqNum;
	
	public TestUuid() {
		super();
	}

	public TestUuid(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * @return the seqNum
	 */
	public Integer getSeqNum() {
		return this.seqNum;
	}
	
	/**
	 * @param seqNum the seqNum to set
	 */
	public void setSeqNum(Integer value) {
		this.seqNum = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof TestUuid)) {
			return false;
		}
		TestUuid other = (TestUuid)o;
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