package com.charset;

import java.io.UnsupportedEncodingException;

/**
 * 字符集转换工具
 * @author 陈捷
 *
 */
public class CharsetUtils {
	
	/**
	 * 转换字符集
	 * @param str 需要转换的字符串
	 * @param charsetName 字符集名称
	 * @return
	 */
	public static String Convert(String str,String charsetName){
		byte[] byteStr;
		String iso=null;
		String newStr=null;
		try {
			byteStr=str.getBytes(charsetName);
			iso = new String(byteStr,"ISO-8859-1");
		    newStr=new String(iso.getBytes("ISO-8859-1"),charsetName); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    
		return newStr;
	}
}
