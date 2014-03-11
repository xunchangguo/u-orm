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
import java.util.List;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-24       郭训常            创建<br/>
 */
public class PaginationSupport<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static final int PAGESIZE = 20;//默认每页记录数
	
	private int pagesize = PAGESIZE;//页大小
	
	private long totalCount = 0;//总记录数
	
	private int pageCount = 0;//总页数
	
	private int startPage = 0;//当前页
	
	private List<T> items = null;//当前页数据
	
	public int getPageCount() {
		return pageCount;
	}
	
	public PaginationSupport(int pagesize, long totalCount, int startPage, List<T> items) {
		super();
		setPagesize(pagesize);
		setTotalCount(totalCount);
		setStartPage(startPage);
		this.items = items;
	}
	
	public PaginationSupport(long totalCount, List<T> items) {
		super();
		setPagesize(PAGESIZE);
		setTotalCount(totalCount);
		setStartPage(0);
		this.items = items;
	}
	
	public PaginationSupport(int pagesize, long totalCount, List<T> items) {
		super();
		setPagesize(pagesize);
		setTotalCount(totalCount);
		setStartPage(0);
		this.items = items;
	}
	
	/*
	 * 设置总页数
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount<0?0:pageCount;
	}
	
	/*
	 * 取页大小
	 */
	public int getPagesize() {
		return pagesize;
	}
	
	/*
	 * 设置每页大小，默认20
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize<0?PAGESIZE:pagesize;
	}
	
	/*
	 * 取当前页数
	 */
	public int getStartPage() {
		return startPage;
	}
	
	/*
	 * 设置当前页数
	 */
	public void setStartPage(int startPage) {
		if (startPage<0 || startPage >= pageCount) {
			this.startPage = 0;
		}
		else{
			this.startPage =startPage; 
		}
	}
	
	/*
	 * 取总记录数
	 */
	public long getTotalCount() {
		return totalCount;
	}
	
	/**
	 * 设置总记录条数
	 */
	public void setTotalCount(long totalCount) {
		if (totalCount > 0) {
			this.totalCount = totalCount;
			setPageCount((int)Math.ceil((double)totalCount / pagesize));			
		} else {
			this.totalCount = 0;
			setPageCount(0);
		}
	}
	
	/**
	 * 取存入当前页的数据
	 */
	public List<T> getItems() {
		return items;
	}
	
	/**
	 * set 结果到数据中
	 */
	public void setItems(List<T> items) {
		this.items = items;
	}
	
	/**
	 * 取下一页
	 */
	public int getNextPage(){
		return startPage >= pageCount ? pageCount - 1 : startPage;
	}
	
	/**
	 * 取前一页
	 */
	public int getPreviousPage(){
		return startPage <= 0 ? 0 : startPage;
	}
	
	/**
	 * 是否有下一页
	 */	
	public boolean hasNextPage() {
		return (getStartPage() < getPageCount() - 1);
	}
	
	/**
	 * 是否有前一页
	 */
	public boolean hasPreviousPage() {
        return (getStartPage() > 0);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if(items != null)
			items = null;
		super.finalize();
	}
}
