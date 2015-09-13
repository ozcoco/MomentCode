package me.wangolf.utils;

import me.wangolf.WeComeActivity;

import com.meigao.mgolf.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Wecome_CheckApkUtils
{
	private static WeComeActivity activity;
	
	private static Context con;
	
	private static Dialog dialog;

	/**
	 * 选择提示对话框
	 */
	public static void CheckApi(Context context) {
		activity = (WeComeActivity) context;

		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		String title = "有新的版本";
		View view = View.inflate(context, R.layout.dialog_photo, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		tvdTitle.setText(title);
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("取消");
		Button btok = (Button) view.findViewById(R.id.bt_ok);
		btok.setText("更新");
		btcancle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				dialog.dismiss();
				
				activity.goHome();
				
			}
		});
		
		btok.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				dialog.dismiss();
				
				activity.loadApk();
			}
		});
		
		dialog.show();

	}

	
}
