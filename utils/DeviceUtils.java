package me.wangolf.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceUtils
{

	/** 
	* @Title: getDeviceIMEI 
	* @Description: 获取设备IMEI号
	* @param @param context
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getDeviceIMEI(Context context)
	{

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String imei = telephonyManager.getDeviceId();

		return imei;

	}

	/** 
	* @Title: getPhoneNumber 
	* @Description: 获取设备电话号码
	* @param @param context
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getPhoneNumber(Context context)
	{

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String phoneNum = telephonyManager.getLine1Number();

		return phoneNum;

	}

	
	
	/** 
	* @Title: getDeviceSoftwareVersion 
	* @Description: 获取设备系统版本号
	* @param @param context
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getDeviceSoftwareVersion(Context context)
	{

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String deviceSoftwareVersion = telephonyManager
				.getDeviceSoftwareVersion();

		return deviceSoftwareVersion;

	}

	
	/** 
	* @Title: getSimSerialNumber 
	* @Description: 获取SIM卡唯一编号ID 
	* @param @param context
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getSimSerialNumber(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String simSerialNumber = telephonyManager.getSimSerialNumber();

		return simSerialNumber;

	}

	
	
	/** 
	* @Title: getSubscriberId 
	* @Description: 获取到客户ID，即IMSI
	* @param @param context
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getSubscriberId(Context context)
	{

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String subscriberId = telephonyManager.getSubscriberId();

		return subscriberId;

	}

}
