package me.wangolf.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

public class WeatherUtils {

	private static Context context;
	// private String BaseUrl="http://weather.api.114la.com/1901/101190101.txt";
	private static String BaseUrl = "http://weather.api.114la.com/";

	public WeatherUtils(Context context) {
		this.context = context;
	}

	/**
	 * 得到城市编码
	 * 
	 * @param cityname
	 * @return
	 */
	public static String getcitycode(String cityname, Context context) {

		String code = "";
		Properties property = new Properties();
		InputStream in = null;
		try {
			
			in = context.getAssets().open("weather_city_code.properties");
			
			property.load(in);
			
			code = (String) property.get(cityname);
			
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return code;
	}

	/**
	 * 截取索引3-6（含6）
	 */
	public static String getShortCode(String code) {
		String scode = "";
		scode = code.substring(3, 7);
		return scode;
	}

	/**
	 * 天气路径
	 * 
	 * @param cityname
	 *            城市名称
	 * @return
	 */
	public static String getWeatherUrl(String cityname, Context context)
	{
		String path = "";
		String code = getcitycode(cityname, context);
		String scode = getShortCode(code);
		path = BaseUrl + scode + "/" + code + ".txt";
		// LogUtils.i("weatherUrl=", path);
		return path;
	}
}
