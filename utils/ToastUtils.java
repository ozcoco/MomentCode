package me.wangolf.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	public static void showInfo(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void showInfo(Context context,int noResult){
		String  msg=context.getString(noResult);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
