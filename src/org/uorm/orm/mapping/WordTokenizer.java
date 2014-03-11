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
package org.uorm.orm.mapping;

import java.util.StringTokenizer;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class WordTokenizer {
	StringTokenizer tokenizer = null;

    public WordTokenizer(String memberName){
        String memberNameFirstLetterUpperCase =
                memberName.substring(0,1).toUpperCase() + memberName.substring(1, memberName.length());

        this.tokenizer = new StringTokenizer(memberNameFirstLetterUpperCase, "ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789", true);
    }


    public String nextWord(){
        if(!this.tokenizer.hasMoreTokens()) {
        	return null;
        }
        String nextWordFirstLetter = this.tokenizer.nextToken();

        if(!this.tokenizer.hasMoreTokens()) {
        	return nextWordFirstLetter;
        }
        String nextWordEnd = this.tokenizer.nextToken();

        return nextWordFirstLetter + nextWordEnd;
    }

    public boolean hasMoreWords(){
        return this.tokenizer.hasMoreTokens();
    }
}
