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

import java.net.InetAddress;
import java.util.UUID;

import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-30       郭训长            创建<br/>
 */
public class UUIDHelper {
	private static long mostSignificantBits;
	private static final byte[] ADDRESS_BYTES;
	private static final int ADDRESS_INT;
	private static final String ADDRESS_HEX_STRING;

	// JVM identifier ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final byte[] JVM_IDENTIFIER_BYTES;
	private static final int JVM_IDENTIFIER_INT;
	private static final String JVM_IDENTIFIER_HEX_STRING;
	
	static {
		byte[] address;
		try {
			address = InetAddress.getLocalHost().getAddress();
		}
		catch ( Exception e ) {
			address = new byte[4];
		}
		ADDRESS_BYTES = address;
		ADDRESS_INT = Utils.bytes2int( ADDRESS_BYTES );
		ADDRESS_HEX_STRING = format( ADDRESS_INT );
		
		JVM_IDENTIFIER_INT = (int) ( System.currentTimeMillis() >>> 8 );
		JVM_IDENTIFIER_BYTES = Utils.int2bytes( JVM_IDENTIFIER_INT );
		JVM_IDENTIFIER_HEX_STRING = format( JVM_IDENTIFIER_INT );
		// generate the "most significant bits" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		byte[] hiBits = new byte[8];
		// use address as first 32 bits (8 * 4 bytes)
		System.arraycopy( getAddressBytes(), 0, hiBits, 0, 4 );
		// use the "jvm identifier" as the next 32 bits
		System.arraycopy( getJvmIdentifierBytes(), 0, hiBits, 4, 4 );
		// set the version (rfc term) appropriately
		hiBits[6] &= 0x0f;
		hiBits[6] |= 0x10;

		mostSignificantBits = Utils.bytes2Long( hiBits );
	}
	
	public static UUID generateUUID(boolean simplerandom) {
		if(simplerandom){
			return UUID.randomUUID();
		}else{
			long leastSignificantBits = generateLeastSignificantBits( System.currentTimeMillis() );
			return new UUID( mostSignificantBits, leastSignificantBits );
		}
	}
	
	public static long generateLeastSignificantBits(long seed) {
		byte[] loBits = new byte[8];
		short hiTime = (short) ( seed >>> 32 );
		int loTime = (int) seed;
		System.arraycopy( Utils.short2bytes( hiTime ), 0, loBits, 0, 2 );
		System.arraycopy( Utils.int2bytes( loTime ), 0, loBits, 2, 4 );
		System.arraycopy( getCountBytes(), 0, loBits, 6, 2 );
		loBits[0] &= 0x3f;
		loBits[0] |= ((byte)2 << (byte)6);
		return Utils.bytes2Long( loBits );
	}
	
	public static byte[] getAddressBytes() {
		return ADDRESS_BYTES;
	}

	public static int getAddressInt() {
		return ADDRESS_INT;
	}

	public static String getAddressHexString() {
		return ADDRESS_HEX_STRING;
	}

	public static byte[] getJvmIdentifierBytes() {
		return JVM_IDENTIFIER_BYTES;
	}

	public static int getJvmIdentifierInt() {
		return JVM_IDENTIFIER_INT;
	}

	public static String getJvmIdentifierHexString() {
		return JVM_IDENTIFIER_HEX_STRING;
	}


	// counter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private static short counter = (short) 0;

	/**
	 * Unique in a millisecond for this JVM instance (unless there are > Short.MAX_VALUE instances created in a
	 * millisecond)
	 */
	public static short getCountShort() {
		synchronized ( UUIDHelper.class ) {
			if ( counter < 0 ) {
				counter = 0;
			}
			return counter++;
		}
	}

	public static byte[] getCountBytes() {
		return Utils.short2bytes( getCountShort() );
	}
	// Helper methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public static String format(int value) {
		final String formatted = Integer.toHexString( value );
		StringBuffer buf = new StringBuffer( "00000000".intern() );
		buf.replace( 8 - formatted.length(), 8, formatted );
		return buf.toString();
	}
	
	public static String format(short value) {
		String formatted = Integer.toHexString( value );
		StringBuffer buf = new StringBuffer( "0000" );
		buf.replace( 4 - formatted.length(), 4, formatted );
		return buf.toString();
	}

}
