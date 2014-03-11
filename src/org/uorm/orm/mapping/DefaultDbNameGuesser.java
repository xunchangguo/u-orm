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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IDbNameGuesser默认实现方法
 * 
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class DefaultDbNameGuesser implements IDbNameGuesser {

	/* (non-Javadoc)
	 * @see jetsennet.sqlclient.orm.mapping.IDbNameGuesser#getPossibleColumnNames(java.lang.reflect.Method)
	 */
	public Collection<String> getPossibleColumnNames(Method member) {
		if(ClassUtil.isGetter(member)){
            if(member.getName().startsWith("is")){
                return getPossibleNames(member.getName().substring(2, member.getName().length()));
            } else {
                return getPossibleNames(member.getName().substring(3, member.getName().length()));
            }
        } else if( ClassUtil.isSetter(member)){
            return getPossibleNames(member.getName().substring(3, member.getName().length()));
        }
        return getPossibleNames(member.getName());
	}

	/* (non-Javadoc)
	 * @see jetsennet.sqlclient.orm.mapping.IDbNameGuesser#getPossibleNames(java.lang.String)
	 */
	public Collection<String> getPossibleNames(String fieldname) {
		Set<String> names = new HashSet<String>();
        String fieldName = fieldname;
        String fieldNameFirstLetterLowerCase =
                fieldName.substring(0,1).toLowerCase() + fieldName.substring(1, fieldName.length()) ;
        String fieldNameFirstLetterUpperCase =
                fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.length()) ;
        names.add(fieldName);
        names.add(fieldNameFirstLetterUpperCase);
        names.add(fieldNameFirstLetterLowerCase);
        names.add(fieldName.toUpperCase());
        names.add(fieldName.toLowerCase());

        List<String> words = getWords(fieldName);

        String underScoreFieldName = getUnderScoreSeparatedWordsColumnName(words);
        names.add(underScoreFieldName);
        names.add(underScoreFieldName.toLowerCase());
        names.add(underScoreFieldName.toUpperCase());
        String numUnderScoreFieldName = getNumberUnderScoreSeparatedWordsColumnName(words);
        names.add(numUnderScoreFieldName);
        names.add(numUnderScoreFieldName.toLowerCase());
        names.add(numUnderScoreFieldName.toUpperCase());
        
        String possColumnName = getPossibleColumnName(fieldName);
        names.add(possColumnName);
        names.add(possColumnName.toLowerCase());
        names.add(possColumnName.toUpperCase());
        String possNumColumnName = getNumberPossibleColumnName(fieldName);
        names.add(possNumColumnName);
        names.add(possNumColumnName.toLowerCase());
        names.add(possNumColumnName.toUpperCase());
        return names;
	}
	
	private String getPossibleColumnName(String fieldName) {
		StringBuffer name = new StringBuffer();
		char[] chars = fieldName.toCharArray();
		for (char c : chars) {
			if(Character.isUpperCase(c) && name.length() > 0) {
				name.append('_');
			}
			name.append(c);
		}
		return name.toString();
	}
	
	private String getNumberPossibleColumnName(String fieldName) {
		StringBuffer name = new StringBuffer();
		char[] chars = fieldName.toCharArray();
		for (char c : chars) {
			if((Character.isDigit(c) || Character.isUpperCase(c)) && (name.length() > 0)){
				name.append('_');
			}
			name.append(c);
		}
		return name.toString();
	}

	private String getUnderScoreSeparatedWordsColumnName(List<String> words) {
        StringBuffer buffer = new StringBuffer();
        int i = 1;
        for(String word : words){
        	buffer.append(word);
            if( i < words.size()){
                buffer.append('_');
            }
            i ++;
        }

        return buffer.toString();
    }
	
	private String getNumberUnderScoreSeparatedWordsColumnName(List<String> words) {
        String buffer = "";
        int i = 1;
        for(String word : words){
        	if(isNumeric(word)){
        		buffer = buffer.substring(0, buffer.length() - 1);
        	}
        	buffer += word;
        	if( i < words.size()){
        		buffer += '_';
        	}
            i ++;
        }

        return buffer;
    }
	
	public static boolean isNumeric(String str){
		for (int i = str.length();--i>=0;){   
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}


    protected List<String> getWords(String fieldName){
        WordTokenizer tokenizer = new WordTokenizer(fieldName);
        List<String> words = new ArrayList<String>();

        while(tokenizer.hasMoreWords()){
            words.add(tokenizer.nextWord());
        }

        return words;
    }
    
    
	/* (non-Javadoc)
	 * @see jetsennet.sqlclient.orm.mapping.IDbNameGuesser#getPossibleTableNames(java.lang.Class)
	 */
	public Collection<String> getPossibleTableNames(Class<?> object) {
		Collection<String> possibleNames = getPossibleNames(ClassUtil.classNameWithoutPackage(object));
		return possibleNames;
	}

}
