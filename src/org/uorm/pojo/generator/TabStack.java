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
 * 控制生成的代码格式，空格，TAB
 * 
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class TabStack {
	private StringBuilder content;
	private int stackSize;

	public TabStack()
	{
		this(0);
	}

	public TabStack(int size) {
		this.stackSize = size;
		this.content = new StringBuilder();
	}

	public StringBuilder getContent() {
		return this.content;
	}

	public int stackSize() {
		return this.stackSize;
	}

	public void reset() {
		this.stackSize = 0;
	}

	public void push() {
		push(1);
	}

	public void push(int count) {
		this.stackSize += count;
		appendEOL();
	}

	public void pop() {
		pop(1);
	}

	public void pop(int count) {
		if (this.stackSize - count > 0) {
			this.stackSize -= count;
		} else {
			this.stackSize = 0;
		}
		appendEOL();
	}

	public void append(String value) {
		this.content.append(value);
	}

	public void append(int value) {
		this.content.append(value);
	}

	public void append(long value) {
		this.content.append(value);
	}

	public void appendEOL() {
		appendEOL(1);
	}

	public void appendEOL(int count) {
		for (int i = 0; i < count; i++) {
			this.content.append(GenUtil.LINE_END);
		}
		for (int i = 0; i < this.stackSize; i++) {
			this.content.append('\t');
		}
	}

	public String toString() {
		return this.content.toString();
	}
}
