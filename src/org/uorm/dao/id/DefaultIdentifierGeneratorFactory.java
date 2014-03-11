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
package org.uorm.dao.id;

import java.util.HashMap;
import java.util.Map;

import org.uorm.orm.annotation.KeyGenertator;
import org.uorm.utils.PropertyHolderUtil;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-30       郭训长            创建<br/>
 */
public class DefaultIdentifierGeneratorFactory implements
		IdentifierGeneratorFactory {

	private Map<String, Class<?>> generator2ClassMap =  null;
	private Map<String, IdentifierGenerator> generator2IdentifierGeneratorInstMap = null;
	
	/**
	 * 
	 */
	public DefaultIdentifierGeneratorFactory() {
		super();
		generator2ClassMap = new HashMap<String, Class<?>>();
		generator2ClassMap.put(KeyGenertator.IDENTITY, org.uorm.dao.id.IdentityGenerator.class);
		generator2ClassMap.put(KeyGenertator.UUID, org.uorm.dao.id.UUIDGenerator.class);
		generator2ClassMap.put(KeyGenertator.UUIDHEX, org.uorm.dao.id.UUIDHexGenerator.class);
		generator2ClassMap.put(KeyGenertator.GUID, org.uorm.dao.id.GUIDGenerator.class);
		generator2ClassMap.put(KeyGenertator.INCREMENT, org.uorm.dao.id.IncrementGenerator.class);
		generator2ClassMap.put(KeyGenertator.SEQUENCE, org.uorm.dao.id.SequenceGenerator.class);
		generator2ClassMap.put(KeyGenertator.SELECT, org.uorm.dao.id.SelectGenerator.class);
		generator2ClassMap.put(KeyGenertator.NATIVE, org.uorm.dao.id.NativeGenerator.class);
		generator2IdentifierGeneratorInstMap = new HashMap<String, IdentifierGenerator>();
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGeneratorFactory#createIdentifierGenerator(java.lang.String)
	 */
	@Override
	public IdentifierGenerator createIdentifierGenerator(String keygenertator) throws Exception {
		IdentifierGenerator generator = generator2IdentifierGeneratorInstMap.get(keygenertator);
		if(generator != null) {
			return generator;
		}
		Class<?> cls = generator2ClassMap.get(keygenertator);
		if(cls != null){
			IdentifierGenerator idgen = ( IdentifierGenerator ) cls.newInstance();
			if ( idgen instanceof UUIDGenerator ) {
				//set cust property ID.UUIDGenerator.random
				String prop = PropertyHolderUtil.getProperty("ID.UUIDGenerator.random");
				if ( "false".equals( prop ) ) {
					((UUIDGenerator)idgen).setSimplerandom(false);
				}
			} else if ( idgen instanceof SelectGenerator ) {
				//set cust property
				String prop = PropertyHolderUtil.getProperty("ID.SelectGenerator.tablename");
				if ( prop != null && prop.trim().length() > 0 ) {
					((SelectGenerator)idgen).setTableName(prop);
				}
				prop = PropertyHolderUtil.getProperty("ID.SelectGenerator.namecolumn");
				if ( prop != null && prop.trim().length() > 0 ) {
					((SelectGenerator)idgen).setNameColumn(prop);
				}
				prop = PropertyHolderUtil.getProperty("ID.SelectGenerator.valuecolumn");
				if ( prop != null && prop.trim().length() > 0 ) {
					((SelectGenerator)idgen).setValueColumn(prop);
				}
			}
			generator2IdentifierGeneratorInstMap.put(keygenertator, idgen);
			return idgen;
		}else{
			try{
				cls = Class.forName(keygenertator);
				Object ins = cls.newInstance();
				if ( ins instanceof IdentifierGenerator ) {
					generator2ClassMap.put(keygenertator, cls);
					generator2IdentifierGeneratorInstMap.put(keygenertator, (IdentifierGenerator)ins);
					return (IdentifierGenerator)ins;
				}
			}catch (Exception e) { // $codepro.audit.disable emptyCatchClause
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.uorm.dao.id.IdentifierGeneratorFactory#getIdentifierGeneratorClass(java.lang.String)
	 */
	@Override
	public Class<?> getIdentifierGeneratorClass(String keygenertator) {
		return generator2ClassMap.get(keygenertator);
	}

}
