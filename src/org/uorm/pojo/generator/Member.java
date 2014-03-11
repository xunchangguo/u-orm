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
package org.uorm.pojo.generator;


/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class Member implements Comparable<Member> {
	private String name;
	private Class<?> type;
	private int size;
	private boolean nullable;
	private boolean writable;
	private boolean pk;
	private String colname;
	private int coltype = 12;
	private boolean fk = false;
	private String reffield = null;
	private String remark = null;

	public Member() {
		this.name = "MyClassName";
		this.type = String.class;
		this.nullable = true;
		this.writable = true;
		this.pk = false;
		this.size = 0;
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
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the writable
	 */
	public boolean isWritable() {
		return writable;
	}

	/**
	 * @param writable the writable to set
	 */
	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	/**
	 * @return the pk
	 */
	public boolean isPk() {
		return pk;
	}

	/**
	 * @param pk the pk to set
	 */
	public void setPk(boolean pk) {
		this.pk = pk;
	}

	/**
	 * @return the coltype
	 */
	public int getColtype() {
		return coltype;
	}

	/**
	 * @param coltype the coltype to set
	 */
	public void setColtype(int coltype) {
		this.coltype = coltype;
	}

	/**
	 * @return the colname
	 */
	public String getColname() {
		return colname;
	}

	/**
	 * @param colname the colname to set
	 */
	public void setColname(String colname) {
		this.colname = colname;
	}

	/**
	 * @return the fk
	 */
	public boolean isFk() {
		return fk;
	}

	/**
	 * @param fk the fk to set
	 */
	public void setFk(boolean fk) {
		this.fk = fk;
	}

	/**
	 * @return the reffield
	 */
	public String getReffield() {
		return reffield;
	}

	/**
	 * @param reffield the reffield to set
	 */
	public void setReffield(String reffield) {
		this.reffield = reffield;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getImport() {
		String className = this.type.getName();
		if ((className.startsWith("java.lang")) || (this.type.isArray())) {
			return "";
		}
		StringBuilder sb = new StringBuilder("import ");
		sb.append(className);
		sb.append(';');
		sb.append(GenUtil.LINE_END);
		return sb.toString();
	}

	public String decapitalize(String value) {
		return value.substring(0, 1).toLowerCase() + value.substring(1);
	}

	public String capitalize(String value) {
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}
	
//	public void addField(TabStack code) {
//		if(this.pk){
//			code.append("//primary key field of ");
//			code.append(decapitalize(this.name));
//			code.appendEOL();
//		}
//		code.append("@FieldMapping(columnName = \"");
//		code.append(colname);
//		code.append("\", columnType = ");
//		code.append(coltype);
//		if(this.pk){
//			code.append(", primary = true");
//		}
//		code.append(")");
//		code.appendEOL();
//		
//		code.append("private ");
//		code.append(this.type.getSimpleName());
//		code.append(" ");
//		code.append(decapitalize(this.name));
//		code.append(";");
//		code.appendEOL();
//	}
	
	public void addField(TabStack code, boolean genJsonAnnotations) {
		if(this.pk && this.fk){
			code.append("/**");
			code.appendEOL();
			code.append(" * primary key field of ");
			code.append(decapitalize(this.name));
			code.appendEOL();
			code.append(" * foreign key field of ");
			code.append(this.reffield);
			code.appendEOL();
			if(remark != null) {
				code.append(" * ");
				code.append(remark);
				code.appendEOL();
			}
			code.append(" */");
			code.appendEOL();
		} else {
			if(this.pk){
				code.append("/**");
				code.appendEOL();
				code.append(" * primary key field of ");
				code.append(decapitalize(this.name));
				code.appendEOL();
				if(remark != null) {
					code.append(" * ");
					code.append(remark);
					code.appendEOL();
				}
				code.append(" */");
				code.appendEOL();
			} else if(this.fk) {
				code.append("/**");
				code.appendEOL();
				code.append(" * foreign key field of ");
				code.append(this.reffield);
				code.appendEOL();
				if(remark != null) {
					code.append(" * ");
					code.append(remark);
					code.appendEOL();
				}
				code.append(" */");
				code.appendEOL();
			} else{
				if(remark != null) {
					code.append("/** ");
					code.append(remark);
					code.append(" */");
					code.appendEOL();
				}
			}
		}
		code.append("@FieldMapping(columnName = \"");
		code.append(colname);
		code.append("\", columnType = ");
		code.append(coltype);
		if(this.pk){
			code.append(", primary = true");
		}
		code.append(")");
		code.appendEOL();
		if(genJsonAnnotations) {
			//gen gson json Annotations
			code.append("@com.google.gson.annotations.SerializedName(\"");
			code.append(colname);
			code.append("\")");
			code.appendEOL();
		}
		code.append("private ");
		code.append(this.type.getSimpleName());
		code.append(" ");
		code.append(decapitalize(this.name));
		code.append(";");
		code.appendEOL();
	}

	/**
	 * add geter and seter methods
	 * @param code
	 */
	public void addAccessors(TabStack code) {
		addGetter(code);
		code.appendEOL();
		code.appendEOL();
		addSetter(code);
	}

	private void addGetter(TabStack code) {
		code.append("/**");
		code.appendEOL();
		code.append(" * @return the ");
		code.append(decapitalize(this.name));
		if(this.remark != null) {
			code.append(" ");
			code.append(remark);
		}
		code.appendEOL();
		code.append(" */");
		code.appendEOL();
		
		code.append("public ");
		code.append(this.type.getSimpleName());
		code.append(" get");
		code.append(capitalize(this.name));
		code.append("() {");
		code.push();
		code.append("return this.");
		code.append(decapitalize(this.name));
		code.append(";");
		code.pop();
		code.append("}");
	}

	private void addSetter(TabStack code) {
		code.append("/**");
		code.appendEOL();
		if(remark != null) {
			code.append(" * ");
			code.append(remark);
			code.appendEOL();
		}
		String pname = decapitalize(this.name);
		code.append(" * @param ");
		code.append(pname);
		code.append(" the ");
		code.append(pname);
		code.append(" to set");
		code.appendEOL();
		code.append(" */");
		code.appendEOL();
		
		String modifier = "public";
		if (!this.writable) {
			modifier = "protected";
		}
		code.append(modifier);
		code.append(" void");
		code.append(" set");
		code.append(capitalize(this.name));
		code.append("(");
		code.append(this.type.getSimpleName());
		code.append(" ");
		code.append(pname);
		code.append(") {");
		code.push();
		code.append("this.");
		code.append(pname);
		code.append(" = ");
		code.append(pname);
		code.append(";");
		code.pop();
		code.append("}");
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Member o) {
		return this.name.compareToIgnoreCase(o.name);
	}

}