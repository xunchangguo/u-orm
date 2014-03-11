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
package org.uorm.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-24       郭训常            创建<br/>
 */
public class Utils {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * byte chang to ASCI
	 * @param b
	 * @return
	 * @throws IOException
	 */
	public String byteToASCI(byte[] b) throws IOException{
		String tepStr = null;
		String ASCString = null;
		for (int i = 0; i < b.length; i++)
		{
			tepStr = Character.toString((char) b[i]);
			ASCString = ASCString + tepStr;
		}
		return ASCString;
	}

	/**
	 * 生成字母和数字组成的随机字符串
	 * @param len ：生成的字符串长度
	 * @return
	 */
	public static String genRandomNum(int len){
		if(len <= 0)
			return "";
		final int  maxNum = 36;
		int i;  //生成的随机数
		int count = 0; //生成的密码的长度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer rtn = new StringBuffer("");
		Random r = new Random();
		while(count < len){
			//生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(maxNum));  //生成的数最大为36-1
			if (i >= 0 && i < str.length) {
				rtn.append(str[i]);
				count ++;
			}
		}

		return rtn.toString();
	}


	/**
	 * 字符串转换为整型
	 * @param str
	 * @return int
	 */
	public static int strToInt(String str){
		if(str == null || str.trim().length() == 0)
			return 0;
		try{
			return Integer.parseInt(str);
		}catch (Exception e) {
			return 0;
		}
	}
	/**
	 * 字符串转换为整型
	 * @param str
	 * @param radixr
	 * @return
	 */
	public static int strToInt(String str, int radixr){
		if(str == null || str.trim().length() == 0)
			return 0;
		try{
			return Integer.parseInt(str, radixr);
		}catch (Exception e) {
			return 0;
		}
	}
	/**
	 * 字符串转换为整型
	 * @param str
	 * @return Integer
	 */
	public static Integer strToInteger(String str){
		if(str == null || str.trim().length() == 0)
			return null;
		try{
			return new Integer(str);
		}catch (Exception e) {
			return null;
		}
	}
	/**
	 * 字符串转换为长整型
	 * @param str
	 * @return
	 */
	public static long str2long(String str){
		if(str == null || str.trim().length() == 0)
			return 0;
		try{
			return Long.parseLong(str);
		}catch (Exception e) {
			return 0;
		}
	}
	
	public static long str2long(String str, int radixr){
		if(str == null || str.trim().length() == 0)
			return 0;
		try{
			return Long.parseLong(str, radixr);
		}catch (Exception e) {
			return 0;
		}
	}
	
	public static Long strToLong(String str){
		if(str == null || str.trim().length() == 0)
			return null;
		try{
			return Long.parseLong(str);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static Float str2float(String str){
		if(str == null || str.trim().length() == 0)
			return 0f;
		try{
			return Float.parseFloat(str);
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 字符串转换为浮点型
	 * @param str
	 * @return
	 */
	public static Float strToFloat(String str){
		if(str == null || str.trim().length() == 0)
			return null;
		try{
			return Float.parseFloat(str);
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 字符串转换为Boolean型
	 * @param str
	 * @return
	 */
	public static Boolean str2Boolean(String str){
		if(str == null || str.trim().length() == 0)
			return null;
		try{
			return Boolean.parseBoolean(str);
		}catch (Exception e) {
			return null;
		}
	}

	/**
	 * Date类型foramt
	 * @param obj 需要格式化的object,如果不是Date类型，将直接调用该类的toString()方法返回。
	 * @param parten format格式，如果为空，则默认为 "yyyy-MM-dd"
	 * @return String
	 */
	public static String dateFormat(Object obj, String parten){
		if(obj == null)
			return null;
		if(parten == null || parten.trim().length() == 0)
			parten = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(parten);
		if (obj instanceof java.sql.Date) {
			return dateFormat.format(obj);			
		}else if(obj instanceof java.util.Date) {
			return dateFormat.format(obj);	
		}else{
			return obj.toString();
		}
	}

	/**
	 * Decimal类型foramt
	 * @param obj 需要格式化的object,如果不是BigDecimal或Float类型，将直接调用该类的toString()方法返回。
	 * @param parten format格式，如果为空，则默认为 "###,##0.00"
	 * @return String
	 */
	public static String bigDicimalFormat(Object obj, String parten){
		if(obj == null)
			return null;
		if(parten == null || parten.trim().length() == 0)
			parten = "###,##0.00";
		DecimalFormat decimalFormat = new DecimalFormat(parten);
		if(obj instanceof BigDecimal) {
			return decimalFormat.format(obj);
		}else if(obj instanceof Float){
			return decimalFormat.format(obj);
		}else if(obj instanceof Double){
			return decimalFormat.format(obj);
		}else{
			return obj.toString();
		}
	}

	/**
	 * 字符串日期转换成Decimal
	 * @param strbigdecimal:要转换的字符串(Decimal)
	 * @return
	 */
	public static BigDecimal str2BigDicimal(String strbigdecimal){
		if(strbigdecimal == null || strbigdecimal.trim().length() == 0)
			return null;
		BigDecimal bd = null;
		try {
			bd = new BigDecimal(strbigdecimal);
		} catch (Exception e) {
			return null;
		}
		return bd;
	}

	/**
	 * 字符串日期转换成Date日期
	 * @param strDate 要转换的字符串日期
	 * @param parten 日期格式，默认为 "yyyy-MM-dd"
	 * @return Date
	 */
	public static Date str2Date(String strDate, String parten){
		Date dt=null;
		if(parten == null)
			parten = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(parten);
		try{
			dt = sdf.parse(strDate);
		}catch(Exception e){
			return null;
		}
		return dt;
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int n = b[i];
			resultSb.append(hexDigits[n>>> 4 & 0xf] + hexDigits[n& 0xf]);
		}
		return resultSb.toString();
	}

	/**MD5加密*/
	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	/**
	 * IP地址转换成数字
	 * @param strIp
	 * @return
	 */
	public static long ipToLong(String strIp) {  
		long[] ip = new long[4];  
		//先找到IP地址字符串中.的位置  
		String[] strIps = strIp.split("[.]");  
		//将每个.之间的字符串转换成整型  
		ip[0] = Long.parseLong(strIps[0]);  
		ip[1] = Long.parseLong(strIps[1]);  
		ip[2] = Long.parseLong(strIps[2]);  
		ip[3] = Long.parseLong(strIps[3]);  
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];  
	}  

	/**
	 * 将十进制数字转换成127.0.0.1形式的ip地址  
	 * @param longIp
	 * @return
	 */
	public static String longToIP(long longIp) {  
		StringBuffer sb = new StringBuffer();  
		//直接右移24位  
		sb.append(String.valueOf((longIp >>> 24)));  
		sb.append('.');  
		//将高8位置0，然后右移16位  
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));  
		sb.append('.');  
		//将高16位置0，然后右移8位  
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));  
		sb.append('.');  
		//将高24位置0  
		sb.append(String.valueOf((longIp & 0x000000FF)));  
		return sb.toString();  
	} 
	
	public static String genRandomMac(){
		int i;  //生成的随机数
		int count = 0;
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer rtn = new StringBuffer("");
		Random r = new Random();
		while(count < 16){
			//生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(16));
			if (i >= 0 && i < str.length) {
				rtn.append(str[i]);
				count ++;
				if(count % 2 == 0 && count < 16){
					rtn.append(':');
				}
			}
		}

		return rtn.toString();
	}
	
	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * bytes to int
	 * @param bytes
	 * @return
	 */
	public static int bytes2int(byte[] bytes) {
		int result = 0;
		for ( int i = 0; i < 4; i++ ) {
			result = ( result << 8 ) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}

	/**
	 * short to bytes
	 * @param shortValue
	 * @return
	 */
	public static byte[] short2bytes(int shortValue) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ( shortValue >> 8 );
		bytes[1] = (byte) ( ( shortValue << 8 ) >> 8 );
		return bytes;
	}

	/**
	 * int to bytes
	 * @param intValue
	 * @return 
	 */
	public static byte[] int2bytes(int intValue) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ( intValue >> 24 );
		bytes[1] = (byte) ( ( intValue << 8 ) >> 24 );
		bytes[2] = (byte) ( ( intValue << 16 ) >> 24 );
		bytes[3] = (byte) ( ( intValue << 24 ) >> 24 );
		return bytes;
	}

	/**
	 * long to bytes
	 * @param longValue
	 * @return
	 */
	public static byte[] long2bytes(long longValue) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ( longValue >> 56 );
		bytes[1] = (byte) ( ( longValue << 8 ) >> 56 );
		bytes[2] = (byte) ( ( longValue << 16 ) >> 56 );
		bytes[3] = (byte) ( ( longValue << 24 ) >> 56 );
		bytes[4] = (byte) ( ( longValue << 32 ) >> 56 );
		bytes[5] = (byte) ( ( longValue << 40 ) >> 56 );
		bytes[6] = (byte) ( ( longValue << 48 ) >> 56 );
		bytes[7] = (byte) ( ( longValue << 56 ) >> 56 );
		return bytes;
	}

	/**
	 * bytes to long
	 * @param bytes The bytes to interpret.
	 * @return
	 */
	public static long bytes2Long(byte[] bytes) {
		if ( bytes == null ) {
			return 0;
		}
		if ( bytes.length != 8 ) {
			throw new IllegalArgumentException( "Expecting 8 byte values to construct a long" );
		}
		long value = 0;
        for (int i=0; i<8; i++) {
			value = (value << 8) | (bytes[i] & 0xff);
		}
		return value;
	}

	public static void main(String[] args) throws Exception {
		long ip = Utils.ipToLong("127.0.0.1");
		System.out.println(ip);
		System.out.println(Utils.longToIP(2147483647L));
		for(int i = 0; i < 8; i ++)
			System.out.println(Utils.genRandomMac());
	}
}
