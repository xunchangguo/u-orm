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

/**
 * class 工具类
 * 
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-18       郭训常            创建<br/>
 */
public class ClassUtil {
	/**
	 * 判断目标类是不是类的子类
	 * @param targetClass
	 * @param superClass
	 * @return
	 */
	public static boolean isSubclassOf(Class<?> targetClass, Class<?> superClass){
		Class<?> targetSuperClass = targetClass.getSuperclass();
		while(targetSuperClass != null){
			if(targetSuperClass.equals(superClass)) return true;
			targetSuperClass = targetSuperClass.getSuperclass();
		}
		return false;
	}

	/**
	 * 目标类是否实现了指定接口
	 * @param targetClass
	 * @param theInterface
	 * @return
	 */
	public static boolean isInterfaceOrSubInterfaceImplemented(Class<?> targetClass, Class<?> theInterface){
		Class<?>[] implementedInterfaces = targetClass.getInterfaces();
		for(Class<?> implementedInterface : implementedInterfaces){
			if(implementedInterface.equals(theInterface)) return true;
			Class<?> superInterface = implementedInterface.getSuperclass();
			while(superInterface != null){
				if(superInterface.equals(theInterface)) return true;
				superInterface = superInterface.getSuperclass();
			}
		}
		return false;
	}
	/**
	 * 方法是不是get方法
	 * @param member
	 * @return
	 */
	public static boolean isGetter(Method member){
		if(member == null){
			throw new NullPointerException("No Method instance provided");
		}

		if(member.getParameterTypes().length > 0){
			return false;
		}

		if(member.getReturnType() == void.class || member.getReturnType() == null){
			return false;
		}

		return  member.getName().startsWith("get") || member.getName().startsWith("is");
	}

	/**
	 * 方法是 不是set方法
	 * @param member
	 * @return
	 */
	public static boolean isSetter(Method member){
		if(member == null){
			throw new NullPointerException("No Method instance provided");
		}

		if( !member.getName().startsWith("set") ){
			return false;
		}
		if( member.getParameterTypes().length != 1 ){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取class的名字（去除包）
	 * @param objectClass
	 * @return
	 */
	public static String classNameWithoutPackage(Class<?> objectClass){
        return classNameWithoutPackage(objectClass.getName());

    }

    /**
     * 获取class的名字（去除包）
     * @param fullClassName 完整类名 
     * @return
     */
    public static String classNameWithoutPackage(String fullClassName){
        return fullClassName.substring(fullClassName.lastIndexOf(".")+1, fullClassName.length());
    }
}
