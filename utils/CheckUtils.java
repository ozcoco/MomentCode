package me.wangolf.utils;


public class CheckUtils {

	/**
	 * 
	 * @param text
	 * @return true 为空
	 * @return false 不为空空
	 * 
	 **/
	public static boolean checkEmpty(String text) {
		if(text==null||"".equals(text)||text.isEmpty()||text.length()<=0){
			return true;
		}
		return false;
	}
}
