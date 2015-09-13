package me.wangolf.utils;

import com.meigao.mgolf.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TelUtils {
	private static Context con;
	private static String phoneNum;
	private static Dialog dialog;

	public TelUtils(Context context) {
		super();
	}

	public static void tel(Context context, String phone) {
		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		phoneNum = phone;
		String title = "拨打电话";
		View view = View.inflate(context, R.layout.dialog_phone, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		TextView tvdMsgitle = (TextView) view.findViewById(R.id.dialog_msg);
		tvdMsgitle.setText(phoneNum);
		tvdTitle.setText(title);
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("取消");
		Button btok = (Button) view.findViewById(R.id.bt_ok);
		btok.setText("拨打");
		btcancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
				if (!"".equals(phoneNum)) {
					phoneNum = phoneNum.replace("-", "");
					toPhone(phoneNum);
				}
			}
		});
		dialog.show();
	}

	/**
	 * 手机拨打电功能功能
	 * 
	 * @param phoneNum
	 */
	protected static void toPhone(String phoneNum) {
		Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNum));
		con.startActivity(phoneIntent);

	}
}
